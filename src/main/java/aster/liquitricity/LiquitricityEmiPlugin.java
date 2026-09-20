package aster.liquitricity;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiWorldInteractionRecipe;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.text.Text;

public class LiquitricityEmiPlugin implements EmiPlugin {
    @Override
    public void register(EmiRegistry registry){
        registry.addRecipe(EmiWorldInteractionRecipe.builder()
                .leftInput(EmiStack.of(LiquitricityRegistry.CRACKLE_FLUID))
                .rightInput(EmiStack.of(Blocks.BLUE_ICE), true, slotWidget -> slotWidget.appendTooltip(Text.translatable("liquitricity.emi.tooltip.side")))
                .rightInput(EmiStack.of(Blocks.SOUL_SOIL), true, slotWidget -> slotWidget.appendTooltip(Text.translatable("liquitricity.emi.tooltip.down")))
                .output(EmiStack.of(Blocks.CALCITE))
                .build()
        );

        registry.addRecipe(
                EmiWorldInteractionRecipe.builder()
                        .leftInput(EmiStack.of(Fluids.LAVA), slotWidget -> slotWidget.appendTooltip(Text.translatable("liquitricity.emi.tooltip.flowing")))
                        .rightInput(EmiStack.of(LiquitricityRegistry.CRACKLE_FLUID), true)
                        .output(EmiStack.of(LiquitricityRegistry.PRISMARINE_LUMP))
                        .build()
        );       registry.addRecipe(
                EmiWorldInteractionRecipe.builder()
                        .leftInput(EmiStack.of(Fluids.LAVA), slotWidget -> {
                            return slotWidget.appendTooltip(Text.translatable("liquitricity.emi.tooltip.still"));
                        })
                        .rightInput(EmiStack.of(LiquitricityRegistry.CRACKLE_FLUID), true)
                        .output(EmiStack.of(Blocks.SEA_LANTERN))
                        .build()
        );
    }
}
