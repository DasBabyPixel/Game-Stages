package de.dasbabypixel.gamestages.common.data.server;

import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StagesCache {
    private static final @NonNull Logger LOGGER = Logger.getLogger(StagesCache.class.getName());
    private final @NonNull ServerGameStageManager manager;
    private final @NonNull ReentrantLock lock = new ReentrantLock();
    private final @NonNull HashMap<StagesFileProvider.Key, Entry> entryMap = new HashMap<>();
    private final @NonNull Map<Set<UUID>, CompositeEntry> compositeMap = new HashMap<>();

    public StagesCache(@NonNull ServerGameStageManager manager) {
        this.manager = manager;
    }

    public void release(@NonNull CompositeStages stages) {
        lock.lock();
        try {
            Set<UUID> uuids = new HashSet<>();
            for (var dependency : stages.dependencies()) {
                uuids.add(dependency.key().uuid());
            }
            uuids = Set.copyOf(uuids);
            System.out.println("Release " + uuids);
            var entry = Objects.requireNonNull(compositeMap.get(uuids));
            if (entry.referenceCount.decrementAndGet() == 0) {
                System.out.println("Empty composite");
                compositeMap.remove(uuids);
                for (var dependency : stages.dependencies()) {
                    release(dependency);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public void releaseComposite(@NonNull Set<@NonNull UUID> uuids) {
        uuids = Set.copyOf(uuids);
        var stages = Objects.requireNonNull(compositeMap.get(uuids));
        release(stages.stages);
    }

    public @NonNull CompositeStages requireComposite(@NonNull Set<@NonNull UUID> uuids) {
        uuids = Objects.requireNonNull(Set.copyOf(uuids));
        lock.lock();
        System.out.println("Require " + uuids);
        try {
            if (compositeMap.containsKey(uuids)) {
                var entry = Objects.requireNonNull(compositeMap.get(uuids));
                entry.referenceCount.incrementAndGet();
                return entry.stages;
            }
            System.out.println("New composite");
            var players = new HashSet<PlayerStages>();
            for (var uuid : uuids) {
                var player = requirePlayer(uuid);
                players.add(player);
            }
            var stages = new CompositeStages(players);
            var entry = new CompositeEntry(stages);
            compositeMap.put(uuids, entry);
            return stages;
        } finally {
            lock.unlock();
        }
    }

    public @NonNull PlayerStages requirePlayer(@NonNull UUID uuid) {
        return (PlayerStages) require(StagesFileProvider.player(uuid));
    }

    public @NonNull TeamStages requireTeam(@NonNull UUID uuid) {
        return (TeamStages) require(StagesFileProvider.team(uuid));
    }

    public @NonNull ServerStages require(StagesFileProvider.@NonNull Key key) {
        lock.lock();
        try {
            if (entryMap.containsKey(key)) {
                var entry = Objects.requireNonNull(entryMap.get(key));
                entry.referenceCount.incrementAndGet();
                return entry.stages;
            }
            try {
                var entry = new Entry(load(key));
                entry.referenceCount.incrementAndGet();
                entry.stages.load();
                entryMap.put(key, entry);
                return entry.stages;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } finally {
            lock.unlock();
        }
    }

    public void shutdown() {
        lock.lock();
        try {
            if (!entryMap.isEmpty()) {
                LOGGER.log(Level.WARNING, "Dangling player/team references", new RuntimeException());
            }
            if (!compositeMap.isEmpty()) {
                LOGGER.log(Level.WARNING, "Dangling composite references", new RuntimeException());
            }
        } finally {
            lock.unlock();
        }
    }

    public void release(@NonNull ServerStages stages) {
        lock.lock();
        try {
            var entry = Objects.requireNonNull(entryMap.get(stages.key()));
            if (entry.referenceCount.decrementAndGet() == 0) {
                entryMap.remove(stages.key());
                stages.unload();
                System.out.println("Unload " + stages.key());
            }
        } finally {
            lock.unlock();
        }
    }

    private ServerStages load(StagesFileProvider.@NonNull Key key) throws IOException {
        System.out.println("Load " + key);
        var file = manager.stagesFileProvider().readStages(key);
        switch (key.type()) {
            case "player" -> {
                return new PlayerStages(manager, key, (StagesFileProvider.PlayerStagesFile) file);
            }
            case "team" -> {
                return new TeamStages(manager, key, (StagesFileProvider.TeamStagesFile) file);
            }
            default -> throw new UnsupportedOperationException();
        }
    }

    private static class CompositeEntry {
        private final @NonNull AtomicInteger referenceCount = new AtomicInteger(1);
        private final @NonNull CompositeStages stages;

        private CompositeEntry(@NonNull CompositeStages stages) {
            this.stages = stages;
        }

        @Override
        public String toString() {
            return "Entry{" + "referenceCount=" + referenceCount.get() + ", stages=" + stages + '}';
        }
    }

    private static class Entry {
        private final @NonNull AtomicInteger referenceCount = new AtomicInteger();
        private final @NonNull ServerStages stages;

        private Entry(@NonNull ServerStages stages) {
            this.stages = stages;
        }

        @Override
        public String toString() {
            return "Entry{" + "referenceCount=" + referenceCount.get() + ", stages=" + stages + '}';
        }
    }
}
