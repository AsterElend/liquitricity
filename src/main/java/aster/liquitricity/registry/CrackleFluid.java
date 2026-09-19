package aster.liquitricity.registry;

import aster.liquitricity.LiquitricityRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@SuppressWarnings("deprecation")
public abstract class CrackleFluid extends FlowableFluid {
    public CrackleFluid() {
    }


    public Fluid getFlowing() {
        return LiquitricityRegistry.CRACKLE_FLUID_FLOWING;
    }

    public Fluid getStill() {
        return LiquitricityRegistry.CRACKLE_FLUID;
    }

    public Item getBucketItem() {
        return LiquitricityRegistry.CRACKLE_BUCKET;
    }

    @Override
    public void randomDisplayTick(World world, BlockPos pos, FluidState state, Random random) {
        if (random.nextInt(2048) == 0) {
                world.playSound((double)pos.getX() + (double)0.5F, (double)pos.getY() + (double)0.5F,
                        (double)pos.getZ() + (double)0.5F, LiquitricityRegistry.HUM_SOUND_EVENT, SoundCategory.BLOCKS,
                        random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 1.5F, false);
        }
         if (random.nextInt(16) == 0) {
            world.addParticle(ParticleTypes.SCRAPE, (double)pos.getX() + random.nextDouble(), (double)pos.getY() + random.nextDouble() + 0.5f*((double) state.getLevel() /15),
                    (double)pos.getZ() + random.nextDouble(), 0.0F, 0.0F, 0.0F);
        }

    }

    @Nullable
    public ParticleEffect getParticle() {
        return ParticleTypes.SCRAPE;
    }
    @Override
    protected boolean isInfinite(World world) {
        return false;
    }
    @Override
    protected void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
        Block.dropStacks(state, world, pos, blockEntity);
    }
    @Override
    public int getFlowSpeed(WorldView world) {
        return 3;
    }
    @Override
    public BlockState toBlockState(FluidState state) {
        return LiquitricityRegistry.CRACKLE_FLUID_BLOCK.getDefaultState().with(FluidBlock.LEVEL, getBlockStateLevel(state));
    }
    @Override
    public boolean matchesType(Fluid fluid) {
        return fluid == LiquitricityRegistry.CRACKLE_FLUID || fluid == LiquitricityRegistry.CRACKLE_FLUID_FLOWING;
    }
    @Override
    public int getLevelDecreasePerBlock(WorldView world) {
        return 1;
    }
    @Override
    public int getTickRate(WorldView world) {
        return 5;
    }
    @Override
    public boolean canBeReplacedWith(FluidState state, BlockView world, BlockPos pos, Fluid fluid, Direction direction) {
        return direction == Direction.DOWN && !fluid.isIn(LiquitricityRegistry.CRACKLE_TAG);
    }

    @Override
    protected float getBlastResistance() {
        return 100.0F;
    }
    @Override
    public Optional<SoundEvent> getBucketFillSound() {
        return Optional.of(SoundEvents.ITEM_BUCKET_FILL);
    }

    public static class Still extends CrackleFluid {

        public Still() {
        }
        @Override
        public int getLevel(FluidState state) {
            return 8;
        }
        @Override
        public boolean isStill(FluidState state) {
            return true;
        }
    }

    public static class Flowing extends CrackleFluid {
        public Flowing() {
        }
        @Override
        protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
            super.appendProperties(builder);
            builder.add(LEVEL);
        }
        @Override
        public int getLevel(FluidState state) {
            return state.get(LEVEL);
        }
        @Override
        public boolean isStill(FluidState state) {
            return false;
        }
    }



}
