package de.dasbabypixel.gamestages.common.addons.item;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@NullMarked
public class ItemStackRestrictionResolverFactories {
    private static final ItemStackRestrictionResolverFactories INSTANCE = new ItemStackRestrictionResolverFactories();
    private final Map<String, ItemStackRestrictionResolverFactory<?>> factoryMap = new HashMap<>();

    private ItemStackRestrictionResolverFactories() {
    }

    public @Nullable ItemStackRestrictionResolverFactory<?> getFactory(String factoryId) {
        return factoryMap.get(factoryId);
    }

    public Collection<ItemStackRestrictionResolverFactory<?>> getAll() {
        return factoryMap.values();
    }

    public void register(ItemStackRestrictionResolverFactory<?> factory) {
        this.factoryMap.put(factory.factoryId(), factory);
    }

    public static ItemStackRestrictionResolverFactories instance() {
        return INSTANCE;
    }
}
