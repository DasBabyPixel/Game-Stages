package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item.datadriven;

import de.dasbabypixel.gamestages.common.addons.item.ItemStackRestrictionResolver;
import de.dasbabypixel.gamestages.common.addons.item.ItemStackRestrictionResolverFactories;
import de.dasbabypixel.gamestages.common.addons.item.ItemStackRestrictionResolverFactory;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.CompiledItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenTypedData;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.ItemStack;
import de.dasbabypixel.gamestages.common.data.RecompilationTask;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionPredicate;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.datadriven.VItemStackRestrictionEntrySettings;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network.DataDrivenSerializer;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network.DataDrivenType;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network.DataDrivenTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;

import static de.dasbabypixel.gamestages.common.v1_21_1.data.CommonCodecs.PREPARED_RESTRICTION_PREDICATE_STREAM_CODEC;

public class NeoDataDrivenTypes {
    public static void register(@NonNull DataDrivenTypes types, @NonNull ItemStackRestrictionResolverFactories factories) {
        types.register(new DataDrivenType<>(VItemStackRestrictionEntrySettings.class, "itemstack_restriction_entry_settings", DataDrivenSerializer.serializer(VItemStackRestrictionEntrySettings::encode, NeoItemStackRestrictionEntrySettings::new)));
        types.register(new DataDrivenType<>(ItemStackRestrictionEntry.class, "itemstack_restriction_entry", new DataDrivenSerializer<>() {
            @Override
            public @NonNull ItemStackRestrictionEntry deserialize(@NonNull RegistryFriendlyByteBuf buf) {
                return new ItemStackRestrictionEntry(PREPARED_RESTRICTION_PREDICATE_STREAM_CODEC.decode(buf), new NeoItemStackRestrictionEntrySettings(buf));
            }

            @Override
            public void serialize(@NonNull RegistryFriendlyByteBuf buf, @NonNull ItemStackRestrictionEntry data) {
                PREPARED_RESTRICTION_PREDICATE_STREAM_CODEC.encode(buf, data.predicate());
                ((VItemStackRestrictionEntrySettings) data.settings()).encode(buf);
            }
        }));
        factories.register(new BuiltinItemFactory());
    }

    private static class BuiltinItemFactory extends ItemStackRestrictionResolverFactory<BuiltinItemFactory.@NonNull Context> {
        public BuiltinItemFactory() {
            super("builtin_item");
        }

        @Override
        public @NonNull Context createContext(@NonNull RecompilationTask task) {
            return new Context(task);
        }

        @Override
        protected @NonNull ItemStackRestrictionResolver compileInternal(@NonNull DataDrivenTypedData<?> data, @NonNull Context context) {
            var entry = (ItemStackRestrictionEntry) Objects.requireNonNull(data.data());
            var compiledPredicate = context.task.predicateCompiler().compile(entry.predicate());
            CompiledItemStackRestrictionEntry compiled = new CompiledItemStackRestrictionEntry() {
                private final @NonNull CompiledRestrictionPredicate predicate = compiledPredicate;

                @Override
                public @NonNull CompiledRestrictionPredicate predicate() {
                    return predicate;
                }
            };
            return new ItemStackRestrictionResolver() {
                @Override
                public @NonNull CompiledItemStackRestrictionEntry resolveRestrictionEntry(@NonNull ItemStack itemStack) {
                    return compiled;
                }

                @Override
                public @NonNull List<@NonNull CompiledItemStackRestrictionEntry> restrictionEntries() {
                    return List.of(compiled);
                }
            };
        }

        private static class Context {
            private final @NonNull RecompilationTask task;

            public Context(@NonNull RecompilationTask task) {
                this.task = task;
            }
        }
    }
}
