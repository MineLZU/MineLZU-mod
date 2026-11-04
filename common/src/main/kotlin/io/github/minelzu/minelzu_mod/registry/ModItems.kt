package io.github.minelzu.minelzu_mod.registry

import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import io.github.minelzu.minelzu_mod.MineLZU
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item

object ModItems {

    val REGISTER: DeferredRegister<Item> = DeferredRegister.create(MineLZU.MODID, Registries.ITEM)

    private val registered: MutableMap<String, RegistrySupplier<Item>> = LinkedHashMap()

    init {
        ModBlocks.all.forEach { (name, block) ->
            registered[name] = REGISTER.register(name) {
                BlockItem(
                    block.get(),
                    Item.Properties()
                        .setId(ResourceKey.create(Registries.ITEM, MineLZU.id(name)))
                        .useBlockDescriptionPrefix(),
                )
            }
        }
    }

    val all: Map<String, RegistrySupplier<Item>> get() = registered
}

