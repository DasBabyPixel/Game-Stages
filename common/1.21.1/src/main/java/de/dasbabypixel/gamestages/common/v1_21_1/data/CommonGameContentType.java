package de.dasbabypixel.gamestages.common.v1_21_1.data;

import de.dasbabypixel.gamestages.common.data.GameContentType;
import de.dasbabypixel.gamestages.common.data.TypedGameContent;
import org.jspecify.annotations.NonNull;

public interface CommonGameContentType<T extends TypedGameContent> extends GameContentType<T> {
    @NonNull GameContentTypeSerializer<T> serializer();
}
