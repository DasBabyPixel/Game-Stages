package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.jei;

import de.dasbabypixel.gamestages.common.CommonInstances;
import de.dasbabypixel.gamestages.common.client.ClientGameStageManager;
import de.dasbabypixel.gamestages.common.v1_21_1.CommonVGameStageMod;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddon;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonJEI;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonManager;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@JeiPlugin
public class StagesJEIPlugin implements IModPlugin {
    private static final Map<NeoAddon, NeoAddonJEI> ADDON_MAP = new HashMap<>();
    private static boolean populated = false;

    static {
        NeoAddonManager.registerAddon("jei", () -> JEIAddon.ADDON);
    }

    public static Map<NeoAddon, NeoAddonJEI> addonMap() {
        if (!populated) {
            for (var addon : NeoAddonManager.instance().addons()) {
                ADDON_MAP.put(addon, addon.createJEISupport());
            }
            populated = true;
        }
        return ADDON_MAP;
    }

    public static Collection<NeoAddonJEI> addons() {
        return addonMap().values();
    }

    @Override
    public void onRuntimeAvailable(@NonNull IJeiRuntime jeiRuntime) {
        for (var addon : addonMap().values()) {
            addon.onRuntimeAvailable(jeiRuntime);
        }
        var player = CommonInstances.platformPlayerProvider.clientSelfPlayer();
        if (player != null) {
            JEIAddon.ADDON.singleRefreshAll(ClientGameStageManager.instance(), player.getGameStages());
        }
    }

    @Override
    public void onRuntimeUnavailable() {
        for (var addon : addonMap().values()) {
            addon.onRuntimeUnavailable();
        }
    }

    @Override
    public @NonNull ResourceLocation getPluginUid() {
        return CommonVGameStageMod.location("game_stages");
    }
}
