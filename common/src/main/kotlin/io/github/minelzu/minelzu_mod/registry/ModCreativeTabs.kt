package io.github.minelzu.minelzu_mod.registry

import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import io.github.minelzu.minelzu_mod.MineLZU
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack

object ModCreativeTabs {

    val REGISTER: DeferredRegister<CreativeModeTab> =
        DeferredRegister.create(MineLZU.MODID, Registries.CREATIVE_MODE_TAB)

    val main: RegistrySupplier<CreativeModeTab> = REGISTER.register("main") {
        CreativeModeTab.Builder(CreativeModeTab.Row.TOP, 0)
            .title(Component.translatable("itemGroup.minelzu.main"))
            .icon { ItemStack(ModBlocks.bricksBrownWhite.get()) }
            .displayItems { _, output -> ModItems.all.values.forEach { item -> output.accept(item.get()) } }
            .build()
    }
}
