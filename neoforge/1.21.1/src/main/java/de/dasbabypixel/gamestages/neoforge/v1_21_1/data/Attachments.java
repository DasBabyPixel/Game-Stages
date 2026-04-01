package de.dasbabypixel.gamestages.neoforge.v1_21_1.data;

import de.dasbabypixel.gamestages.common.BuildConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.function.Supplier;

public class Attachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(Objects.requireNonNull(NeoForgeRegistries.ATTACHMENT_TYPES), BuildConstants.MOD_ID);
    public static final Supplier<AttachmentType<Source>> SOURCE = ATTACHMENT_TYPES.register("source", () -> AttachmentType
            .serializable(Source::new)
            .build());

    public static class Source implements INBTSerializable<CompoundTag> {
        private final @NonNull Set<@NonNull UUID> owners = new HashSet<>();

        public Source(IAttachmentHolder h) {
            if (h instanceof Player player) {
                owners.add(player.getUUID());
            }
        }

        public Source(@NonNull UUID owner) {
            this.owners.add(owner);
        }

        public @NonNull Set<@NonNull UUID> owners() {
            return owners;
        }

        public void addOwner(@NonNull UUID owner) {
            this.owners.add(owner);
        }

        public void setOwners(@NonNull Collection<@NonNull UUID> owners) {
            this.owners.clear();
            this.owners.addAll(owners);
        }

        public void addOwners(@NonNull Collection<@NonNull UUID> owners) {
            this.owners.addAll(owners);
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.@NonNull Provider provider) {
            var tag = new CompoundTag();
            if (!owners.isEmpty()) {
                var ownerList = new ListTag(owners.size());
                for (var owner : owners) {
                    ownerList.add(NbtUtils.createUUID(owner));
                }
                tag.put("owners", ownerList);
            }
            return tag;
        }

        @Override
        public void deserializeNBT(HolderLookup.@NonNull Provider provider, @NonNull CompoundTag compoundTag) {
            if (compoundTag.contains("owner")) {
                owners.add(compoundTag.getUUID("owner"));
            } else if (compoundTag.contains("owners")) {
                var ownerList = compoundTag.getList("owners", Tag.TAG_INT_ARRAY);
                for (var tag : ownerList) {
                    owners.add(NbtUtils.loadUUID(Objects.requireNonNull(tag)));
                }
            }
        }
    }
}
