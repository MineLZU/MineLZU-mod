package io.github.minelzu.minelzu_mod.block

import net.minecraft.core.Direction
import net.minecraft.world.level.block.TransparentBlock
import net.minecraft.world.level.block.state.BlockState

class TranslucentGlassBlock(properties: Properties) : TransparentBlock(properties) {

    override fun getLightDampening(state: BlockState): Int = 12

    override fun skipRendering(state: BlockState, neighborState: BlockState, direction: Direction): Boolean = false
}
