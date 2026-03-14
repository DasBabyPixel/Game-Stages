package de.dasbabypixel.gamestages.common.v1_21_1.addon;

import de.dasbabypixel.gamestages.common.addon.ContentRegistry;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonGameContentSerializer;

public interface VContentRegistry {
    ContentRegistry.Attribute<CommonGameContentSerializer<?>> GAME_CONTENT_SERIALIZER = ContentRegistry.Attribute.create(CommonGameContentSerializer.class);
}
