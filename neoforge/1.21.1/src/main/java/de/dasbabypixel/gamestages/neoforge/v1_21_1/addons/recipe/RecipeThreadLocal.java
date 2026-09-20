package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe;

import de.dasbabypixel.gamestages.common.data.BaseStages;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@NullMarked
public class RecipeThreadLocal {
    private static final Logger LOGGER = Objects.requireNonNull(LoggerFactory.getLogger(RecipeThreadLocal.class.getName()));
    private static final StackWalker WALKER = Objects.requireNonNull(StackWalker.getInstance());
    private static final Set<String> SEEN_STACKS = ConcurrentHashMap.newKeySet();
    private static final ThreadLocal<RecipeThreadLocal> THREAD_LOCAL = ThreadLocal.withInitial(RecipeThreadLocal::new);
    private final Deque<Entry> entries = new ArrayDeque<>();

    public static RecipeThreadLocal get() {
        return THREAD_LOCAL.get();
    }

    @SuppressWarnings("DataFlowIssue")
    private static String captureRelevantStack() {
        return WALKER.walk(stream -> stream
                .dropWhile(frame -> frame.getClassName().equals(RecipeThreadLocal.class.getName()))
                .dropWhile(frame -> frame.getClassName().equals(RecipeManager.class.getName()))
                .dropWhile(frame -> frame.getClassName().equals(RecipeManager.class.getName() + "$1"))
                .limit(10)
                .map(frame -> String.format("\tat %s.%s(%s:%d)", frame.getClassName(), frame.getMethodName(), frame.getFileName() != null ? frame.getFileName() : "Unknown Source", frame.getLineNumber()))
                .collect(Collectors.joining("\n")));
    }

    public @Nullable BaseStages stagesOrRecord() {
        if (entries.isEmpty()) {
            // Called from unsupported code
            var stack = captureRelevantStack();
            if (SEEN_STACKS.add(stack)) {
                LOGGER.warn("Stack entry: \"{}\"", stack);
                LOGGER.warn("GameStages doesn't support a recipe type", new Exception("dump stack"));
            }
            return null;
        }
        return entries.getLast().stages();
    }

    public void pushStages(@Nullable BaseStages stages) {
        entries.addLast(new Entry(stages));
    }

    public void popStages() {
        entries.removeLast();
    }

    private record Entry(@Nullable BaseStages stages) {
    }
}
