package io.github.minelzu.minelzu_mod.registry

import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import io.github.minelzu.minelzu_mod.MineLZU
import io.github.minelzu.minelzu_mod.block.HorizontalStairsBlock
import io.github.minelzu.minelzu_mod.block.HorizontalWallBlock
import io.github.minelzu.minelzu_mod.block.NoCollisionFullOutlineBlock
import io.github.minelzu.minelzu_mod.block.SimpleBuildingBlock
import io.github.minelzu.minelzu_mod.block.TranslucentGlassBlock
import io.github.minelzu.minelzu_mod.block.VoxeledBuildingBlock
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SlabBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.phys.shapes.Shapes

object ModBlocks {

    val REGISTER: DeferredRegister<Block> = DeferredRegister.create(MineLZU.MODID, Registries.BLOCK)

    private val registered: MutableMap<String, RegistrySupplier<Block>> = LinkedHashMap()

    private val simpleCubes: List<String> = listOf(
        "ow_01",
        "co_01",
        "bricks_brown_white",
        "bricks_brown_black",
        "bricks_grey_black",
        "bricks_white_black",
        "ow_02_cube",
    )

    private val shapedBases: List<String> = listOf(
        "bricks_brown_white",
        "bricks_brown_black",
        "bricks_grey_black",
        "bricks_white_black",
        "ow_02_cube",
    )

    init {
        simpleCubes.forEach { name -> block(name) { properties -> SimpleBuildingBlock(properties) } }
        shapedBases.forEach { name ->
            block("${name}_stairs") { properties ->
                HorizontalStairsBlock(registered.getValue(name).get().defaultBlockState(), properties)
            }
            block("${name}_slab") { properties -> SlabBlock(properties) }
        }
        glassBlock("tg_blue") { properties -> TranslucentGlassBlock(properties) }
        block("a2_wall_lamp") { properties -> NoCollisionFullOutlineBlock(properties) }
        block("lamp_post") { properties ->
            VoxeledBuildingBlock(properties, Shapes.box(6.0, 0.0, 6.0, 10.0, 16.0, 10.0))
        }
        block("ow_02") { properties ->
            HorizontalWallBlock(properties, doubleArrayOf(0.0, 0.0, 3.0, 16.0, 16.0, 13.0))
        }
        block("a2_openwork") { properties ->
            HorizontalWallBlock(properties, doubleArrayOf(0.0, 0.0, 0.0, 16.0, 16.0, 16.0))
        }
        block("support_bar") { properties ->
            HorizontalWallBlock(properties, doubleArrayOf(7.5, 0.0, 0.0, 8.5, 16.0, 16.0))
        }
    }

    val all: Map<String, RegistrySupplier<Block>> get() = registered

    val ow01: RegistrySupplier<Block> get() = registered.getValue("ow_01")

    val ow02: RegistrySupplier<Block> get() = registered.getValue("ow_02")

    val bricksBrownWhite: RegistrySupplier<Block> get() = registered.getValue("bricks_brown_white")

    private fun block(name: String, factory: (BlockBehaviour.Properties) -> Block): RegistrySupplier<Block> =
        register(name) { factory(stone(name)) }

    private fun glassBlock(name: String, factory: (BlockBehaviour.Properties) -> Block): RegistrySupplier<Block> =
        register(name) { factory(glass(name)) }

    private fun register(name: String, factory: () -> Block): RegistrySupplier<Block> =
        REGISTER.register(name) { factory() }.also { registered[name] = it }

    private fun stone(name: String): BlockBehaviour.Properties = stoneOf(base(name))

    private fun glass(name: String): BlockBehaviour.Properties = glassOf(base(name))

    private fun base(name: String): BlockBehaviour.Properties =
        BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, MineLZU.id(name)))

    private fun stoneOf(properties: BlockBehaviour.Properties): BlockBehaviour.Properties =
        properties
            .mapColor(MapColor.STONE)
            .sound(SoundType.STONE)
            .requiresCorrectToolForDrops()
            .strength(-1.0f, 100.0f)

    private fun glassOf(properties: BlockBehaviour.Properties): BlockBehaviour.Properties =
        properties
            .mapColor(MapColor.COLOR_LIGHT_BLUE)
            .sound(SoundType.GLASS)
            .requiresCorrectToolForDrops()
            .strength(-1.0f, 100.0f)
}

