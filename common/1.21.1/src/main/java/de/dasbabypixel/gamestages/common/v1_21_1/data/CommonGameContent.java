package de.dasbabypixel.gamestages.common.v1_21_1.data;

import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.data.GameContentType;
import de.dasbabypixel.gamestages.common.data.TypedGameContent;
import de.dasbabypixel.gamestages.common.v1_21_1.CommonVGameStageMod;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@NullMarked
public interface CommonGameContent extends GameContent {
    ResourceKey<Registry<CommonGameContentSerializer<?>>> REGISTRY_KEY = ResourceKey.createRegistryKey(CommonVGameStageMod.location("game_content_serializer"));
    StreamCodec<RegistryFriendlyByteBuf, CommonGameContent> STREAM_CODEC = ByteBufCodecs.registry(REGISTRY_KEY)
            .dispatch(CommonGameContent::serializer, CommonGameContentSerializer::streamCodec);

    @HideFromJS
    CommonGameContentSerializer<?> serializer();

    @SuppressWarnings("unchecked")
    @Override
    default CommonGameContent except(GameContent... other) {
        return new CommonGameContent.Except(this, (List<CommonGameContent>) (Object) List.of(other));
    }

    @SuppressWarnings("unchecked")
    @Override
    default CommonGameContent only(GameContent... other) {
        return new CommonGameContent.Only(this, (List<CommonGameContent>) (Object) List.of(other));
    }

    @SuppressWarnings("unchecked")
    @Override
    default CommonGameContent union(GameContent... other) {
        var len = other.length + 1;
        var empty = this instanceof TypedGameContent typed && typed.isEmpty();
        if (empty) len--;
        var list = new ArrayList<CommonGameContent>(len);
        if (!empty) list.add(this);
        Object otherList = Arrays.asList(other);
        list.addAll((Collection<? extends CommonGameContent>) otherList);
        return new CommonGameContent.Union(list);
    }

    @Override
    default CommonGameContent filterType(GameContentType<?> type) {
        return new FilterType(this, (CommonGameContentType<?>) type);
    }

    interface Composite extends CommonGameContent {
        Collection<? extends GameContent> content();
    }

    record Mod(String modId) implements CommonGameContent {
        @SuppressWarnings("DataFlowIssue")
        public static final StreamCodec<RegistryFriendlyByteBuf, Mod> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, Mod::modId, Mod::new);

        @Override
        public CommonGameContentSerializer<?> serializer() {
            return CommonGameContentSerializer.MOD;
        }
    }

    record FilterType(CommonGameContent base, CommonGameContentType<?> type) implements CommonGameContent {
        @SuppressWarnings("DataFlowIssue")
        public static final StreamCodec<RegistryFriendlyByteBuf, FilterType> STREAM_CODEC = StreamCodec.composite(CommonGameContent.STREAM_CODEC, FilterType::base, CommonGameContentType.STREAM_CODEC, FilterType::type, FilterType::new);

        @Override
        public CommonGameContentSerializer<FilterType> serializer() {
            return CommonGameContentSerializer.FILTER_TYPE;
        }
    }

    @SuppressWarnings("DataFlowIssue")
    record Except(CommonGameContent base, List<CommonGameContent> exclusion) implements CommonGameContent {
        public static final StreamCodec<RegistryFriendlyByteBuf, Except> STREAM_CODEC = StreamCodec.composite(CommonGameContent.STREAM_CODEC, Except::base, CommonGameContent.STREAM_CODEC.apply(ByteBufCodecs.list()), Except::exclusion, Except::new);

        public Except {
            exclusion = List.copyOf(exclusion);
        }

        @Override
        public CommonGameContentSerializer<Except> serializer() {
            return CommonGameContentSerializer.EXCEPT;
        }
    }

    @SuppressWarnings("DataFlowIssue")
    record Only(CommonGameContent base, List<CommonGameContent> inclusion) implements CommonGameContent {
        public static final StreamCodec<RegistryFriendlyByteBuf, Only> STREAM_CODEC = StreamCodec.composite(CommonGameContent.STREAM_CODEC, Only::base, CommonGameContent.STREAM_CODEC.apply(ByteBufCodecs.list()), Only::inclusion, Only::new);

        public Only {
            inclusion = List.copyOf(inclusion);
        }

        @Override
        public CommonGameContentSerializer<Only> serializer() {
            return CommonGameContentSerializer.ONLY;
        }
    }

    @SuppressWarnings("DataFlowIssue")
    record Union(List<CommonGameContent> content) implements Composite {
        public static final StreamCodec<RegistryFriendlyByteBuf, Union> STREAM_CODEC = StreamCodec.composite(CommonGameContent.STREAM_CODEC.apply(ByteBufCodecs.list()), Union::content, Union::new);

        public Union {
            content = List.copyOf(content);
        }

        @Override
        public CommonGameContentSerializer<Union> serializer() {
            return CommonGameContentSerializer.UNION;
        }
    }
}
