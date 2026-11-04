package io.github.minelzu.minelzu_mod.datagen.model

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.minecraft.resources.Identifier

internal enum class Face(val key: String) {
    NORTH("north"),
    EAST("east"),
    SOUTH("south"),
    WEST("west"),
    UP("up"),
    DOWN("down"),
}

internal data class ElementRotation(
    val origin: List<Double>,
    val axis: String,
    val angle: Double,
) {

    fun toJson(): JsonObject = JsonObject().apply {
        add("origin", origin.toJsonArray())
        addProperty("axis", axis)
        addProperty("angle", angle)
    }
}

internal data class ElementFace(val uv: List<Double>) {

    fun toJson(): JsonObject = JsonObject().apply {
        add("uv", uv.toJsonArray())
        addProperty("texture", TEXTURE_KEY)
    }

    private companion object {
        const val TEXTURE_KEY = "#0"
    }
}

internal data class Element(
    val from: List<Double>,
    val to: List<Double>,
    val faces: Map<Face, ElementFace>,
    val rotation: ElementRotation? = null,
) {

    fun toJson(): JsonObject = JsonObject().apply {
        add("from", from.toJsonArray())
        add("to", to.toJsonArray())
        rotation?.let { add("rotation", it.toJson()) }
        add("faces", JsonObject().apply { faces.forEach { (face, value) -> add(face.key, value.toJson()) } })
    }
}

internal class FaceCollector {

    private val faces: MutableMap<Face, ElementFace> = LinkedHashMap()

    fun north(u1: Double, v1: Double, u2: Double, v2: Double) = put(Face.NORTH, u1, v1, u2, v2)

    fun east(u1: Double, v1: Double, u2: Double, v2: Double) = put(Face.EAST, u1, v1, u2, v2)

    fun south(u1: Double, v1: Double, u2: Double, v2: Double) = put(Face.SOUTH, u1, v1, u2, v2)

    fun west(u1: Double, v1: Double, u2: Double, v2: Double) = put(Face.WEST, u1, v1, u2, v2)

    fun up(u1: Double, v1: Double, u2: Double, v2: Double) = put(Face.UP, u1, v1, u2, v2)

    fun down(u1: Double, v1: Double, u2: Double, v2: Double) = put(Face.DOWN, u1, v1, u2, v2)

    fun build(): Map<Face, ElementFace> = faces.toMap()

    private fun put(face: Face, u1: Double, v1: Double, u2: Double, v2: Double) {
        faces[face] = ElementFace(v(u1, v1, u2, v2))
    }
}

internal fun v(vararg values: Double): List<Double> = values.toList()

internal fun rotation(originX: Double, originY: Double, originZ: Double, axis: String, angle: Double): ElementRotation =
    ElementRotation(v(originX, originY, originZ), axis, angle)

internal fun element(
    from: List<Double>,
    to: List<Double>,
    rotation: ElementRotation? = null,
    content: FaceCollector.() -> Unit,
): Element {
    val collector = FaceCollector()
    collector.content()
    return Element(from, to, collector.build(), rotation)
}

internal fun elementModel(texture: Identifier, elements: List<Element>): JsonObject = JsonObject().apply {
    addProperty("parent", "minecraft:block/block")
    add("textures", JsonObject().apply {
        addProperty("0", texture.toString())
        addProperty("particle", texture.toString())
    })
    add("elements", JsonArray().apply { elements.forEach { add(it.toJson()) } })
}

private fun List<Double>.toJsonArray(): JsonArray = JsonArray().apply { this@toJsonArray.forEach { add(it) } }
