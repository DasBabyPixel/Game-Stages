package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;

import java.util.UUID;

@Mixin(Player.class)
@Implements(@Interface(iface = de.dasbabypixel.gamestages.common.entity.Player.class, prefix = "stages$"))
@NullMarked
public abstract class MCPlayerMixin extends LivingEntity implements de.dasbabypixel.gamestages.common.entity.Player {
    public MCPlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    public UUID stages$getUniqueId() {
        return this.getUUID();
    }
}
