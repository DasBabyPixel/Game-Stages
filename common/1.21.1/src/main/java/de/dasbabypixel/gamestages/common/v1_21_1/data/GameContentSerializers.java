package de.dasbabypixel.gamestages.common.v1_21_1.data;

import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.data.GameContentDirect;
import de.dasbabypixel.gamestages.common.data.GameContentExcept;
import de.dasbabypixel.gamestages.common.data.GameContentFilterType;
import de.dasbabypixel.gamestages.common.data.GameContentMod;
import de.dasbabypixel.gamestages.common.data.GameContentOnly;
import de.dasbabypixel.gamestages.common.data.GameContentRegistry;
import de.dasbabypixel.gamestages.common.data.GameContentSimple;
import de.dasbabypixel.gamestages.common.data.GameContentUnion;
import de.dasbabypixel.gamestages.common.data.attribute.CompilableAttribute;
import de.dasbabypixel.gamestages.common.data.attribute.ImmutableAttribute;
import de.dasbabypixel.gamestages.common.data.attribute.SimpleImmutableAttribute;
import de.dasbabypixel.gamestages.common.v1_21_1.CommonVGameStageMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

@NullMarked
public class GameContentSerializers {
    public static final ResourceKey<Registry<GameContentRegistry.Entry<?, ?, ?, ?>>> CONTENT_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(CommonVGameStageMod.location("game_content_type"));
    public static final ResourceKey<Registry<GameContentSerializer<?>>> SERIALIZER_REGISTRY_KEY = ResourceKey.createRegistryKey(CommonVGameStageMod.location("game_content_serializer"));
    private static final ImmutableAttribute<GameContentRegistry.Entry<?, ?, ?, ?>, StreamCodec<? super RegistryFriendlyByteBuf, ?>> ATTRIBUTE_STREAM_CODEC_ELEMENTS = new SimpleImmutableAttribute<>();
    private static final CompilableAttribute<GameContentRegistry.Builder.Entry<?, ?, ?, ?, ?>, StreamCodec<? super RegistryFriendlyByteBuf, ?>, GameContentRegistry.Entry<?, ?, ?, ?>> ATTRIBUTE_STREAM_CODEC_ELEMENTS_BUILDER = ATTRIBUTE_STREAM_CODEC_ELEMENTS.compilable();
    private final StreamCodec<RegistryFriendlyByteBuf, GameContentRegistry.Entry<?, ?, ?, ?>> streamCodecContentType;
    private final List<GameContentRegistry.Entry<?, ?, ?, ?>> entries;

    private final Map<GameContentRegistry.Entry<?, ?, ?, ?>, StreamCodec<? super RegistryFriendlyByteBuf, ?>> elementCodecByType;
    private final StreamCodec<RegistryFriendlyByteBuf, GameContent> streamCodec;
    private final StreamCodec<RegistryFriendlyByteBuf, GameContentExcept> streamCodecExcept;
    private final StreamCodec<RegistryFriendlyByteBuf, GameContentOnly> streamCodecOnly;
    private final StreamCodec<RegistryFriendlyByteBuf, GameContentUnion> streamCodecUnion;
    private final StreamCodec<RegistryFriendlyByteBuf, GameContentFilterType<?, ?, ?>> streamCodecFilterType;
    private final StreamCodec<RegistryFriendlyByteBuf, GameContentSimple> streamCodecSimple;
    private final StreamCodec<ByteBuf, GameContentMod> streamCodecMod;
    private final Map<GameContentRegistry.Entry<?, ?, ?, ?>, StreamCodec<? super RegistryFriendlyByteBuf, GameContentDirect<?, ?, ?>>> streamCodecDirectMap;
    private final GameContentSerializer<GameContentExcept> serializerExcept;
    private final GameContentSerializer<GameContentOnly> serializerOnly;
    private final GameContentSerializer<GameContentUnion> serializerUnion;
    private final GameContentSerializer<GameContentFilterType<?, ?, ?>> serializerFilterType;
    private final GameContentSerializer<GameContentSimple> serializerSimple;
    private final GameContentSerializer<GameContentMod> serializerMod;
    private final Map<GameContentRegistry.Entry<?, ?, ?, ?>, GameContentSerializer<GameContentDirect<?, ?, ?>>> serializerDirectMap;

    @SuppressWarnings({"DataFlowIssue", "unchecked"})
    public GameContentSerializers(List<? extends GameContentRegistry.Entry<?, ?, ?, ?>> entries) {
        this.entries = List.copyOf(entries);
        {
            var typeCodecByType = new HashMap<GameContentRegistry.Entry<?, ?, ?, ?>, StreamCodec<? super RegistryFriendlyByteBuf, GameContentRegistry.Entry<?, ?, ?, ?>>>();
            var elementCodecByType = new HashMap<GameContentRegistry.Entry<?, ?, ?, ?>, StreamCodec<? super RegistryFriendlyByteBuf, ?>>();
            for (var entry : entries) {
                typeCodecByType.put(entry, StreamCodec.unit(entry));
                elementCodecByType.put(entry, Objects.requireNonNull(entry.get(ATTRIBUTE_STREAM_CODEC_ELEMENTS)));
            }

            this.streamCodecContentType = ByteBufCodecs
                    .registry(CONTENT_TYPE_REGISTRY_KEY)
                    .dispatch(Function.identity(), typeCodecByType::get);
            this.elementCodecByType = Map.copyOf(elementCodecByType);
        }

        this.streamCodec = ByteBufCodecs
                .registry(SERIALIZER_REGISTRY_KEY)
                .dispatch(this::serializer, GameContentSerializer::streamCodec);
        this.streamCodecExcept = StreamCodec.composite(streamCodec, GameContentExcept::base, streamCodec, GameContentExcept::exclusion, GameContentExcept::new);
        this.streamCodecOnly = StreamCodec.composite(streamCodec, GameContentOnly::base, streamCodec, GameContentOnly::inclusion, GameContentOnly::new);
        this.streamCodecUnion = StreamCodec.composite(streamCodec.apply(ByteBufCodecs.list()), GameContentUnion::list, GameContentUnion::new);
        this.streamCodecFilterType = StreamCodec.composite(streamCodec, GameContentFilterType::base, streamCodecContentType, GameContentFilterType::typeEntry, GameContentFilterType::new);
        var simpleEntryCodec = new StreamCodec<RegistryFriendlyByteBuf, GameContentSimple.TypeEntry<?>>() {
            @SuppressWarnings("unchecked")
            @Override
            public void encode(RegistryFriendlyByteBuf buffer, GameContentSimple.TypeEntry<?> value) {
                streamCodecContentType.encode(buffer, value.typeEntry());
                var c = (StreamCodec<? super RegistryFriendlyByteBuf, Object>) Objects.requireNonNull(elementCodecByType.get(value.typeEntry()));
                c.encode(buffer, value.elements());
            }

            @SuppressWarnings("unchecked")
            @Override
            public GameContentSimple.TypeEntry<?> decode(RegistryFriendlyByteBuf buffer) {
                var type = (GameContentRegistry.Entry<?, ?, @NonNull Object, ?>) streamCodecContentType.decode(buffer);
                var c = Objects.requireNonNull(elementCodecByType.get(type));
                var list = (List<Object>) c.decode(buffer);
                return new GameContentSimple.TypeEntry<>(type, list);
            }
        };
        this.streamCodecSimple = simpleEntryCodec
                .apply(ByteBufCodecs.list())
                .map(GameContentSimple::new, GameContentSimple::entries);
        this.streamCodecMod = ByteBufCodecs.STRING_UTF8.map(GameContentMod::new, GameContentMod::modId);

        this.serializerExcept = () -> streamCodecExcept;
        this.serializerOnly = () -> streamCodecOnly;
        this.serializerUnion = () -> streamCodecUnion;
        this.serializerFilterType = () -> streamCodecFilterType;
        this.serializerSimple = () -> streamCodecSimple;
        this.serializerMod = () -> streamCodecMod;

        {
            var streamCodecDirectMap = new HashMap<GameContentRegistry.Entry<?, ?, ?, ?>, StreamCodec<? super RegistryFriendlyByteBuf, GameContentDirect<?, ?, ?>>>();
            for (var entry : entries) {
                var type = (GameContentRegistry.Entry<?, ?, @NonNull Object, ?>) entry;
                var codec = (StreamCodec<? super RegistryFriendlyByteBuf, Object>) entry.get(ATTRIBUTE_STREAM_CODEC_ELEMENTS);
                streamCodecDirectMap.put(type, codec.map(o -> new GameContentDirect<>(type, o), GameContentDirect::elements));
            }
            this.streamCodecDirectMap = Map.copyOf(streamCodecDirectMap);

            var serializerDirectMap = new HashMap<GameContentRegistry.Entry<?, ?, ?, ?>, GameContentSerializer<GameContentDirect<?, ?, ?>>>();
            for (var entry : entries) {
                var direct = this.streamCodecDirectMap.get(entry);
                serializerDirectMap.put(entry, () -> direct);
            }
            this.serializerDirectMap = Map.copyOf(serializerDirectMap);
        }
    }

    @SuppressWarnings("unchecked")
    public static <TypeData, Elements, Element> CompilableAttribute<GameContentRegistry.Builder.Entry<?, ?, TypeData, Elements, Element>, StreamCodec<? super RegistryFriendlyByteBuf, Elements>, GameContentRegistry.Entry<?, TypeData, Elements, Element>> attributeStreamCodecElements() {
        return (CompilableAttribute<GameContentRegistry.Builder.@NonNull Entry<?, ?, TypeData, Elements, Element>, @NonNull StreamCodec<? super RegistryFriendlyByteBuf, Elements>, GameContentRegistry.@NonNull Entry<?, TypeData, Elements, Element>>) (Object) ATTRIBUTE_STREAM_CODEC_ELEMENTS_BUILDER;
    }

    @SuppressWarnings("unchecked")
    public <TypeData, Elements, Element> StreamCodec<? super RegistryFriendlyByteBuf, GameContentDirect<TypeData, Elements, Element>> directStreamCodec(GameContentRegistry.Entry<?, TypeData, Elements, Element> typeEntry) {
        return (StreamCodec<? super RegistryFriendlyByteBuf, GameContentDirect<TypeData, Elements, Element>>) Objects.requireNonNull((Object) this.streamCodecDirectMap.get(typeEntry));
    }

    public void register(RegisterHandler handler) {
        handler.register(CONTENT_TYPE_REGISTRY_KEY, registry -> {
            for (var entry : entries) {
                registry.register(CommonVGameStageMod.location(entry.id()), entry);
            }
        });
        handler.register(SERIALIZER_REGISTRY_KEY, registry -> {
            registry.register(CommonVGameStageMod.location("except"), serializerExcept);
            registry.register(CommonVGameStageMod.location("only"), serializerOnly);
            registry.register(CommonVGameStageMod.location("union"), serializerUnion);
            registry.register(CommonVGameStageMod.location("filter_type"), serializerFilterType);
            registry.register(CommonVGameStageMod.location("simple"), serializerSimple);

            for (var entry : entries) {
                registry.register(CommonVGameStageMod.location("direct_" + entry.id()), Objects.requireNonNull(serializerDirectMap.get(entry)));
            }
        });
    }

    private GameContentSerializer<?> serializer(GameContent content) {
        return switch (content) {
            case GameContentExcept ignored -> serializerExcept;
            case GameContentOnly ignored -> serializerOnly;
            case GameContentUnion ignored -> serializerUnion;
            case GameContentFilterType<?, ?, ?> ignored -> serializerFilterType;
            case GameContentSimple ignored -> serializerSimple;
            case GameContentDirect<?, ?, ?> direct ->
                    Objects.requireNonNull(serializerDirectMap.get(direct.typeEntry()));
            case GameContentMod ignored -> serializerMod;
        };
    }

    public interface RegisterHandler {
        <T> void register(ResourceKey<? extends Registry<T>> registryKey, Consumer<RegistryHelper<T>> consumer);
    }

    public interface RegistryHelper<T> {
        void register(ResourceKey<T> key, T value);

        void register(ResourceLocation location, T value);
    }
}
