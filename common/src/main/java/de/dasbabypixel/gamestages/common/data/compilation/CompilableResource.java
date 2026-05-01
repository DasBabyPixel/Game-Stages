package de.dasbabypixel.gamestages.common.data.compilation;

import org.jspecify.annotations.NullMarked;

/**
 * A compilable resource is a resource that can be compiled on a per-stages basis.
 * A compilable resource often has a precompiled, shared state that can be used to reduce the load on the per-stages compilation.
 *
 * @param <PreCompiled>
 */
@NullMarked
public interface CompilableResource<DataHolder, Compiled> {
    Compiled compile(DataHolder dataHolder);
}
