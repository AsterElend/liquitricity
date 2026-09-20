package aster.liquitricity.datagen;

import aster.liquitricity.LiquitricityRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;

public class LiquitricityModelProvider extends FabricModelProvider {
    public LiquitricityModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator generator) {
        generator.registerSimpleCubeAll(LiquitricityRegistry.LIQUITRICITY_DEVICE);
        generator.registerSimpleCubeAll(LiquitricityRegistry.PRISMARINE_LUMP);
    }

    @Override
    public void generateItemModels(ItemModelGenerator generator) {
        generator.register(LiquitricityRegistry.OHMARINE_ALLOY, Models.GENERATED);
        generator.register(LiquitricityRegistry.SMALL_FLUID_BATTERY, Models.GENERATED);
        generator.register(LiquitricityRegistry.MEDIUM_FLUID_BATTERY, Models.GENERATED);
        generator.register(LiquitricityRegistry.LARGE_FLUID_BATTERY, Models.GENERATED);
        generator.register(LiquitricityRegistry.CRACKLE_BUCKET, Models.GENERATED);
        generator.register(LiquitricityRegistry.CRACKLE_BOTTLE, Models.GENERATED);
    }
}
