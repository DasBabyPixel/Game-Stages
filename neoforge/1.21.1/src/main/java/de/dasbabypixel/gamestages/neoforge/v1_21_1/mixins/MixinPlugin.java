package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

@NullMarked
public class MixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(@Nullable String mixinPackage) {

    }

    @Override
    public @Nullable String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(@Nullable String targetClassName, @Nullable String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(@Nullable Set<String> myTargets, @Nullable Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return List.of();
    }

    @Override
    public void preApply(@Nullable String targetClassName, @Nullable ClassNode targetClass, @Nullable String mixinClassName, @Nullable IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(@Nullable String targetClassName, @Nullable ClassNode targetClass, @Nullable String mixinClassName, @Nullable IMixinInfo mixinInfo) {

    }
}
