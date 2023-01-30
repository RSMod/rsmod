package org.rsmod.plugins.api.cache.type.obj

import org.rsmod.plugins.api.cache.type.ConfigType

data class ObjectType(
    override val id: Int,
    val name: String,
    val width: Int,
    val height: Int,
    val blockPath: Boolean,
    val blockProjectile: Boolean,
    val interactType: Int,
    val obstruct: Boolean,
    val clipType: Int,
    val clipMask: Int,
    val varp: Int,
    val varbit: Int,
    val animation: Int,
    val category: Int,
    val rotated: Boolean,
    val options: List<String?>,
    val transforms: List<Int>,
    val defaultTransform: Int,
    val models: List<Int>,
    val modelTypes: List<Int>,
    val contouredGround: Int,
    val nonFlatShading: Boolean,
    val clippedModel: Boolean,
    val decorDisplacement: Int,
    val ambient: Int,
    val contrast: Int,
    val recolorSrc: List<Int>,
    val recolorDest: List<Int>,
    val retextureSrc: List<Int>,
    val retextureDest: List<Int>,
    val clipped: Boolean,
    val resizeX: Int,
    val resizeHeight: Int,
    val resizeY: Int,
    val mapSceneId: Int,
    val offsetX: Int,
    val offsetHeight: Int,
    val offsetY: Int,
    val hollow: Boolean,
    val supportItems: Int,
    val ambientSoundId: Int,
    val ambientSoundRadius: Int,
    val anInt3426: Int,
    val anInt3427: Int,
    val aBoolean3429: Boolean,
    val anIntArray3428: List<Int>,
    val mapIconId: Int,
    val intParameters: Map<Int, Int>,
    val strParameters: Map<Int, String>
) : ConfigType
