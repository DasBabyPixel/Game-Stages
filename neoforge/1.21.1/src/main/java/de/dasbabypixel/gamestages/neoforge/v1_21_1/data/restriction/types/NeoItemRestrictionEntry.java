package de.dasbabypixel.gamestages.neoforge.v1_21_1.data.restriction.types;

import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.ItemCollection;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.server.ServerGameStageManager;
import de.dasbabypixel.gamestages.common.network.CustomPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonItemCollection;
import de.dasbabypixel.gamestages.common.v1_21_1.data.restriction.compiled.ItemCollectionCompiler;
import de.dasbabypixel.gamestages.common.v1_21_1.data.restriction.types.CommonItemRestrictionEntry;
import de.dasbabypixel.gamestages.common.v1_21_1.network.packets.clientbound.CommonItemRestrictionPacket;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.jei.StagesJEIPlugin;
import net.neoforged.fml.util.thread.EffectiveSide;
import org.jspecify.annotations.NonNull;

public class NeoItemRestrictionEntry extends CommonItemRestrictionEntry<NeoItemRestrictionEntry, NeoItemRestrictionEntry.PreCompiled> {
    public NeoItemRestrictionEntry(@NonNull PreparedRestrictionPredicate predicate, @NonNull ItemCollection<?> targetCollection) {
        super(predicate, targetCollection);
    }

    @Override
    public @NonNull CustomPacket createPacket(@NonNull ServerGameStageManager instance) {
        var items = instance.get(ItemCollectionCompiler.ATTRIBUTE).flatten(targetCollection());
        return new CommonItemRestrictionPacket(predicate(), items, hideTooltip(), renderItemName(), hideInJEI());
    }

    @Override
    public NeoItemRestrictionEntry.@NonNull PreCompiled precompile(@NonNull AbstractGameStageManager instance) {
        var items = instance.get(ItemCollectionCompiler.ATTRIBUTE).flatten(targetCollection());
        return new PreCompiled(items);
    }

    @Override
    public @NonNull CompiledRestrictionEntry compile(@NonNull AbstractGameStageManager instance, @NonNull PreCompiled preCompiled, @NonNull CompiledRestrictionPredicate predicate) {
        System.out.println("compiled...");
        System.out.println("compiled...");
        predicate.addNotifier(newTest -> {
            System.out.println("update " + newTest);
            System.out.println(EffectiveSide.get());
            if (EffectiveSide.get().isClient()) {
                System.out.println("On client");
                if (newTest) {
                    StagesJEIPlugin.show(preCompiled.items.items());
                } else {
                    StagesJEIPlugin.hide(preCompiled.items.items());
                }
            }
        });
        return new CompiledRestrictionEntry() {

        };
    }

    public record PreCompiled(CommonItemCollection.@NonNull Direct items) {
    }
}
