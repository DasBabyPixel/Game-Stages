package de.dasbabypixel.gamestages.common.data.compilation;

import org.jspecify.annotations.NullMarked;

/**
 * A compilable resource is a resource that can be compiled on a per-stages basis.
 * A compilable resource often has a precompiled, shared state that can be used to reduce the load on the per-stages compilation.
 *
 * @param <PreCompiled>
 */
@NullMarked
public interface CompilableResource<SharedDataHolder, PreCompiled extends CompilableResource.PreCompiled<SingleDataHolder, Compiled>, SingleDataHolder, Compiled> {
    PreCompiled precompile(SharedDataHolder dataHolder);

    interface PreCompiled<SingleDataHolder, Compiled> {
        Compiled compile(SingleDataHolder dataHolder);
    }
}
