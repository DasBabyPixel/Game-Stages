package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins.recipe.integration.jei;

import com.mojang.serialization.Codec;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.jei.mixins.IBookmarkList;
import mezz.jei.api.helpers.ICodecHelper;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.gui.bookmarks.BookmarkList;
import mezz.jei.gui.bookmarks.IBookmark;
import mezz.jei.gui.config.IBookmarkConfig;
import net.minecraft.core.RegistryAccess;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Set;

@NullMarked
@Mixin(value = BookmarkList.class, remap = false)
@Implements(@Interface(iface = IBookmarkList.class, prefix = "stages$"))
public abstract class JEIBookmarkListMixin {
    @Shadow
    @Final
    private List<IBookmark> bookmarksList;

    @Shadow
    @Final
    private Set<IBookmark> bookmarksSet;
    @Shadow
    @Final
    private IBookmarkConfig bookmarkConfig;
    @Shadow
    @Final
    private IRecipeManager recipeManager;
    @Shadow
    @Final
    private IFocusFactory focusFactory;
    @Shadow
    @Final
    private IGuiHelper guiHelper;
    @Shadow
    @Final
    private IIngredientManager ingredientManager;
    @Shadow
    @Final
    private RegistryAccess registryAccess;
    @Shadow
    @Final
    private ICodecHelper codecHelper;
    @Shadow
    @Final
    private Codec<IBookmark> bookmarkCodec;

    @Shadow
    protected abstract void notifyListenersOfChange();

    public List<IBookmark> stages$getBookmarks() {
        return List.copyOf(bookmarksList);
    }

    public void stages$removeAll(List<IBookmark> bookmarks) {
        var changed = false;
        for (var bookmark : bookmarks) {
            if (!bookmarksSet.remove(bookmark)) continue;
            bookmarksList.remove(bookmark);
            changed = true;
        }
        if (changed) {
            notifyListenersOfChange();
            bookmarkConfig.saveBookmarks(recipeManager, focusFactory, guiHelper, ingredientManager, registryAccess, codecHelper, bookmarksList, bookmarkCodec);
        }
    }
}
