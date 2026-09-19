package aster.liquitricity.registry;

import aster.liquitricity.LiquitricityRegistry;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import team.reborn.energy.api.EnergyStorage;

import java.util.Iterator;

@SuppressWarnings({"experimental", "deprecation"})
public class LiquitricityDeviceBlockEntity extends BlockEntity {

    public LiquitricityDeviceBlockEntity(BlockPos pos, BlockState state){
        super(LiquitricityRegistry.LIQUITRICITY_ENTITY, pos, state);
    }

    public EnergyStorage createDynamicReceiver(Direction querySide) {
        return new EnergyStorage() {

            // --- ENERGY TO FLUID (Wires pushing power into the block) ---
            @Override
            public long insert(long potentialDroplets, TransactionContext transaction) {
                if (world == null || world.isClient || potentialDroplets <= 0) return 0;

                Direction oppositeSide = querySide.getOpposite();
                BlockPos targetPos = pos.offset(oppositeSide);

                // Querying the tank on the opposite side.
                // The tank sees the fluid entering from the side facing our block (querySide)
                Storage<FluidVariant> targetStorage = FluidStorage.SIDED.find(world, targetPos, querySide);
                if (targetStorage == null) return 0;

                Fluid customFluid = LiquitricityRegistry.CRACKLE_FLUID;
                FluidVariant variant = FluidVariant.of(customFluid);

                long dropletsAccepted = targetStorage.insert(variant, potentialDroplets, transaction);
                if (dropletsAccepted <= 0) return 0;

                transaction.addCloseCallback((tx, result) -> {
                    if (result.wasCommitted()) markDirty();
                });

                return dropletsAccepted;
            }

            // --- FLUID TO ENERGY (Wires pulling power out of the block) ---
            @Override
            public long extract(long maxAmount, TransactionContext transaction) {
                if (world == null || world.isClient || maxAmount <= 0) return 0;

                Direction oppositeSide = querySide.getOpposite();
                BlockPos targetPos = pos.offset(oppositeSide);

                // FIXED: The tank is losing fluid from its face touching our block.
                // That face corresponds to oppositeSide from the tank's perspective.
                Storage<FluidVariant> targetStorage = FluidStorage.SIDED.find(world, targetPos, oppositeSide);
                if (targetStorage == null) return 0;

                Fluid customFluid = LiquitricityRegistry.CRACKLE_FLUID;
                FluidVariant variant = FluidVariant.of(customFluid);

                // Extract the fluid from the tank using the transaction context
                long dropletsExtracted = targetStorage.extract(variant, maxAmount, transaction);
                if (dropletsExtracted <= 0) return 0;

                transaction.addCloseCallback((tx, result) -> {
                    if (result.wasCommitted()) markDirty();
                });

                return dropletsExtracted;
            }

            // FIXED: Wires check this to see if any power is available to extract.
            // We simulate looking at the adjacent fluid tank to see how much fluid is in there.
            @Override
            public long getAmount() {
                if (world == null || world.isClient) return 0;

                Direction oppositeSide = querySide.getOpposite();
                BlockPos targetPos = pos.offset(oppositeSide);
                Storage<FluidVariant> targetStorage = FluidStorage.SIDED.find(world, targetPos, oppositeSide);
                if (targetStorage == null) return 0;

                Fluid customFluid = LiquitricityRegistry.CRACKLE_FLUID;
                FluidVariant variant = FluidVariant.of(customFluid);



                // Counts how much of our specific fluid is inside that block's storage
                return getAmountOfFluid(targetStorage, variant);

            }

            @Override
            public long getCapacity() { return 65536; }

            // FIXED: Explicitly tell the network that extraction is supported
            @Override
            public boolean supportsExtraction() { return true; }

            @Override
            public boolean supportsInsertion() { return true; }
        };
    }

    public long getAmountOfFluid(Storage<FluidVariant> storage, FluidVariant targetFluid) {
        long totalAmount = 0;
        try (Transaction tx = Transaction.openOuter()) {
            for (Iterator<StorageView<FluidVariant>> it = storage.iterator(); it.hasNext(); ) {
                StorageView<FluidVariant> view = it.next();
                if (!view.isResourceBlank() && view.getResource().equals(targetFluid)) {
                    totalAmount += view.getAmount();
                }
            }
        }
        return totalAmount;
    }
}