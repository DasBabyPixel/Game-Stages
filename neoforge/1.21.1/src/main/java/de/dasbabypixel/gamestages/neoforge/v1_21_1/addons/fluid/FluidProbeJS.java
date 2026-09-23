package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.fluid;

import de.dasbabypixel.gamestages.common.v1_21_1.addons.fluid.FluidType;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonProbeJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.server.ServerRegisterEventJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.probejs.StagesProbeJSPlugin;
import moe.wolfgirl.probejs.plugin.builtins.alias.RegistryTypes;
import moe.wolfgirl.probejs.plugin.builtins.alias.SpecialTypes;
import moe.wolfgirl.probejs.typescript.base.AliasRegistrar;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

import static de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.probejs.StagesProbeJSPlugin.transformerRegistry;
import static moe.wolfgirl.probejs.typescript.document.Types.clazz;
import static moe.wolfgirl.probejs.typescript.document.Types.union;
import static moe.wolfgirl.probejs.typescript.document.Types.wrapped;

@NullMarked
public class FluidProbeJS implements NeoAddonProbeJS {
    static {
        registerTransformers();
    }

    @SuppressWarnings({"DataFlowIssue", "CodeBlock2Expr"})
    private static void registerTransformers() {
        var fluidCollection = StagesProbeJSPlugin.typedCollection(FluidType.get());
        var usingOnlyFluidCollection = StagesProbeJSPlugin.collectionUsingOnly(FluidType.get());

        transformerRegistry.register(ServerRegisterEventJS.class, (classDecl, methodDecl) -> {
            methodDecl.returnType = fluidCollection;
            methodDecl.params.getFirst().typeInfo = usingOnlyFluidCollection;
        }, "fluids");
        transformerRegistry.register(ServerRegisterEventJS.class, (classDecl, methodDecl) -> {
            methodDecl.params.get(1).typeInfo = usingOnlyFluidCollection;
        }, "restrictFluids");
    }

    @Override
    public void addTypeAlias(AliasRegistrar registrar) {
        var self = StagesProbeJSPlugin.typedCompletionsClassPath(FluidType.get());
        var fluid = RegistryTypes.object("Fluid");
        var fluidExplicit = wrapped("`.${%s}`", fluid);
        var fluidTag = wrapped("`#${%s}`", RegistryTypes.tag("Fluid"));
        var mod = wrapped("`@${%s}`", SpecialTypes.MOD_ID);
        var recursive = Objects.requireNonNull(clazz(self).asInput()).asArray();
        var fluidWrapper = union(fluid, fluidExplicit, fluidTag, mod, recursive);
        registrar.addInputAlias(self, fluidWrapper.markAsInput());
    }
}
