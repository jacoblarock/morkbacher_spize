package com.jacoblarock.morkbacher_spize.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SpizeBlock extends Block implements SimpleWaterloggedBlock {
    public static final int minCount = 1;
    public static final int maxCount = 4;
    public static final IntegerProperty count = IntegerProperty.create(
        "spize_block_count",
        1,
        4
    );
    public static final BooleanProperty waterlogged = BlockStateProperties.WATERLOGGED;

    protected static final VoxelShape oneShape = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 7.0D, 10.0D);
    protected static final VoxelShape twoShape = Shapes.or(
        Block.box(3.0D, 0.0D, 4.0D, 7.0D, 7.0D, 8.0D),
        Block.box(9.0D, 0.0D, 5.0D, 13.0D, 7.0D, 9.0D)
    );
    protected static final VoxelShape threeShape = Shapes.or(
        Block.box(3.0D, 0.0D, 3.0D, 7.0D, 7.0D, 7.0D),
        Block.box(9.0D, 0.0D, 2.0D, 13.0D, 7.0D, 6.0D),
        Block.box(4.0D, 0.0D, 9.0D, 8.0D, 7.0D, 13.0D)
    );
    protected static final VoxelShape fourShape = Shapes.or(
        Block.box(3.0D, 0.0D, 3.0D, 7.0D, 7.0D, 7.0D),
        Block.box(9.0D, 0.0D, 2.0D, 13.0D, 7.0D, 6.0D),
        Block.box(4.0D, 0.0D, 9.0D, 8.0D, 7.0D, 13.0D),
        Block.box(8.0D, 0.0D, 8.0D, 12.0D, 7.0D, 12.0D)
    );

    public SpizeBlock() {
        super(
            BlockBehaviour.Properties.of()
                .strength(0.1F)
                .sound(SoundType.GLASS)
                .noOcclusion()
                .lightLevel((state) -> state.getValue(count) * 2)
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(count, 1).setValue(waterlogged, false));
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return switch (state.getValue(count)) {
            case 2 -> twoShape;
            case 3 -> threeShape;
            case 4 -> fourShape;
            default -> oneShape;
        };
    }

    @Override
    public boolean canBeReplaced(@NotNull BlockState state, BlockPlaceContext context) {
        return !context.isSecondaryUseActive() && context.getItemInHand().getItem() == this.asItem() && state.getValue(count) < maxCount || super.canBeReplaced(state, context);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos());
        if (blockstate.is(this)) {
            return blockstate.setValue(count, Math.min(maxCount, blockstate.getValue(count) + 1));
        } else {
            FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
            boolean flag = fluidstate.getType() == Fluids.WATER;
            return Objects.requireNonNull(super.getStateForPlacement(context)).setValue(waterlogged, flag);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(count, waterlogged);
    }

    @Override
    public @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(waterlogged) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }
}