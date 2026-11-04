package io.github.minelzu.minelzu_mod

import io.github.minelzu.minelzu_mod.registry.ModBlocks
import io.github.minelzu.minelzu_mod.registry.ModCreativeTabs
import io.github.minelzu.minelzu_mod.registry.ModItems
import net.minecraft.resources.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object MineLZU {

    const val MODID: String = "minelzu"
    const val MODNAME: String = "LZU Minecraft"
    const val CLASSPATH: String = "io.github.minelzu.minelzu_mod"

    @JvmField
    val LOGGER: Logger = LoggerFactory.getLogger("MineLzu")

    fun id(path: String): Identifier = Identifier.fromNamespaceAndPath(MODID, path)

    fun init() {
        LOGGER.info("[$MODNAME] >>> Registering blocks!")
        ModBlocks.REGISTER.register()
        ModItems.REGISTER.register()
        ModCreativeTabs.REGISTER.register()
        LOGGER.info("[$MODNAME] <<< {} blocks registered!", ModBlocks.all.size)
    }
}
