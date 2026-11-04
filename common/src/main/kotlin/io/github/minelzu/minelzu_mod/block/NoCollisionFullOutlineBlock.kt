package io.github.minelzu.minelzu_mod.block

import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class NoCollisionFullOutlineBlock(properties: BlockBehaviour.Properties) :
    HorizontalWallBlock(properties, FULL_BOX) {

    override fun getCollisionShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext,
    ): VoxelShape = Shapes.empty()

    override fun getVisualShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext,
    ): VoxelShape = Shapes.empty()
}

private val FULL_BOX: DoubleArray = doubleArrayOf(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)
