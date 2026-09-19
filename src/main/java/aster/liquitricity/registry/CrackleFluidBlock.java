package aster.liquitricity.registry;

import aster.liquitricity.Liquitricity;
import aster.liquitricity.LiquitricityRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;

import java.util.function.ToIntFunction;

public class CrackleFluidBlock extends FluidBlock {
    private final FlowableFluid fluid;
    public static final ToIntFunction<BlockState> STATE_TO_LUMINANCE = (state) -> {
        int level = state.get(FluidBlock.LEVEL);
        if (level == 0) return 15;
        return Math.max(0, 15-(level * 2));
    };


    public CrackleFluidBlock(FlowableFluid fluid, Settings settings) {
        super(fluid, settings);
        this.fluid = fluid;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        // Ensure this runs on the server side and target is a LivingEntity
        if (world.isClient() || !(entity instanceof LivingEntity livingEntity)) {
            super.onEntityCollision(state, world, pos, entity);
            return;
        }

        Vec3d resistanceMultiplier = new Vec3d(0.50D, 1d, 0.50D);

        // Apply damage once every second (20 ticks)
        if (world.getTime() % 20 == 0) {
            ServerWorld serverWorld = (ServerWorld) world;
            DamageSource damageSource = serverWorld.getDamageSources().lightningBolt(); //

            livingEntity.playSound(SoundEvents.BLOCK_CHAIN_BREAK, 1, 4);
            livingEntity.damage(damageSource, 4.0F); // 4.0F = 2 hearts of damage
        }

        entity.setVelocity(entity.getVelocity().multiply(resistanceMultiplier));
        entity.velocityModified = true;
        super.onEntityCollision(state, world, pos, entity);
    }



    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (this.receiveNeighborFluids(world, pos, state)) {
            world.scheduleFluidTick(pos, state.getFluidState().getFluid(), this.fluid.getTickRate(world));
        }
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (this.receiveNeighborFluids(world, pos, state)) {
            world.scheduleFluidTick(pos, state.getFluidState().getFluid(), this.fluid.getTickRate(world));
        }
    }

    @Override
    public boolean receiveNeighborFluids(World world, BlockPos pos, BlockState state) {
        // 1. Check if the block directly below is Soul Soil
        BlockState blockBelow = world.getBlockState(pos.down());
        if (blockBelow.isOf(Blocks.SOUL_SOIL)) {

            // 2. Loop through horizontal directions to see if any neighbor is Blue Ice
            for (Direction direction : Direction.Type.HORIZONTAL) {
                BlockPos neighborPos = pos.offset(direction);
                BlockState neighborState = world.getBlockState(neighborPos);

                if (neighborState.isOf(Blocks.BLUE_ICE)) {
                    // Turn this fluid block into Calcite
                    world.setBlockState(pos, Blocks.CALCITE.getDefaultState());
                    this.playFizzSound(world, pos);
                    return false; // Stop further fluid processing
                }
            }
        }

        for (Direction direction : Direction.values()) {

            BlockPos neighborPos = pos.offset(direction);
            if (world.getFluidState(neighborPos).isIn(FluidTags.LAVA)) {

                Block outputBlock = LiquitricityRegistry.PRISMARINE_LUMP;
                if (world.getFluidState(neighborPos).isStill()){
                    outputBlock = Blocks.SEA_LANTERN;
                }
                if (direction == Direction.UP && world.getFluidState(pos).isStill()){
                    world.setBlockState(pos, outputBlock.getDefaultState());
                } else if (direction != Direction.UP){
                    world.setBlockState(neighborPos, outputBlock.getDefaultState());
                }


                this.playFizzSound(world, pos);
                return false;
            }
        }



        return true;
    }
    private void playFizzSound(World world, BlockPos pos) {
        world.syncWorldEvent(1501, pos, 0); // Vanilla extinguish/fizz sound event
    }





}