package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins.recipe.integration.jei;

import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.jei.mixins.IBookmarkOverlay;
import mezz.jei.gui.bookmarks.BookmarkList;
import mezz.jei.gui.overlay.bookmarks.BookmarkOverlay;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@NullMarked
@Mixin(value = BookmarkOverlay.class, remap = false)
@Implements(@Interface(iface = IBookmarkOverlay.class, prefix = "stages$"))
public class JEIBookmarkOverlayMixin {
    @Shadow
    @Final
    private BookmarkList bookmarkList;

    public BookmarkList stages$getBookmarkList() {
        return bookmarkList;
    }
}
