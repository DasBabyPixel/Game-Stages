package de.dasbabypixel.gamestages.common.data.server;

import org.jspecify.annotations.NullMarked;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

@NullMarked
public class StagesCache {
    private static final Logger LOGGER = Logger.getLogger(StagesCache.class.getName());
    private final GlobalServerState globalServerState;
    private final StagesFileProvider fileProvider;
    private final ReentrantLock lock = new ReentrantLock();
    private final HashMap<StagesFileProvider.Key, Entry> entryMap = new HashMap<>();
    private final Map<Set<UUID>, CompositeEntry> compositeMap = new HashMap<>();

    public StagesCache(GlobalServerState globalServerState) {
        this.globalServerState = globalServerState;
        this.fileProvider = globalServerState.stagesFileProvider();
    }

    public void release(CompositeStages stages) {
        lock.lock();
        try {
            Set<UUID> uuids = new HashSet<>();
            for (var dependency : stages.dependencies()) {
                uuids.add(dependency.key().uuid());
            }
            uuids = Set.copyOf(uuids);
            LOGGER.log(Level.INFO, "Release {0}", uuids);
            var entry = Objects.requireNonNull(compositeMap.get(uuids));
            if (entry.referenceCount.decrementAndGet() == 0) {
                LOGGER.log(Level.INFO, "Delete composite");
                compositeMap.remove(uuids);
                for (var dependency : stages.dependencies()) {
                    release(dependency);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public void releaseComposite(Set<UUID> uuids) {
        uuids = Set.copyOf(uuids);
        var stages = Objects.requireNonNull(compositeMap.get(uuids));
        release(stages.stages);
    }

    public CompositeStages requireComposite(Set<UUID> uuids) {
        uuids = Objects.requireNonNull(Set.copyOf(uuids));
        lock.lock();
        LOGGER.log(Level.INFO, "Require {0}", uuids);
        try {
            if (compositeMap.containsKey(uuids)) {
                var entry = Objects.requireNonNull(compositeMap.get(uuids));
                entry.referenceCount.incrementAndGet();
                return entry.stages;
            }
            LOGGER.log(Level.INFO, "Creating new composite");
            var players = new HashSet<PlayerStages>();
            for (var uuid : uuids) {
                var player = requirePlayer(uuid);
                players.add(player);
            }
            var stages = new CompositeStages(GlobalServerState.currentManager(), players);
            stages.recompileAll(GlobalServerState.currentManager());
            var entry = new CompositeEntry(stages);
            compositeMap.put(uuids, entry);
            return stages;
        } finally {
            lock.unlock();
        }
    }

    public PlayerStages requirePlayer(UUID uuid) {
        return (PlayerStages) require(StagesFileProvider.player(uuid));
    }

    public TeamStages requireTeam(UUID uuid) {
        return (TeamStages) require(StagesFileProvider.team(uuid));
    }

    public ServerStages require(StagesFileProvider.Key key) {
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

    public void release(ServerStages stages) {
        lock.lock();
        try {
            var entry = Objects.requireNonNull(entryMap.get(stages.key()));
            if (entry.referenceCount.decrementAndGet() == 0) {
                entryMap.remove(stages.key());
                stages.unload();
                LOGGER.log(Level.INFO, "Unload {0}", stages.key());
            }
        } finally {
            lock.unlock();
        }
    }

    private ServerStages load(StagesFileProvider.Key key) throws IOException {
        LOGGER.log(Level.INFO, "Loading {0}", key);
        var file = globalServerState.stagesFileProvider().readStages(key);
        switch (key.type()) {
            case "player" -> {
                return new PlayerStages(fileProvider, this, key, (StagesFileProvider.PlayerStagesFile) file);
            }
            case "team" -> {
                return new TeamStages(fileProvider, key, (StagesFileProvider.TeamStagesFile) file);
            }
            default -> throw new UnsupportedOperationException();
        }
    }

    private static class CompositeEntry {
        private final AtomicInteger referenceCount = new AtomicInteger(1);
        private final CompositeStages stages;

        private CompositeEntry(CompositeStages stages) {
            this.stages = stages;
        }

        @Override
        public String toString() {
            return "Entry{" + "referenceCount=" + referenceCount.get() + ", stages=" + stages + '}';
        }
    }

    private static class Entry {
        private final AtomicInteger referenceCount = new AtomicInteger();
        private final ServerStages stages;

        private Entry(ServerStages stages) {
            this.stages = stages;
        }

        @Override
        public String toString() {
            return "Entry{" + "referenceCount=" + referenceCount.get() + ", stages=" + stages + '}';
        }
    }
}
