package aster.liquitricity.registry;

import aster.liquitricity.Liquitricity;
import aster.liquitricity.LiquitricityRegistry;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.impl.transfer.fluid.FluidVariantImpl;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class FluidBatteryItem extends Item {
private final SingleVariantStorage<FluidVariant> storage;
public FluidBatteryItem(Settings settings, long capacity){
    super(settings);
    storage = new SingleVariantStorage<FluidVariant>() {
        @Override
        protected FluidVariant getBlankVariant() {
            return FluidVariant.blank();
        }

        @Override
        protected long getCapacity(FluidVariant variant) {
            return capacity;
        }
    };
}
public SingleVariantStorage<FluidVariant> getStorage()
{return storage;}

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (!storage.isResourceBlank()){
            long droplet = storage.getAmount();
            long bucket = droplet / FluidConstants.BUCKET;
            Text name = FluidVariantAttributes.getName(storage.getResource());
            tooltip.add(
                    Text.translatable("liquitricity.battery.contains")
                            .append(Text.of(Long.toString(bucket)))
                            .append(Text.translatable("liquitricity.battery.buckets")).append(name));

        }
        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack){
        return !storage.isResourceBlank();
    }



    @Override
    public int getItemBarStep(ItemStack stack) {
        // Scale the fluid amount to a 13-pixel wide bar
        return Math.round(13.0f * (float)  storage.getAmount() / (float) storage.getCapacity());
    }

    // 3. Set the color of the bar (e.g., Light Blue for water)
    @Override
    public int getItemBarColor(ItemStack stack) {
        return 0x00fff3;
    }

    @Override
    public void onItemEntityDestroyed(ItemEntity entity) {
    super.onItemEntityDestroyed(entity);
    if (entity.getWorld().isClient) return;
    if (storage.getResource().equals(LiquitricityRegistry.CRACKLE_FLUID) && storage.getAmount() >= FluidConstants.BUCKET * 8){
        if (entity.getWorld().isSkyVisible(entity.getBlockPos())){
            LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(entity.getWorld());
            if (lightning!=null){
                lightning.refreshPositionAfterTeleport(entity.getPos());
                entity.getWorld().spawnEntity(lightning);
            }
        }
    }
    }
}
