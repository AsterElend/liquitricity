package aster.liquitricity;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import team.reborn.energy.api.EnergyStorage;

public class FluidToEnergyItemWrapper implements EnergyStorage {
    private final ContainerItemContext context;

    public FluidToEnergyItemWrapper(ContainerItemContext context) {
        this.context = context;
    }

    // Safely look up the active FluidStorage context for this specific item stack instance
    private Storage<FluidVariant> getFluidStorage() {
        return FluidStorage.ITEM.find(context.getItemVariant().toStack(), context);
    }

    @Override
    public boolean supportsExtraction() {
        return true; // It actively acts as a power generator/provider
    }

    @Override
    public boolean supportsInsertion() {
        return false; // Cannot insert raw energy directly; it must be refilled with liquid
    }

    @Override
    public long getAmount() {
        Storage<net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant> fluidStorage = getFluidStorage();
        if (fluidStorage == null) return 0;

        // Iterate through fluid slots and count droplets if they match your crackle fluid
        long totalDroplets = 0;
        for (StorageView<FluidVariant> view : fluidStorage) {
            if (!view.isResourceBlank() && view.getResource().getFluid() == LiquitricityRegistry.CRACKLE_FLUID) {
                totalDroplets += view.getAmount();
            }
        }
        // 1 droplet = 1 energy
        return totalDroplets;
    }

    @Override
    public long getCapacity() {
        Storage<net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant> fluidStorage = getFluidStorage();
        if (fluidStorage == null) return 0;

        // Total capacity translates directly if the item was entirely filled with CRACKLE_FLUID
        long totalCapacity = 0;
        for (StorageView<net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant> view : fluidStorage) {
            totalCapacity += view.getCapacity();
        }
        return totalCapacity;
    }

    @Override
    public long extract(long maxAmount, TransactionContext transaction) {
        Storage<net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant> fluidStorage = getFluidStorage();
        if (fluidStorage == null || maxAmount <= 0) return 0;

        long extractedEnergy = 0;

        // Extract liquid droplets matching the requested energy amount
        for (StorageView<net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant> view : fluidStorage) {
            if (!view.isResourceBlank() && view.getResource().getFluid() == LiquitricityRegistry.CRACKLE_FLUID) {
                long neededDroplets = maxAmount - extractedEnergy;

                // Extract directly from the fluid API using the same transaction chain
                long extractedDroplets = fluidStorage.extract(view.getResource(), neededDroplets, transaction);
                extractedEnergy += extractedDroplets;

                if (extractedEnergy >= maxAmount) break;
            }
        }

        return extractedEnergy;
    }

    @Override
    public long insert(long maxAmount, TransactionContext transaction) {
        return 0; // Insertion is disabled
    }
}
