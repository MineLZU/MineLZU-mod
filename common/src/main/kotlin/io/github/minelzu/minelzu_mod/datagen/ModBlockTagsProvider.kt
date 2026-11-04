package io.github.minelzu.minelzu_mod.datagen

import io.github.minelzu.minelzu_mod.registry.ModBlocks
import java.util.concurrent.CompletableFuture
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.TagsProvider
import net.minecraft.tags.BlockTags
import net.minecraft.world.level.block.Block

class ModBlockTagsProvider(
    output: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider>,
) : TagsProvider<Block>(output, Registries.BLOCK, lookupProvider) {

    override fun addTags(registries: HolderLookup.Provider) {
        tag(BlockTags.WALLS).add(ModBlocks.ow01.getKey()).add(ModBlocks.ow02.getKey())
    }
}


