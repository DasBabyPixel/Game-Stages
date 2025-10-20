package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration;

import de.dasbabypixel.gamestages.neoforge.integration.Mod;
import de.dasbabypixel.gamestages.neoforge.integration.ModProvider;
import net.neoforged.fml.ModList;

public class NeoModProvider implements ModProvider {
    @Override
    public boolean isLoaded(Mod mod) {
        return ModList.get().isLoaded(mod.id());
    }
}
