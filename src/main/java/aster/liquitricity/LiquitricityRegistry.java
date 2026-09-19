package aster.liquitricity;


import aster.liquitricity.registry.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class LiquitricityRegistry {
    public static final List<Item> ADD_TO_CREATIVE_TAB = new ArrayList<>();

    public static final FlowableFluid CRACKLE_FLUID = registerFluid("crackle", new CrackleFluid.Still());
    public static final FlowableFluid CRACKLE_FLUID_FLOWING = registerFluid("crackle_flowing", new CrackleFluid.Flowing());


    public static final Item OHMARINE_ALLOY = registerItem("ohmarine_alloy", new Item(new FabricItemSettings()));
    public static final Item SMALL_FLUID_BATTERY = registerItem("small_fluid_battery", new FluidBatteryItem(new FabricItemSettings().maxCount(1), FluidConstants.BUCKET * 2));
    public static final Item MEDIUM_FLUID_BATTERY = registerItem("medium_fluid_battery", new FluidBatteryItem(new FabricItemSettings().maxCount(1), FluidConstants.BUCKET * 4));
    public static final Item LARGE_FLUID_BATTERY = registerItem("large_fluid_battery", new FluidBatteryItem(new FabricItemSettings().maxCount(1), FluidConstants.BUCKET * 8));
    public static final Item CRACKLE_BUCKET = registerItem("crackle_bucket", new BucketItem(CRACKLE_FLUID, new FabricItemSettings().maxCount(1)));
    public static final Item CRACKLE_BOTTLE = registerItem("crackle_bottle", new Item(new FabricItemSettings().maxCount(4)));

    public static final Block LIQUITRICITY_DEVICE = registerBlock("liquitricity_device", new LiquitricityDeviceBlock(FabricBlockSettings.copyOf(Blocks.CAULDRON)));
    public static final Block AQUALECTRIC_CHAMBER = registerBlock("fluid_chamber", new FluidChamberBlock(FabricBlockSettings.copyOf(Blocks.CAULDRON)));
    public static final Block PRISMARINE_LUMP = registerBlock("prismarine_lump", new Block(FabricBlockSettings.copyOf(Blocks.PRISMARINE).requiresTool()));
    public static final Block CRACKLE_FLUID_BLOCK = registerBlockNoItem("crackle", new CrackleFluidBlock(CRACKLE_FLUID, FabricBlockSettings.copyOf(Blocks.WATER).luminance(CrackleFluidBlock.STATE_TO_LUMINANCE)));

    public static final TagKey<Fluid> CRACKLE_TAG = TagKey.of(RegistryKeys.FLUID, Identifier.of("liquitricity", "crackle"));
    public static final TagKey<Item> PRISMARINE_BITS_TAG = TagKey.of(RegistryKeys.ITEM, Identifier.of("liquitricity", "prismarine_bits"));
    public static final BlockEntityType<LiquitricityDeviceBlockEntity> LIQUITRICITY_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of("liquitricity", "liquitricity_device"),
            FabricBlockEntityTypeBuilder.create(LiquitricityDeviceBlockEntity::new, LIQUITRICITY_DEVICE).build()
    );
    public static final BlockEntityType<FluidChamberBlockEntity> CHAMBER_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of("liquitricity", "fluid_chamber"),
            FabricBlockEntityTypeBuilder.create(FluidChamberBlockEntity::new, AQUALECTRIC_CHAMBER).build()
    );


    public static SoundEvent HUM_SOUND_EVENT = SoundEvent.of(Liquitricity.id("crackle_hum"));
    public static void register(){
        Registry.register(Registries.SOUND_EVENT, Liquitricity.id("crackle_hum"), HUM_SOUND_EVENT);

    };

    private static  Item registerItem(String name, Item item){
       Item trueItem =  Registry.register(Registries.ITEM, Identifier.of("liquitricity", name), item);
        ADD_TO_CREATIVE_TAB.add(trueItem);
        return trueItem;
     }

    private static Block registerBlock(String name, Block block){
      registerItem(name, new BlockItem(block, new FabricItemSettings()));
     return Registry.register(Registries.BLOCK, Identifier.of("liquitricity", name), block);
    }

    private static Block registerBlockNoItem(String name, Block block){
        return Registry.register(Registries.BLOCK, Identifier.of("liquitricity", name), block);
    }


    private static FlowableFluid registerFluid(String name, FlowableFluid fluid){
        return Registry.register(Registries.FLUID, Identifier.of("liquitricity", name), fluid);
    }

}
