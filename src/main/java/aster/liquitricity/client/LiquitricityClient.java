package aster.liquitricity.client;

import aster.liquitricity.LiquitricityRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.util.Identifier;

public class LiquitricityClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Register the handler for BOTH the still and flowing fluid variants
        FluidRenderHandlerRegistry.INSTANCE.register(
                LiquitricityRegistry.CRACKLE_FLUID,
                LiquitricityRegistry.CRACKLE_FLUID_FLOWING,
                new SimpleFluidRenderHandler(
                        Identifier.of("liquitricity", "block/crackle"),
                        Identifier.of("liquitricity", "block/crackle")
                )
        );

        // Map both fluids to render translucency
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(),
                LiquitricityRegistry.CRACKLE_FLUID,
                LiquitricityRegistry.CRACKLE_FLUID_FLOWING
        );

        BlockEntityRendererFactories.register(LiquitricityRegistry.CHAMBER_ENTITY, FluidChamberBlockEntityRenderer::new);
    }
}
