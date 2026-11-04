package io.github.minelzu.minelzu_mod.fabric

import io.github.minelzu.minelzu_mod.MineLZU
import net.fabricmc.api.ModInitializer

class MineLZUFabric : ModInitializer {

    override fun onInitialize() = MineLZU.init()
}
