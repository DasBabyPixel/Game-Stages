package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.jei;


import de.dasbabypixel.gamestages.common.event.EventType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class JEIIntegration {
    public static final EventType<InitJEISupportEvent> INIT_JEI_SUPPORT_EVENT = EventType.create();
    public static boolean isReloading = false;

    public record InitJEISupportEvent() {
    }
}
