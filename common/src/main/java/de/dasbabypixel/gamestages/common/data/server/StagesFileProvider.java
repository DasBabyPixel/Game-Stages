package de.dasbabypixel.gamestages.common.data.server;

import de.dasbabypixel.gamestages.common.data.GameStage;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32C;
import java.util.zip.Checksum;

public class StagesFileProvider {
    private static final @NonNull Logger LOGGER = Logger.getLogger(StagesFileProvider.class.getName());
    private final @NonNull ExecutorService writeExecutor = Executors.newSingleThreadExecutor();
    private final @NonNull ReentrantLock lock = new ReentrantLock();
    /**
     * Cache for stages that are currently being written. Fetching from the file would give incorrect results.
     */
    private final @NonNull Map<Key, StagesFile> stagesBeingWritten = new HashMap<>();
    private final @NonNull Path directory;

    public StagesFileProvider(@NonNull Path directory) {
        this.directory = directory;
    }

    public StagesFile readStages(@NonNull Key key) throws IOException {
        lock.lock();
        try {
            if (stagesBeingWritten.containsKey(key)) {
                return stagesBeingWritten.get(key);
            }
            var provider = providerFor(key);
            var stages = tryReadStages(provider, stageFile(key, "txt"));
            if (stages != null) return stages;
            stages = tryReadStages(provider, stageFile(key, "txt.bak"));
            if (stages != null) return stages;
            throw new IOException("GameStages were corrupted");
        } finally {
            lock.unlock();
        }
    }

    private Provider providerFor(@NonNull Key key) {
        return switch (key.type()) {
            case "player" -> new Provider.Player();
            case "team" -> new Provider.Team();
            default -> throw new UnsupportedOperationException();
        };
    }

    private @Nullable StagesFile tryReadStages(@NonNull Provider provider, @NonNull Path stageFile) throws IOException {
        if (!Files.exists(stageFile)) return provider.defaultInstance();
        try (var reader = Files.newBufferedReader(stageFile, Objects.requireNonNull(StandardCharsets.UTF_8))) {
            var checksum = new CRC32C();

            var file = provider.emptyInstance();
            try {
                file.read(reader, checksum);
            } catch (CorruptedException e) {
                return null;
            }

            var line = reader.readLine();
            if (line == null) return null;
            try {
                var c = Integer.parseUnsignedInt(line, 16);
                if (c != (int) (checksum.getValue() & 0xFFFFFFFFL)) return null;
            } catch (NumberFormatException e) {
                return null;
            }
            if (reader.readLine() != null) return null;

            return file;
        }
    }

    public void writeStages(@NonNull Key key, @NonNull StagesFile stages) {
        lock.lock();
        try {
            stagesBeingWritten.put(key, stages);
        } finally {
            lock.unlock();
        }
        writeExecutor.submit(() -> {
            lock.lock();
            try {
                var s = stagesBeingWritten.remove(key);
                if (s == null) return;
                var stageFileTmp = stageFile(key, "txt.tmp");
                try {
                    Files.createDirectories(stageFileTmp.getParent());
                    try (var writer = Files.newBufferedWriter(stageFileTmp, Objects.requireNonNull(StandardCharsets.UTF_8), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)) {
                        var checksum = new CRC32C();
                        stages.write(writer, checksum);

                        writer.write(Integer.toHexString((int) (checksum.getValue() & 0xFFFFFFFFL)));
                    }

                    Files.copy(stageFileTmp, stageFile(key, "txt.bak"), StandardCopyOption.REPLACE_EXISTING);
                    Files.move(stageFileTmp, stageFile(key, "txt"), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
                } catch (IOException e) {
                    // We could crash here, but likely there will be another write task, which might succeed
                    // We could also implement a retry strategy?
                    LOGGER.log(Level.SEVERE, "Failed to write stages file", e);
                }
            } finally {
                lock.unlock();
            }
        });
    }

    private @NonNull Path stageFile(@NonNull Key key, @NonNull String suffix) {
        return Objects.requireNonNull(Objects
                .requireNonNull(directory.resolve(key.type()))
                .resolve(key.uuid() + "." + suffix));
    }

    public void shutdown() throws IOException {
        writeExecutor.shutdown();
        while (true) {
            try {
                if (!writeExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    throw new IOException("Failed to write stages file");
                }
                return;
            } catch (InterruptedException ignored) {
            }
        }
    }

    public static Key player(@NonNull UUID uuid) {
        return new Key("player", uuid);
    }

    public static Key team(@NonNull UUID uuid) {
        return new Key("team", uuid);
    }

    public interface Provider {
        @NonNull StagesFile defaultInstance();

        @NonNull StagesFile emptyInstance();

        class Player implements Provider {
            @Override
            public @NonNull StagesFile defaultInstance() {
                return new PlayerStagesFile(Set.of(), null);
            }

            @Override
            public @NonNull StagesFile emptyInstance() {
                return new PlayerStagesFile();
            }
        }

        class Team implements Provider {
            @Override
            public @NonNull StagesFile defaultInstance() {
                return new TeamStagesFile(Set.of(), Set.of());
            }

            @Override
            public @NonNull StagesFile emptyInstance() {
                return new TeamStagesFile();
            }
        }
    }

    public record Key(@NonNull String type, @NonNull UUID uuid) {
    }

    public static class StagesFile {
        private static final int LOCAL_VERSION = 1;
        private @NonNull Set<@NonNull GameStage> stages;

        public StagesFile(@NonNull Set<@NonNull GameStage> stages) {
            this.stages = Objects.requireNonNull(Set.copyOf(stages));
        }

        private StagesFile() {
            stages = Set.of();
        }

        public void write(@NonNull BufferedWriter writer, @NonNull Checksum checksum) throws IOException {
            writeLocalVersion(writer, checksum, LOCAL_VERSION);
            for (var stage : stages) {
                writer.write(stage.name());
                writer.newLine();
                checksum.update(stage.name().getBytes(Objects.requireNonNull(StandardCharsets.UTF_8)));
                checksum.update(0);
            }
            writer.newLine();
            checksum.update(1);
        }

        public void read(@NonNull BufferedReader reader, @NonNull Checksum checksum) throws IOException, CorruptedException {
            readLocalVersion(reader, checksum, LOCAL_VERSION);
            var stages = new HashSet<GameStage>();
            for (var line = reader.readLine(); ; line = reader.readLine()) {
                if (line == null) throw corrupted();
                if (line.isEmpty()) break;
                stages.add(new GameStage(line));
                checksum.update(line.getBytes(Objects.requireNonNull(StandardCharsets.UTF_8)));
                checksum.update(0);
            }
            checksum.update(1);
            this.stages = Objects.requireNonNull(Set.copyOf(stages));
        }

        protected @NonNull CorruptedException corrupted() {
            return new CorruptedException();
        }

        public Set<GameStage> stages() {
            return stages;
        }

        protected void writeLocalVersion(@NonNull BufferedWriter writer, @NonNull Checksum checksum, int version) throws IOException {
            var str = Integer.toString(version);
            writer.write(str);
            writer.newLine();
            checksum.update(str.getBytes(Objects.requireNonNull(StandardCharsets.UTF_8)));
            checksum.update(0);
        }

        protected void readLocalVersion(@NonNull BufferedReader reader, @NonNull Checksum checksum, int expect) throws IOException, CorruptedException {
            var line = reader.readLine();
            if (line == null) throw corrupted();
            try {
                var version = Integer.parseInt(line);
                if (version != expect) throw corrupted();
                checksum.update(line.getBytes(Objects.requireNonNull(StandardCharsets.UTF_8)));
                checksum.update(0);
            } catch (NumberFormatException e) {
                throw corrupted();
            }
        }
    }

    public static class PlayerStagesFile extends StagesFile {
        private static final int LOCAL_VERSION = 1;
        private @Nullable UUID teamId;

        public PlayerStagesFile(@NonNull Set<@NonNull GameStage> stages, @Nullable UUID teamId) {
            super(stages);
            this.teamId = teamId;
        }

        public PlayerStagesFile() {
        }

        @Override
        public void write(@NonNull BufferedWriter writer, @NonNull Checksum checksum) throws IOException {
            super.write(writer, checksum);
            writeLocalVersion(writer, checksum, LOCAL_VERSION);
            var line = teamId == null ? "" : Objects.requireNonNull(teamId.toString());
            checksum.update(line.getBytes(Objects.requireNonNull(StandardCharsets.UTF_8)));
            writer.write(line);
            writer.newLine();
        }

        @Override
        public void read(@NonNull BufferedReader reader, @NonNull Checksum checksum) throws IOException, CorruptedException {
            super.read(reader, checksum);
            readLocalVersion(reader, checksum, LOCAL_VERSION);
            var line = reader.readLine();
            if (line == null) throw corrupted();
            checksum.update(line.getBytes(Objects.requireNonNull(StandardCharsets.UTF_8)));
            teamId = line.isEmpty() ? null : UUID.fromString(line);
        }

        public @Nullable UUID teamId() {
            return teamId;
        }
    }

    public static class TeamStagesFile extends StagesFile {
        private static final int LOCAL_VERSION = 1;
        private @NonNull Set<@NonNull UUID> players;

        public TeamStagesFile(@NonNull Set<GameStage> stages, @NonNull Set<UUID> players) {
            super(stages);
            this.players = Objects.requireNonNull(Set.copyOf(players));
        }

        public TeamStagesFile() {
            players = Set.of();
        }

        @Override
        public void write(@NonNull BufferedWriter writer, @NonNull Checksum checksum) throws IOException {
            super.write(writer, checksum);
            writeLocalVersion(writer, checksum, LOCAL_VERSION);
            for (var player : players) {
                writer.write(player.toString());
                writer.newLine();
                checksum.update(Objects
                        .requireNonNull(player.toString())
                        .getBytes(Objects.requireNonNull(StandardCharsets.UTF_8)));
                checksum.update(0);
            }
            writer.newLine();
            checksum.update(1);
        }

        @Override
        public void read(@NonNull BufferedReader reader, @NonNull Checksum checksum) throws IOException, CorruptedException {
            super.read(reader, checksum);
            readLocalVersion(reader, checksum, LOCAL_VERSION);
            var players = new HashSet<UUID>();
            for (var line = reader.readLine(); ; line = reader.readLine()) {
                if (line == null) throw corrupted();
                if (line.isEmpty()) break;
                players.add(UUID.fromString(line));
                checksum.update(line.getBytes(Objects.requireNonNull(StandardCharsets.UTF_8)));
                checksum.update(0);
            }
            checksum.update(1);
            this.players = Objects.requireNonNull(Set.copyOf(players));
        }

        public Set<UUID> players() {
            return players;
        }
    }

    public static class CorruptedException extends Exception {
    }
}
