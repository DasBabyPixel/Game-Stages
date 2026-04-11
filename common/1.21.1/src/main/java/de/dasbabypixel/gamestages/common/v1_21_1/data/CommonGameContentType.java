package de.dasbabypixel.gamestages.common.v1_21_1.data;

import de.dasbabypixel.gamestages.common.data.GameContentType;
import de.dasbabypixel.gamestages.common.data.TypedGameContent;
import de.dasbabypixel.gamestages.common.v1_21_1.CommonVGameStageMod;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import org.jspecify.annotations.NullMarked;

import java.util.function.Function;

@NullMarked
public interface CommonGameContentType<T extends TypedGameContent> extends GameContentType<T> {
    ResourceKey<Registry<CommonGameContentType<?>>> REGISTRY_KEY = ResourceKey.createRegistryKey(CommonVGameStageMod.location("game_content_type"));
    StreamCodec<RegistryFriendlyByteBuf, CommonGameContentType<?>> STREAM_CODEC = ByteBufCodecs
            .registry(REGISTRY_KEY)
            .dispatch(Function.identity(), CommonGameContentType::streamCodec);

    StreamCodec<RegistryFriendlyByteBuf, CommonGameContentType<T>> streamCodec();

    T modContent(String modId);

    abstract class AbstractGameContentType<T extends TypedGameContent> implements CommonGameContentType<T> {
        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CommonGameContentType<T>> streamCodec() {
            return StreamCodec.unit(this);
        }
    }
}
