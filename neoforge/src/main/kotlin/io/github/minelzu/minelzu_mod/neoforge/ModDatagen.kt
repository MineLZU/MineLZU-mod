package io.github.minelzu.minelzu_mod.neoforge

import io.github.minelzu.minelzu_mod.datagen.ModBlockTagsProvider
import io.github.minelzu.minelzu_mod.datagen.ModModelProvider
import net.neoforged.neoforge.data.event.GatherDataEvent
import net.neoforged.neoforge.data.event.PackGenerator

internal object ModDatagen {

    fun gather(event: GatherDataEvent.Client) {
        event.createProvider(PackGenerator.DataProviderFromOutput { output -> ModModelProvider(output) })
        event.createProvider(
            PackGenerator.DataProviderFromOutputLookup { output, lookupProvider ->
                ModBlockTagsProvider(output, lookupProvider)
            },
        )
    }
}
