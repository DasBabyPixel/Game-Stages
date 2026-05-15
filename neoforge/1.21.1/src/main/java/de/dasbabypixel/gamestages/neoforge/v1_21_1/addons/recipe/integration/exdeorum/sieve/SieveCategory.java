package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration.exdeorum.sieve;


import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;
import thedarkcolour.exdeorum.compat.ClientXeiUtil;
import thedarkcolour.exdeorum.compat.XeiUtil;
import thedarkcolour.exdeorum.compat.jei.ExDeorumJeiPlugin;
import thedarkcolour.exdeorum.data.TranslationKeys;
import thedarkcolour.exdeorum.material.DefaultMaterials;

import java.util.List;

@NullMarked
public class SieveCategory implements IRecipeCategory<JEISieveRecipe> {
    private final IDrawable slot;
    private final IDrawable row;
    private final IDrawable icon;
    private final Component title;
    private final MutableInt rows;

    public SieveCategory(IGuiHelper helper, ItemLike icon, Component title, MutableInt rows) {
        this.slot = helper.getSlotDrawable();
        this.row = helper.createDrawable(ExDeorumJeiPlugin.EX_DEORUM_JEI_TEXTURE, 0, 0, 162, 18);
        this.icon = helper.createDrawableItemStack(new ItemStack(icon));
        this.title = title;
        this.rows = rows;
    }

    public SieveCategory(IGuiHelper helper) {
        this(helper, DefaultMaterials.OAK_SIEVE, Component.translatable(TranslationKeys.SIEVE_CATEGORY_TITLE), JEISieveRecipe.SIEVE_ROWS);
    }

    @Override
    public RecipeType<JEISieveRecipe> getRecipeType() {
        return JEISieveRecipe.RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return this.title;
    }

    @Override
    public int getWidth() {
        return XeiUtil.SIEVE_WIDTH;
    }

    @Override
    public int getHeight() {
        return XeiUtil.SIEVE_ROW_START + XeiUtil.SIEVE_ROW_HEIGHT * rows.intValue();
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, JEISieveRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 59, 1).addIngredients(recipe.ingredient());
        builder.addSlot(RecipeIngredientRole.CATALYST, 87, 1).addItemStack(recipe.mesh());

        for (int i = 0; i < recipe.results().size(); i++) {
            var result = recipe.results().get(i);
            var slot = builder.addSlot(RecipeIngredientRole.OUTPUT, 1 + (i % 9) * 18, 1 + XeiUtil.SIEVE_ROW_START + 18 * (i / 9))
                    .addItemStack(result.item());

            addTooltips(slot, result.byHandOnly(), result.provider(), result.holder());
        }
    }

    @Override
    public void draw(JEISieveRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        this.slot.draw(graphics, 58, 0);
        this.slot.draw(graphics, 86, 0);

        int rows = this.rows.intValue();

        for (int i = 0; i < rows; i++) {
            this.row.draw(graphics, 0, 28 + i * 18);
        }
    }

    @SuppressWarnings("DataFlowIssue")
    public static void addTooltips(IRecipeSlotBuilder slot, boolean byHandOnly, NumberProvider provider, RecipeHolder<?> holder) {
        if (byHandOnly) {
            slot.setCustomRenderer(VanillaTypes.ITEM_STACK, AsteriskItemRenderer.INSTANCE);
        }
        slot.addRichTooltipCallback((slotView, tooltip) -> {
            XeiUtil.addSieveDropTooltip(byHandOnly, provider, tooltip::add);
            tooltip.add(Component.literal("Recipe ID: " + holder.id()).withStyle(ChatFormatting.GRAY));
        });
    }

    enum AsteriskItemRenderer implements IIngredientRenderer<ItemStack> {
        INSTANCE;

        AsteriskItemRenderer() {
        }

        public void render(GuiGraphics graphics, @Nullable ItemStack ingredient) {
            if (ingredient != null) {
                RenderSystem.enableDepthTest();
                ClientXeiUtil.renderItemWithAsterisk(graphics, ingredient);
                RenderSystem.disableDepthTest();
            }

        }

        public List<Component> getTooltip(ItemStack ingredient, TooltipFlag tooltipFlag) {
            Minecraft minecraft = Minecraft.getInstance();
            Player player = minecraft.player;
            return ingredient.getTooltipLines(Item.TooltipContext.EMPTY, player, tooltipFlag);
        }
    }
}
