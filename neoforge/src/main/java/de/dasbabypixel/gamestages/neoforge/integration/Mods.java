package de.dasbabypixel.gamestages.neoforge.integration;

import de.dasbabypixel.gamestages.neoforge.NeoForgeInstances;

import java.util.Objects;

public class Mods {
    public static final Mod KUBEJS = new Mod("kubejs", Objects.requireNonNull(NeoForgeInstances.modProvider));
}
