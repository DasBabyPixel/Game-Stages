package de.dasbabypixel.gamestages.common.v1_21_1.data;

import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.data.GameContentProvider;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.List;

@NullMarked
public class VGameContentProvider implements GameContentProvider {
    @Override
    public GameContent emptyContent() {
        return new CommonGameContent.Union(List.of());
    }

    @SuppressWarnings("unchecked")
    @Override
    public GameContent union(Collection<? extends GameContent> collection) {
        return new CommonGameContent.Union((List<CommonGameContent>) List.copyOf(collection));
    }
}
