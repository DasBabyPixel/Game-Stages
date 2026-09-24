package de.dasbabypixel.gamestages.neoforge.v1_21_1.addon;

import de.dasbabypixel.gamestages.common.data.GameContentRegistry;
import de.dasbabypixel.gamestages.common.data.attribute.AttributeEntry;
import de.dasbabypixel.gamestages.common.data.attribute.ImmutableAttributeHolder;
import de.dasbabypixel.gamestages.neoforge.integration.Mods;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.probejs.StagesProbeJSPlugin;
import dev.latvian.mods.rhino.type.TypeInfo;
import moe.wolfgirl.probejs.typescript.document.base.TypeDocument;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@NullMarked
public class AddonUtil {
    public static Collection<AttributeEntry<ImmutableAttributeHolder<?>, ?>> returnType(TypeInfo typeInfo) {
        return typeInformation(() -> new StagesProbeJSPlugin.TypeInformation.Converting(typeInfo), () -> List.of(Objects.requireNonNull(typeInfo.asClass())));
    }

    public static Collection<AttributeEntry<ImmutableAttributeHolder<?>, ?>> returnType(GameContentRegistry.Entry<?, ?, ?, ?> typeEntry) {
        return typeInformation(() -> new StagesProbeJSPlugin.TypeInformation.Direct(StagesProbeJSPlugin.typedCollection(typeEntry)), List::of);
    }

    public static Collection<AttributeEntry<ImmutableAttributeHolder<?>, ?>> singleParameter(TypeInfo typeInfo) {
        return typeInformation(() -> new StagesProbeJSPlugin.TypeInformation.Converting(typeInfo, TypeDocument::markAsInput), () -> List.of(Objects.requireNonNull(typeInfo.asClass())));
    }

    public static Collection<AttributeEntry<ImmutableAttributeHolder<?>, ?>> usingOnlyArray(GameContentRegistry.Entry<?, ?, ?, ?> typeEntry) {
        return typeInformation(() -> new StagesProbeJSPlugin.TypeInformation.Direct(Objects.requireNonNull(Objects
                .requireNonNull(StagesProbeJSPlugin.collectionUsingOnly(typeEntry).markAsInput())
                .asArray())), List::of);
    }


    private static Collection<AttributeEntry<ImmutableAttributeHolder<?>, ?>> typeInformation(Supplier<StagesProbeJSPlugin.TypeInformation> supplier, Supplier<List<Class<?>>> discoveryClasses) {
        var attributes = new ArrayList<AttributeEntry<ImmutableAttributeHolder<?>, ?>>();
        if (Mods.PROBEJS.isLoaded()) {
            attributes.add(new AttributeEntry<>(StagesProbeJSPlugin.ATTRIBUTE_TYPE_INFORMATION, supplier.get()));
            attributes.add(new AttributeEntry<>(StagesProbeJSPlugin.ATTRIBUTE_DISCOVERY_CLASSES, new StagesProbeJSPlugin.DiscoveryClasses(discoveryClasses.get())));
        }
        return attributes;
    }
}
