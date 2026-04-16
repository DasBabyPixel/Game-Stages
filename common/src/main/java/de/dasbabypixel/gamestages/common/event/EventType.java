package de.dasbabypixel.gamestages.common.event;

import org.jspecify.annotations.NullMarked;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@NullMarked
public class EventType<Event> {
    private final Set<EventListener<Event>> listeners = new HashSet<>();
    private Consumer<Event> executor = ignored -> {
    };

    public Event call(Event event) {
        executor.accept(event);
        return event;
    }

    public void addListener(Consumer<Event> listener) {
        addListener(0, listener);
    }

    public void addListener(int order, Consumer<Event> listener) {
        this.listeners.add(new EventListener<>(order, listener));
        recompile();
    }

    @SuppressWarnings({"DataFlowIssue", "unchecked"})
    private void recompile() {
        var a = listeners.stream().sorted(Comparator.comparingInt(EventListener::order)).toArray(EventListener[]::new);
        if (a.length == 0) {
            executor = ignored -> {
            };
        } else {
            executor = event -> {
                for (var eventListener : a) {
                    eventListener.executor().accept(event);
                }
            };
        }
    }

    public static <Event> EventType<Event> create() {
        return new EventType<>();
    }
}
