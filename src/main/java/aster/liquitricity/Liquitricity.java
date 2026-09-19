package aster.liquitricity;

import aster.liquitricity.registry.FluidBatteryItem;
import aster.liquitricity.registry.LiquitricityTab;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.EmptyItemFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.FullItemFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.reborn.energy.api.EnergyStorage;

public class Liquitricity implements ModInitializer {
	public static final String MOD_ID = "liquitricity";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
		LiquitricityRegistry.register();
		LiquitricityTab.register();

		EnergyStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> {
			if (direction == null) return null;
			return blockEntity.createDynamicReceiver(direction);
		}, LiquitricityRegistry.LIQUITRICITY_ENTITY);
		Item[] batteries = {LiquitricityRegistry.SMALL_FLUID_BATTERY, LiquitricityRegistry.MEDIUM_FLUID_BATTERY, LiquitricityRegistry.LARGE_FLUID_BATTERY};

		ItemStorage.SIDED.registerForBlockEntity(((blockEntity, direction) ->
                InventoryStorage.of(blockEntity.getInventory(), null)),
				LiquitricityRegistry.CHAMBER_ENTITY);

		FluidStorage.ITEM.registerForItems((itemStack, context) -> {
			if (itemStack.getItem() instanceof FluidBatteryItem battery){
				return battery.getStorage();
			}
			return null;
		}, batteries);

		EnergyStorage.ITEM.registerForItems((itemStack, context) -> new FluidToEnergyItemWrapper(context), batteries);

		FluidStorage.SIDED.registerForBlockEntity(((blockEntity, direction) -> blockEntity.getFluidStorage()), LiquitricityRegistry.CHAMBER_ENTITY);

		FluidStorage.combinedItemApiProvider(Items.BUCKET).register(context -> {
			return new EmptyItemFluidStorage(
					context,
					LiquitricityRegistry.CRACKLE_BUCKET,
					LiquitricityRegistry.CRACKLE_FLUID,
					FluidConstants.BUCKET
			);
		});

		FluidStorage.combinedItemApiProvider(LiquitricityRegistry.CRACKLE_BUCKET).register(context -> {
			return new FullItemFluidStorage(
					context,
					Items.BUCKET,
					FluidVariant.of(LiquitricityRegistry.CRACKLE_FLUID),
					FluidConstants.BUCKET
			);
		});

			FluidStorage.combinedItemApiProvider(Items.GLASS_BOTTLE).register(context -> {
			return new EmptyItemFluidStorage(
					context,
					LiquitricityRegistry.CRACKLE_BOTTLE,
					LiquitricityRegistry.CRACKLE_FLUID,
					FluidConstants.BOTTLE
			);
		});

		FluidStorage.combinedItemApiProvider(LiquitricityRegistry.CRACKLE_BOTTLE).register(context -> {
			return new FullItemFluidStorage(
					context,
					Items.GLASS_BOTTLE,
					FluidVariant.of(LiquitricityRegistry.CRACKLE_FLUID),
					FluidConstants.BOTTLE
			);
		});





	}



	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}
}
