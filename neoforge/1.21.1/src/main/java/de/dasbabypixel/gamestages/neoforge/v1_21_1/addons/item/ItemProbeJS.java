package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item;

import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonProbeJS;
import moe.wolfgirl.probejs.lang.typescript.ScriptDump;
import org.jspecify.annotations.NullMarked;

import static moe.wolfgirl.probejs.lang.typescript.code.type.Types.*;

@NullMarked
public class ItemProbeJS implements NeoAddonProbeJS {
    @Override
    public void assignType(ScriptDump scriptDump) {
        var item = or(primitive("`${Special.Item}`"), primitive("`.${Special.Item}`"), primitive("`#${Special.ItemTag}`"), primitive("`@${Special.Mod}`"), type(ItemCollectionWrapper.class).asArray());
        scriptDump.assignType(ItemCollectionWrapper.class, item);
    }
}
