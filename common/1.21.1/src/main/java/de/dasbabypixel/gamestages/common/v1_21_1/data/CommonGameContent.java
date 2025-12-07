package de.dasbabypixel.gamestages.common.v1_21_1.data;

import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.data.GameContentType;
import de.dasbabypixel.gamestages.common.v1_21_1.CommonVGameStageMod;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public interface CommonGameContent extends GameContent {
    @NonNull ResourceKey<Registry<CommonGameContentSerializer<?>>> REGISTRY_KEY = ResourceKey.createRegistryKey(CommonVGameStageMod.location("game_content_serializer"));
    @NonNull StreamCodec<RegistryFriendlyByteBuf, CommonGameContent> STREAM_CODEC = ByteBufCodecs
            .registry(REGISTRY_KEY)
            .dispatch(CommonGameContent::serializer, CommonGameContentSerializer::streamCodec);

    @HideFromJS
    @NonNull CommonGameContentSerializer<?> serializer();

    @SuppressWarnings("unchecked")
    @Override
    default @NonNull CommonGameContent except(@NonNull GameContent @NonNull ... other) {
        return new CommonGameContent.Except(this, (List<CommonGameContent>) (Object) List.of(other));
    }

    @SuppressWarnings("unchecked")
    @Override
    default @NonNull CommonGameContent only(@NonNull GameContent @NonNull ... other) {
        return new CommonGameContent.Only(this, (List<CommonGameContent>) (Object) List.of(other));
    }

    @SuppressWarnings("unchecked")
    @Override
    default @NonNull CommonGameContent union(@NonNull GameContent @NonNull ... other) {
        var list = new ArrayList<CommonGameContent>(other.length + 1);
        list.add(this);
        Object otherList = Arrays.asList(other);
        list.addAll((Collection<? extends CommonGameContent>) otherList);
        return new CommonGameContent.Union(list);
    }

    @Override
    default @NonNull CommonGameContent filterType(@NonNull GameContentType<?> type) {
        return new FilterType(this, type);
    }

    interface Composite extends CommonGameContent {
        @NonNull Collection<? extends GameContent> content();
    }

    record FilterType(@NonNull CommonGameContent base, @NonNull GameContentType<?> type) implements CommonGameContent {
        public static final StreamCodec<RegistryFriendlyByteBuf, FilterType> STREAM_CODEC = StreamCodec.composite(CommonGameContent.STREAM_CODEC, FilterType::base, GameContentTypeSerializer.STREAM_CODEC, FilterType::type, FilterType::new);

        @Override
        public @NonNull CommonGameContentSerializer<FilterType> serializer() {
            return CommonGameContentSerializer.FILTER_TYPE;
        }
    }

    record Except(@NonNull CommonGameContent base,
                  @NonNull List<@NonNull CommonGameContent> exclusion) implements CommonGameContent {
        public static final StreamCodec<RegistryFriendlyByteBuf, Except> STREAM_CODEC = StreamCodec.composite(CommonGameContent.STREAM_CODEC, Except::base, CommonGameContent.STREAM_CODEC.apply(ByteBufCodecs.list()), Except::exclusion, Except::new);

        public Except {
            exclusion = List.copyOf(exclusion);
        }

        @Override
        public @NonNull CommonGameContentSerializer<Except> serializer() {
            return CommonGameContentSerializer.EXCEPT;
        }
    }

    record Only(@NonNull CommonGameContent base,
                @NonNull List<@NonNull CommonGameContent> inclusion) implements CommonGameContent {
        public static final StreamCodec<RegistryFriendlyByteBuf, Only> STREAM_CODEC = StreamCodec.composite(CommonGameContent.STREAM_CODEC, Only::base, CommonGameContent.STREAM_CODEC.apply(ByteBufCodecs.list()), Only::inclusion, Only::new);

        public Only {
            inclusion = List.copyOf(inclusion);
        }

        @Override
        public @NonNull CommonGameContentSerializer<Only> serializer() {
            return CommonGameContentSerializer.ONLY;
        }
    }

    record Union(@NonNull List<@NonNull CommonGameContent> content) implements Composite {
        public static final StreamCodec<RegistryFriendlyByteBuf, Union> STREAM_CODEC = StreamCodec.composite(CommonGameContent.STREAM_CODEC.apply(ByteBufCodecs.list()), Union::content, Union::new);

        public Union {
            content = List.copyOf(content);
        }

        @Override
        public @NonNull CommonGameContentSerializer<Union> serializer() {
            return CommonGameContentSerializer.UNION;
        }
    }
}
