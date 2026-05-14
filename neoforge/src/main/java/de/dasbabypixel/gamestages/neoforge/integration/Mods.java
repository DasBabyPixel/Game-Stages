package de.dasbabypixel.gamestages.neoforge.integration;

import de.dasbabypixel.gamestages.neoforge.NeoForgeInstances;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public class Mods {
    public static final Mod KUBEJS = mod("kubejs");
    public static final Mod PROBEJS = mod("probejs");
    public static final Mod JEI = mod("jei");
    public static final Mod FTB_TEAMS = mod("ftbteams");

    public static Mod mod(String id) {
        return new Mod(id, Objects.requireNonNull(NeoForgeInstances.modProvider));
    }
}
