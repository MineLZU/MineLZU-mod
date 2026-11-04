package io.github.minelzu.minelzu_mod.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Mirror
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

open class HorizontalWallBlock(
    properties: BlockBehaviour.Properties,
    private val box: DoubleArray,
) : VoxeledBuildingBlock(properties, shapeOf(box, false)) {

    init {
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH))
    }

    override fun baseShape(): VoxelShape = shapeOf(box, false)

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape =
        shapeOf(box, isRotated(state))

    override fun getCollisionShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext,
    ): VoxelShape = shapeOf(box, isRotated(state))

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
        defaultBlockState().setValue(FACING, context.horizontalDirection)

    override fun rotate(state: BlockState, rotation: Rotation): BlockState =
        state.setValue(FACING, rotation.rotate(state.getValue(FACING)))

    override fun mirror(state: BlockState, mirror: Mirror): BlockState =
        state.rotate(mirror.getRotation(state.getValue(FACING)))

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING)
    }

    private fun isRotated(state: BlockState): Boolean = when (state.getValue(FACING)) {
        Direction.EAST, Direction.WEST -> true
        else -> false
    }

    companion object {
        val FACING: EnumProperty<Direction> = BlockStateProperties.HORIZONTAL_FACING
    }
}

private fun shapeOf(box: DoubleArray, rotated: Boolean): VoxelShape = if (rotated) {
    Shapes.box(box[2], box[1], box[0], box[5], box[4], box[3])
} else {
    Shapes.box(box[0], box[1], box[2], box[3], box[4], box[5])
}
