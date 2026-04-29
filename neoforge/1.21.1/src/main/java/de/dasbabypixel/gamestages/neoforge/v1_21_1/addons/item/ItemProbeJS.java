package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item;

import com.google.common.collect.Multimap;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenTypedData;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network.DataDrivenTypes;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonProbeJS;
import moe.wolfgirl.probejs.lang.java.clazz.ClassPath;
import moe.wolfgirl.probejs.lang.typescript.ScriptDump;
import moe.wolfgirl.probejs.lang.typescript.code.member.TypeDecl;
import moe.wolfgirl.probejs.lang.typescript.code.type.BaseType;
import moe.wolfgirl.probejs.lang.typescript.code.type.Types;
import org.jspecify.annotations.NullMarked;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.ArrayList;
import java.util.Objects;

import static moe.wolfgirl.probejs.lang.typescript.code.type.Types.or;
import static moe.wolfgirl.probejs.lang.typescript.code.type.Types.primitive;
import static moe.wolfgirl.probejs.lang.typescript.code.type.Types.type;

@NullMarked
public class ItemProbeJS implements NeoAddonProbeJS {
    private static final VarHandle CONVERTIBLES;

    static {
        try {
            var lookup = MethodHandles.privateLookupIn(ScriptDump.class, MethodHandles.lookup());
            CONVERTIBLES = Objects.requireNonNull(lookup.findVarHandle(ScriptDump.class, "convertibles", Multimap.class));
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void assignType(ScriptDump scriptDump) {
        var item = or(primitive("`${Special.Item}`"), primitive("`.${Special.Item}`"), primitive("`#${Special.ItemTag}`"), primitive("`@${Special.Mod}`"), type(ItemCollectionWrapper.class).asArray());
        scriptDump.assignType(ItemCollectionWrapper.class, item);

        var dataDrivenTypedDataList = new ArrayList<BaseType>();
        for (var type : DataDrivenTypes.instance().types()) {
            var builder = Types.object();
            builder.member("type", Types.literal(type.type()));
            switch (type.type()) {
                case "sequential" -> {
                    builder.member("values", Types.type(DataDrivenTypedData.class));
                    builder.member("else", Types.type(ItemKJS.RegisteredItemStackEntries.Entry.class));
                }
                case "predicate" -> {
                    builder.member("condition", Types.OBJECT);
                    builder.member("return", Types.type(ItemKJS.RegisteredItemStackEntries.Entry.class));
                }
                default -> {
                    continue;
                }
            }
            dataDrivenTypedDataList.add(Objects.requireNonNull(builder.build()));
        }

        Multimap<ClassPath, TypeDecl> convertibles = (Multimap<ClassPath, TypeDecl>) Objects.requireNonNull(CONVERTIBLES.get(scriptDump));
        convertibles.removeAll(new ClassPath(DataDrivenTypedData.class));

        scriptDump.assignType(DataDrivenTypedData.class, Types.or(Objects.requireNonNull(dataDrivenTypedDataList.toArray(BaseType[]::new))));
    }
}
