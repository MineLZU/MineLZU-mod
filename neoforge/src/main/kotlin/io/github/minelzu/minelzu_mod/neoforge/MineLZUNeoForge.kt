package io.github.minelzu.minelzu_mod.neoforge

import io.github.minelzu.minelzu_mod.MineLZU
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.data.event.GatherDataEvent

@Mod(MineLZU.MODID)
class MineLZUNeoForge(modEventBus: IEventBus, modContainer: ModContainer) {

    init {
        MineLZU.init()
        modEventBus.addListener(GatherDataEvent.Client::class.java) { event -> ModDatagen.gather(event) }
    }
}
