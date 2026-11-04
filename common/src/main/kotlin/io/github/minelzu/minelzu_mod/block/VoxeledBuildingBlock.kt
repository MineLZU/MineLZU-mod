package io.github.minelzu.minelzu_mod.block

import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

open class VoxeledBuildingBlock(properties: Properties, protected val shape: VoxelShape, ) : Block(properties) {
    protected open fun baseShape(): VoxelShape = shape

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape = baseShape()

    override fun getCollisionShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext, ): VoxelShape = baseShape()
}
