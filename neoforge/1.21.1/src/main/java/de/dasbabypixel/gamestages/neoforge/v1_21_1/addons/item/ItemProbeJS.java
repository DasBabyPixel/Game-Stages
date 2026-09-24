package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item;

import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenTypedData;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.ItemType;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network.DataDrivenTypes;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonProbeJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item.jsapi.ItemStackRestrictionEntryJS;
import moe.wolfgirl.probejs.plugin.builtins.alias.RecordTypes;
import moe.wolfgirl.probejs.plugin.builtins.alias.RegistryTypes;
import moe.wolfgirl.probejs.plugin.builtins.alias.SpecialTypes;
import moe.wolfgirl.probejs.typescript.base.AliasRegistrar;
import moe.wolfgirl.probejs.typescript.document.Types;
import moe.wolfgirl.probejs.typescript.document.base.Type;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.probejs.StagesProbeJSPlugin.typedCompletionsClassPath;
import static moe.wolfgirl.probejs.typescript.document.Types.clazz;
import static moe.wolfgirl.probejs.typescript.document.Types.literal;
import static moe.wolfgirl.probejs.typescript.document.Types.union;
import static moe.wolfgirl.probejs.typescript.document.Types.wrapped;


@NullMarked
public class ItemProbeJS implements NeoAddonProbeJS {
    static {
        RecordTypes.SKIP_RECORDS.add(DataDrivenTypedData.class);
    }

    @Override
    public void addTypeAlias(AliasRegistrar registrar) {
        {
            var self = typedCompletionsClassPath(ItemType.get());
            var item = clazz(Item.class).markAsInput();
            var itemExplicit = wrapped("`.${%s}`", item);
            var itemTag = wrapped("`#${%s}`", RegistryTypes.tag("Item"));
            var mod = wrapped("`@${%s}`", SpecialTypes.MOD_ID);
            var recursive = Objects.requireNonNull(clazz(self).asInput()).asArray();
            var itemWrapper = union(item, itemExplicit, itemTag, mod, recursive);
            registrar.addInputAlias(self, itemWrapper.markAsInput());
        }

        var dataDrivenTypedDataList = new ArrayList<Type>();
        for (var type : DataDrivenTypes.instance().types()) {
            var skip = new AtomicBoolean(false);
            var ot = Types.object(builder -> {
                Objects.requireNonNull(builder);
                builder.param("type", literal(type.type()));
                switch (type.type()) {
                    case "sequential" -> {
                        builder.param("values", clazz(DataDrivenTypedData.class));
                        builder.param("else", clazz(ItemStackRestrictionEntryJS.class));
                    }
                    case "predicate" -> {
                        builder.param("condition", Types.OBJECT);
                        builder.param("return", clazz(ItemStackRestrictionEntryJS.class));
                    }
                    default -> skip.setPlain(true);
                }
            });
            if (skip.getPlain()) continue;
            dataDrivenTypedDataList.add(Objects.requireNonNull(ot));
        }

        registrar.addInputAlias(DataDrivenTypedData.class, union(Objects.requireNonNull(dataDrivenTypedDataList.toArray(Type[]::new))));
    }
}
