package de.dasbabypixel.gamestages.common.v1_21_1.addons.item;

import de.dasbabypixel.gamestages.common.addon.AddonManager;
import de.dasbabypixel.gamestages.common.data.ItemStack;
import de.dasbabypixel.gamestages.common.data.server.ServerGameStageManager;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.messages.RecipeMessages;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.messages.ResolveItemStackPredicate;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public class ItemRecipeIntegration {
    public void register(AddonManager<?> addonManager) {
        addonManager.addMessageListener(RecipeMessages.ORIGIN_ID, ResolveItemStackPredicate.ID, (addon, objectMessage) -> {
            var message = (ResolveItemStackPredicate) Objects.requireNonNull(objectMessage);
            var entry = VItemAddon.getEntry(ServerGameStageManager.instance(), message.itemStack, (ItemStack) (Object) message.itemStack);
            if (entry != null) {
                message.predicate = message.predicate.and(entry.predicate());
            }
        });
    }
}
