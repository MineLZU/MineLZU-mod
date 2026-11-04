package io.github.minelzu.minelzu_mod.datagen

import com.google.gson.JsonObject
import io.github.minelzu.minelzu_mod.MineLZU
import io.github.minelzu.minelzu_mod.datagen.model.Element
import io.github.minelzu.minelzu_mod.datagen.model.ElementModels
import io.github.minelzu.minelzu_mod.datagen.model.elementModel
import io.github.minelzu.minelzu_mod.registry.ModBlocks
import io.github.minelzu.minelzu_mod.registry.ModItems
import java.util.concurrent.CompletableFuture
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.BlockModelGenerators.NOP
import net.minecraft.client.data.models.BlockModelGenerators.ROTATION_HORIZONTAL_FACING
import net.minecraft.client.data.models.BlockModelGenerators.Y_ROT_180
import net.minecraft.client.data.models.BlockModelGenerators.Y_ROT_270
import net.minecraft.client.data.models.BlockModelGenerators.Y_ROT_90
import net.minecraft.client.data.models.BlockModelGenerators.createSimpleBlock
import net.minecraft.client.data.models.BlockModelGenerators.createSlab
import net.minecraft.client.data.models.BlockModelGenerators.createStairs
import net.minecraft.client.data.models.BlockModelGenerators.plainVariant
import net.minecraft.client.data.models.ItemModelOutput
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator
import net.minecraft.client.data.models.blockstates.PropertyDispatch
import net.minecraft.client.data.models.model.ItemModelUtils
import net.minecraft.client.data.models.model.ModelInstance
import net.minecraft.client.renderer.block.dispatch.BlockStateModelDispatcher
import net.minecraft.client.renderer.block.dispatch.VariantMutator
import net.minecraft.client.renderer.item.ClientItem
import net.minecraft.client.renderer.item.ItemModel
import net.minecraft.core.Direction
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.minecraft.resources.Identifier
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.properties.BlockStateProperties

class ModModelProvider(private val output: PackOutput) : DataProvider {

    private val blockStates: MutableList<BlockModelDefinitionGenerator> = mutableListOf()

    private val itemDefinitions: MutableMap<Item, ItemModel.Unbaked> = LinkedHashMap()

    private val models: MutableMap<Identifier, ModelInstance> = LinkedHashMap()

    override fun run(cache: CachedOutput): CompletableFuture<*> {
        val blockModels = BlockModelGenerators(
            { generator -> blockStates += generator },
            ItemCollector(itemDefinitions),
            { model, instance -> models[model] = instance },
        )
        registerModels(blockModels)
        val blockStatePath = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates")
        val itemPath = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "items")
        val modelPath = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models")
        val futures = mutableListOf<CompletableFuture<*>>()
        blockStates.forEach { generator ->
            val id = BuiltInRegistries.BLOCK.getKey(generator.block())
            futures += DataProvider.saveStable(cache, BlockStateModelDispatcher.CODEC, generator.create(), blockStatePath.json(id))
        }
        itemDefinitions.forEach { (item, model) ->
            val id = BuiltInRegistries.ITEM.getKey(item)
            futures += DataProvider.saveStable(cache, ClientItem.CODEC, ClientItem(model, ClientItem.Properties.DEFAULT), itemPath.json(id))
        }
        models.forEach { (id, instance) ->
            futures += DataProvider.saveStable(cache, instance.get(), modelPath.json(id))
        }
        return CompletableFuture.allOf(*futures.toTypedArray())
    }

    override fun getName(): String = "MineLZU block states and models"

    private fun registerModels(blockModels: BlockModelGenerators) {
        cubes.forEach { cube(blockModels, it) }
        glassBlocks.forEach { glass(blockModels, it) }
        shapedBases.forEach { shaped(blockModels, it) }
        elementBlocks.forEach { element(blockModels, it) }
    }

    private fun cube(blockModels: BlockModelGenerators, block: TexturedBlock) {
        write(blockModels, block.model, cubeAll(block.texture, null))
        simpleState(blockModels, block)
        item(blockModels, block)
    }

    private fun glass(blockModels: BlockModelGenerators, block: TexturedBlock) {
        write(blockModels, block.model, cubeAll(block.texture, false))
        simpleState(blockModels, block)
        item(blockModels, block)
    }

    private fun shaped(blockModels: BlockModelGenerators, block: TexturedBlock) {
        val slab = block.model("_slab")
        val slabTop = block.model("_slab_top")
        val stairs = block.model("_stairs")
        val stairsInner = block.model("_stairs_inner")
        val stairsOuter = block.model("_stairs_outer")
        write(blockModels, slab, textured("minecraft:block/slab", block.texture))
        write(blockModels, slabTop, textured("minecraft:block/slab_top", block.texture))
        write(blockModels, stairs, textured("minecraft:block/stairs", block.texture))
        write(blockModels, stairsInner, textured("minecraft:block/inner_stairs", block.texture))
        write(blockModels, stairsOuter, textured("minecraft:block/outer_stairs", block.texture))
        blockModels.blockStateOutput.accept(
            createSlab(blockOf("${block.name}_slab"), plainVariant(slab), plainVariant(slabTop), plainVariant(block.model)),
        )
        blockModels.blockStateOutput.accept(
            createStairs(
                blockOf("${block.name}_stairs"),
                plainVariant(stairsInner),
                plainVariant(stairs),
                plainVariant(stairsOuter),
            ),
        )
        item(blockModels, block.withSuffix("_stairs"))
        item(blockModels, block.withSuffix("_slab"))
    }

    private fun element(blockModels: BlockModelGenerators, entry: ElementBlock) {
        val block = entry.block
        write(blockModels, block.model, elementModel(block.texture, entry.elements))
        val dispatch = MultiVariantGenerator.dispatch(blockOf(block.name), plainVariant(block.model))
        val facing = entry.facing
        blockModels.blockStateOutput.accept(if (facing == null) dispatch else dispatch.with(facing))
        item(blockModels, block)
    }

    private fun simpleState(blockModels: BlockModelGenerators, block: TexturedBlock) {
        blockModels.blockStateOutput.accept(createSimpleBlock(blockOf(block.name), plainVariant(block.model)))
    }

    private fun item(blockModels: BlockModelGenerators, block: TexturedBlock) {
        val itemModel = MineLZU.id("item/${block.name}")
        val item = ModItems.all.getValue(block.name).get()
        blockModels.itemModelOutput.accept(item, ItemModelUtils.plainModel(itemModel))
        write(blockModels, itemModel, JsonObject().apply { addProperty("parent", block.model.toString()) })
    }

    private fun blockOf(name: String): Block = ModBlocks.all.getValue(name).get()

    private fun write(blockModels: BlockModelGenerators, model: Identifier, json: JsonObject) {
        blockModels.modelOutput.accept(model, ModelInstance { json })
    }

    private fun cubeAll(texture: Identifier, ambientOcclusion: Boolean?): JsonObject = JsonObject().apply {
        addProperty("parent", "minecraft:block/cube_all")
        add("textures", JsonObject().apply { addProperty("all", texture.toString()) })
        ambientOcclusion?.let { addProperty("ambientocclusion", it) }
    }

    private fun textured(parent: String, texture: Identifier): JsonObject = JsonObject().apply {
        addProperty("parent", parent)
        add("textures", JsonObject().apply {
            addProperty("bottom", texture.toString())
            addProperty("top", texture.toString())
            addProperty("side", texture.toString())
        })
    }

    private class ItemCollector(
        private val definitions: MutableMap<Item, ItemModel.Unbaked>,
    ) : ItemModelOutput {

        override fun accept(item: Item, model: ItemModel.Unbaked, properties: ClientItem.Properties) {
            definitions[item] = model
        }

        override fun copy(donor: Item, acceptor: Item) {
            definitions[acceptor] = definitions.getValue(donor)
        }
    }

    private data class TexturedBlock(val path: String) {

        val name: String = path.substringAfter('/')

        val model: Identifier = MineLZU.id("block/$path")

        val texture: Identifier = MineLZU.id("block/$path")

        fun model(suffix: String): Identifier = MineLZU.id("block/$path$suffix")

        fun withSuffix(suffix: String): TexturedBlock = TexturedBlock(path + suffix)
    }

    private data class ElementBlock(
        val block: TexturedBlock,
        val elements: List<Element>,
        val facing: PropertyDispatch<VariantMutator>?,
    )

    private companion object {

        val ow02Facing: PropertyDispatch<VariantMutator> =
            PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING)
                .select(Direction.NORTH, NOP)
                .select(Direction.SOUTH, NOP)
                .select(Direction.EAST, Y_ROT_90)
                .select(Direction.WEST, Y_ROT_90)

        val a2OpenworkFacing: PropertyDispatch<VariantMutator> =
            PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING)
                .select(Direction.NORTH, Y_ROT_180)
                .select(Direction.SOUTH, NOP)
                .select(Direction.EAST, Y_ROT_270)
                .select(Direction.WEST, Y_ROT_90)

        val supportBarFacing: PropertyDispatch<VariantMutator> =
            PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING)
                .select(Direction.NORTH, Y_ROT_90)
                .select(Direction.SOUTH, Y_ROT_270)
                .select(Direction.EAST, Y_ROT_180)
                .select(Direction.WEST, NOP)

        val cubes: List<TexturedBlock> = listOf(
            TexturedBlock("walls/ow_01"),
            TexturedBlock("bricks/bricks_brown_white"),
            TexturedBlock("bricks/bricks_brown_black"),
            TexturedBlock("bricks/bricks_grey_black"),
            TexturedBlock("bricks/bricks_white_black"),
            TexturedBlock("bricks/ow_02_cube"),
        )

        val glassBlocks: List<TexturedBlock> = listOf(TexturedBlock("glass/tg_blue"))

        val shapedBases: List<TexturedBlock> = listOf(
            TexturedBlock("bricks/bricks_brown_white"),
            TexturedBlock("bricks/bricks_brown_black"),
            TexturedBlock("bricks/bricks_grey_black"),
            TexturedBlock("bricks/bricks_white_black"),
            TexturedBlock("bricks/ow_02_cube"),
        )

        val elementBlocks: List<ElementBlock> = listOf(
            ElementBlock(TexturedBlock("bricks/a2_openwork"), ElementModels.a2Openwork, a2OpenworkFacing),
            ElementBlock(TexturedBlock("corners/co_01"), ElementModels.co01, null),
            ElementBlock(TexturedBlock("misc/a2_wall_lamp"), ElementModels.a2WallLamp, ROTATION_HORIZONTAL_FACING),
            ElementBlock(TexturedBlock("misc/lamp_post"), ElementModels.lampPost, null),
            ElementBlock(TexturedBlock("misc/support_bar"), ElementModels.supportBar, supportBarFacing),
            ElementBlock(TexturedBlock("walls/ow_02"), ElementModels.ow02, ow02Facing),
        )
    }
}
