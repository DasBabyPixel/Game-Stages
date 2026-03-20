package de.dasbabypixel.gamestages.neoforge.v1_21_1.data;

import de.dasbabypixel.gamestages.common.BuildConstants;
import de.dasbabypixel.gamestages.common.data.GameStage;
import de.dasbabypixel.gamestages.common.v1_21_1.network.util.GameStagePayload;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class Attachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, BuildConstants.MOD_ID);
    public static final Supplier<AttachmentType<List<GameStage>>> ATTACHMENT_REFERENCES = ATTACHMENT_TYPES.register("game_stages", () -> AttachmentType
            .builder(() -> List.<GameStage>of())
            .serialize(GameStagePayload.CODEC_LIST)
            .copyOnDeath()
            .build());
    public static final Supplier<AttachmentType<Source>> SOURCE = ATTACHMENT_TYPES.register("source", () -> AttachmentType
            .serializable(Source::new)
            .build());

    public static class Source implements INBTSerializable<CompoundTag> {
        private UUID owner;

        public Source(IAttachmentHolder h) {
            if (h instanceof Player player) owner = player.getUUID();
        }

        public Source(UUID owner) {
            this.owner = owner;
        }

        public @Nullable UUID owner() {
            return owner;
        }

        public void setOwner(@Nullable UUID owner) {
            this.owner = owner;
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.@NonNull Provider provider) {
            var tag = new CompoundTag();
            if (owner != null) {
                tag.putUUID("owner", owner);
            }
            return tag;
        }

        @Override
        public void deserializeNBT(HolderLookup.@NonNull Provider provider, @NonNull CompoundTag compoundTag) {
            if (compoundTag.contains("owner")) {
                owner = compoundTag.getUUID("owner");
            } else {
                owner = null;
            }
        }
    }
}
