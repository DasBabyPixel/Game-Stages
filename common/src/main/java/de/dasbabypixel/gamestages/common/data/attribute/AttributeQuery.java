package de.dasbabypixel.gamestages.common.data.attribute;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
public interface AttributeQuery<H extends AttributeHolder<? extends H>, T> {
    T get(H holder);

    static <H extends AttributeHolder<? extends H>, T> Holder<H, T> holder() {
        return new Holder<>();
    }

    final class Holder<H extends AttributeHolder<? extends H>, T> implements AttributeQuery<H, T> {
        private final Attribute<H, HolderInstance<T>> attribute = new Attribute<>(HolderInstance::new);

        public void init(H holder, T value) {
            holder.get(attribute).value = Objects.requireNonNull(value);
        }

        @Override
        public T get(H holder) {
            return Objects.requireNonNull(holder.get(attribute).value);
        }

        private static final class HolderInstance<T> {
            private @Nullable T value;
        }
    }
}
