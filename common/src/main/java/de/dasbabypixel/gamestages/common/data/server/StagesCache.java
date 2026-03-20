package de.dasbabypixel.gamestages.common.data.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StagesCache {
    private static final Logger LOGGER = Logger.getLogger(StagesCache.class.getName());
    private final ServerGameStageManager manager;
    private final ReentrantLock lock = new ReentrantLock();
    private final Map<StagesFileProvider.Key, Entry> entryMap = new HashMap<>();

    public StagesCache(ServerGameStageManager manager) {
        this.manager = manager;
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
            var entry = entryMap.computeIfAbsent(key, key1 -> {
                try {
                    return new Entry(load(key1));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            entry.referenceCount.incrementAndGet();
            return entry.stages;
        } finally {
            lock.unlock();
        }
    }

    public void shutdown() {
        lock.lock();
        try {
            if (!entryMap.isEmpty()) {
                LOGGER.log(Level.WARNING, "Dangling references", new RuntimeException());
            }
        } finally {
            lock.unlock();
        }
    }

    public void release(ServerStages stages) {
        lock.lock();
        try {
            var entry = entryMap.get(stages.key());
            if (entry.referenceCount.decrementAndGet() == 0) {
                entryMap.remove(stages.key());
            }
        } finally {
            lock.unlock();
        }
    }

    private ServerStages load(StagesFileProvider.Key key) throws IOException {
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
