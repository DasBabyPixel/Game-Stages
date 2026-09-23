package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins.integration.kubejs;

import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.JSContext;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.StagesServerScriptManager;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Objects;

@NullMarked
@Mixin(ServerScriptManager.class)
@Implements(@Interface(iface = StagesServerScriptManager.class, prefix = "stages$"))
public class ServerScriptManagerMixin {
    @Unique
    private @Nullable JSContext stages$context;

    public JSContext stages$context() {
        return Objects.requireNonNull(stages$context);
    }

    public void stages$context(@Nullable JSContext context) {
        this.stages$context = context;
    }
}
