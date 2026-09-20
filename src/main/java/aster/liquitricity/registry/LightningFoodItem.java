package aster.liquitricity.registry;

import aster.liquitricity.LiquitricityRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class LightningFoodItem extends Item {
    public LightningFoodItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        // Always call the super method first so the player actually eats the food
        ItemStack resultStack = super.finishUsing(stack, world, user);
        if (user instanceof PlayerEntity player && !player.getAbilities().creativeMode){
            resultStack.decrement(1);
            if (resultStack.isEmpty()){
                resultStack = new ItemStack(Items.GLASS_BOTTLE);
            }
            if (!player.getInventory().insertStack(new ItemStack(Items.GLASS_BOTTLE))){
                player.dropItem(new ItemStack(Items.GLASS_BOTTLE), false);
            }
        }
        // Logic must run on the logical server side
        if (!world.isClient()) {
            ServerWorld serverWorld = (ServerWorld) world;
            Vec3d pos = user.getPos();

            // Create the lightning entity
            LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(serverWorld);
            if (lightning != null) {
                // Set the position and spawn the lightning
                lightning.refreshPositionAfterTeleport(pos.x, pos.y, pos.z);
                serverWorld.spawnEntity(lightning);
            }
        }

        return resultStack;
    }

   @Override
   public UseAction getUseAction(ItemStack stack) {
       return UseAction.DRINK;
   }
   @Override
   public int getMaxUseTime(ItemStack stack) {
       return 40;
   }

    @Override
    public SoundEvent getDrinkSound() {
        return SoundEvents.ENTITY_GENERIC_DRINK;
    }

    @Override
    public SoundEvent getEatSound() {
        return LiquitricityRegistry.HUM_SOUND_EVENT;
    }
}
