package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.jei.mixins;

import mezz.jei.gui.bookmarks.IBookmark;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public interface IBookmarkList {
    List<IBookmark> getBookmarks();

    /**
     * Removes bookmarks
     */
    void removeAll(List<IBookmark> bookmarks);
}
