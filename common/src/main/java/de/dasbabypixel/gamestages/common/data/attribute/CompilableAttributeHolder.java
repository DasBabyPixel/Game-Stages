package de.dasbabypixel.gamestages.common.data.attribute;

import de.dasbabypixel.gamestages.common.data.compilation.CompilableResource;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface CompilableAttributeHolder<H extends CompilableAttributeHolder<H, PC, C>, PC extends CompilableAttributeHolder.PreCompiled<PC, C>, C> extends IAttributeHolder<H>, CompilableResource<H, PC, SDH, C> {
    <T> T get(CompilableAttribute<? super H, ?, ?, ?, T> attribute);

    interface PreCompiled<H extends PreCompiled<H, C>, C> extends CompilableResource.PreCompiled<H, C>, IAttributeHolder<H> {
    }
}
