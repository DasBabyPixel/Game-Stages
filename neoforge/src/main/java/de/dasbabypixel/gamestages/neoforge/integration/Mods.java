package de.dasbabypixel.gamestages.neoforge.integration;

import de.dasbabypixel.gamestages.neoforge.NeoForgeInstances;

import java.util.Objects;

public class Mods {
    public static final Mod KUBEJS = new Mod("kubejs", Objects.requireNonNull(NeoForgeInstances.modProvider));
    public static final Mod PROBEJS = new Mod("probejs", Objects.requireNonNull(NeoForgeInstances.modProvider));
    public static final Mod JEI = new Mod("jei", Objects.requireNonNull(NeoForgeInstances.modProvider));
    public static final Mod FTB_TEAMS = new Mod("ftbteams", Objects.requireNonNull(NeoForgeInstances.modProvider));
}
