package de.dasbabypixel.gamestages.common.data.server;

import de.dasbabypixel.gamestages.common.data.GameStage;
import org.jspecify.annotations.Nullable;

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

public class StagesFileProvider {
    private static final Logger LOGGER = Logger.getLogger(StagesFileProvider.class.getName());
    private final ExecutorService writeExecutor = Executors.newSingleThreadExecutor();
    private final ReentrantLock lock = new ReentrantLock();
    /**
     * Cache for stages that are currently being written. Fetching from the file would give incorrect results.
     */
    private final Map<Key, Set<GameStage>> stagesBeingWritten = new HashMap<>();
    private final Path directory;

    public StagesFileProvider(Path directory) {
        this.directory = directory;
    }

    public static Key player(UUID uuid) {
        return new Key("player", uuid);
    }

    public static Key team(UUID uuid) {
        return new Key("team", uuid);
    }

    public Set<GameStage> readStages(Key key) throws IOException {
        lock.lock();
        try {
            if (stagesBeingWritten.containsKey(key)) {
                return stagesBeingWritten.get(key);
            }
            var stages = tryReadStages(stageFile(key, "txt"));
            if (stages != null) return stages;
            stages = tryReadStages(stageFile(key, "txt.bak"));
            if (stages != null) return stages;
            throw new IOException("GameStages were corrupted");
        } finally {
            lock.unlock();
        }
    }

    private @Nullable Set<GameStage> tryReadStages(Path stageFile) throws IOException {
        if (!Files.exists(stageFile)) return Set.of();
        try (var reader = Files.newBufferedReader(stageFile, StandardCharsets.UTF_8)) {
            var stages = new ArrayList<GameStage>();
            var checksum = new CRC32C();
            for (var line = reader.readLine(); ; line = reader.readLine()) {
                if (line == null) return null;
                if (line.isEmpty()) break;
                checksum.update(line.getBytes(StandardCharsets.UTF_8));
                stages.add(new GameStage(line));
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

            return Set.copyOf(stages);
        }
    }

    public void writeStages(Key key, Set<GameStage> stages) {
        stages = Set.copyOf(stages);
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
                    var writer = Files.newBufferedWriter(stageFileTmp, StandardCharsets.UTF_8, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
                    var content = String.join("\n", s.stream().map(GameStage::name).toList());
                    writer.write(content);
                    writer.newLine();
                    writer.newLine();
                    var checksum = new CRC32C();
                    s
                            .stream()
                            .map(GameStage::name)
                            .map(a -> a.getBytes(StandardCharsets.UTF_8))
                            .forEach(checksum::update);
                    writer.write(Integer.toHexString((int) (checksum.getValue() & 0xFFFFFFFFL)));
                    writer.close();

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

    private Path stageFile(Key key, String suffix) {
        return directory.resolve(key.type()).resolve(key.uuid().toString() + "." + suffix);
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

    public record Key(String type, UUID uuid) {
    }
}
