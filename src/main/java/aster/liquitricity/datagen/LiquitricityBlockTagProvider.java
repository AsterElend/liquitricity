package aster.liquitricity.datagen;

import aster.liquitricity.LiquitricityRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.entity.ItemEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public class LiquitricityBlockTagProvider extends FabricTagProvider<Block> {

    public LiquitricityBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.BLOCK, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup lookup) {
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(
                LiquitricityRegistry.AQUALECTRIC_CHAMBER,
                LiquitricityRegistry.LIQUITRICITY_DEVICE,
                LiquitricityRegistry.PRISMARINE_LUMP
        );

    }
}
