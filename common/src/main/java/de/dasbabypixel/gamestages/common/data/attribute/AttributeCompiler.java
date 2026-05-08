package de.dasbabypixel.gamestages.common.data.attribute;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class AttributeCompiler<Holder> extends SimpleAttributeHolder<AttributeCompiler<Holder>> {
    private final Holder holder;

    public AttributeCompiler(Holder holder) {
        this.holder = holder;
    }

    public Holder holder() {
        return holder;
    }
}
