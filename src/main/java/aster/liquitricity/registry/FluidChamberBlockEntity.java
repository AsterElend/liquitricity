package aster.liquitricity.registry;

import aster.liquitricity.LiquitricityRegistry;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class FluidChamberBlockEntity extends BlockEntity {
    // 4-slot inventory for holding the fluid containers
    private final SimpleInventory inventory = new SimpleInventory(4) {
        @Override
        public void markDirty() {
            super.markDirty();
            FluidChamberBlockEntity.this.markDirty();
        }

        @Override
        public int getMaxCountPerStack(){
            return 1;
        }
    };

    private final InventoryStorage itemStorage = InventoryStorage.of(inventory, null);

    public FluidChamberBlockEntity(BlockPos pos, BlockState state) {
        super(LiquitricityRegistry.CHAMBER_ENTITY, pos, state);
    }

    // This method is called by the Fluid API registration to find the available fluid space
    public Storage<FluidVariant> getFluidStorage() {
        List<Storage<FluidVariant>> parts = new ArrayList<>();

        // Loop through all 4 slots and check if the item inside can store fluid
        for (int i = 0; i < 4; i++) {
            Storage<ItemVariant> slotStorage = itemStorage.getSlot(i);
            ContainerItemContext context = ContainerItemContext.ofSingleSlot((SingleSlotStorage<ItemVariant>) slotStorage);
            Storage<FluidVariant> itemFluidStorage = context.find(FluidStorage.ITEM);

            if (itemFluidStorage != null) {
                parts.add(itemFluidStorage);
            }
        }

        // CombinedStorage treats all individual item storages as one single large tank
        return new CombinedStorage<>(parts);
    }

    // Handles Right-Click interaction logic
    // Handles Right-Click interaction logic
    public void handleInteraction(PlayerEntity player, Hand hand) {
        if (world == null || world.isClient) return;
        ItemStack held = player.getStackInHand(hand);

        if (held.isEmpty()) {
            // Empty hand: Pull out the first item found
            for (int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.getStack(i);
                if (!stack.isEmpty()) {
                    player.getInventory().insertStack(stack);

                    // 1. Clear the stack
                    inventory.setStack(i, ItemStack.EMPTY);

                    // 2. FORCE both markDirty and the world listeners update
                    inventory.markDirty();
                    this.markDirty();
                    return;
                }
            }
        } else {
            // Item in hand: Check if it can hold fluid before accepting it
            ContainerItemContext tempContext = ContainerItemContext.withConstant(held);
            if (tempContext.find(FluidStorage.ITEM) != null) {
                // Find an empty slot in the chamber
                for (int i = 0; i < inventory.size(); i++) {
                    if (inventory.getStack(i).isEmpty()) {
                        // Insert exactly 1 item from the player's hand
                        ItemStack toInsert = held.copy();
                        toInsert.setCount(1);
                        inventory.setStack(i, toInsert);
                        held.decrement(1);

                        // FORCE updates here as well
                        inventory.markDirty();
                        this.markDirty();
                        return;
                    }
                }
            }
        }
    }


    @Override
    public void readNbt(NbtCompound nbt){
        super.readNbt(nbt);
        inventory.clear();
        Inventories.readNbt(nbt, inventory.stacks);
    }

    @Override
    public void writeNbt(NbtCompound nbt){
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory.stacks, true);
    }


    public SimpleInventory getInventory(){
        return inventory;
}

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (world != null && !world.isClient) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }

}
