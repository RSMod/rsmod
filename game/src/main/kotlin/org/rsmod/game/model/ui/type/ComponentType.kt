package org.rsmod.game.model.ui.type

import org.rsmod.game.cache.type.CacheType

data class ComponentType(
    override val id: Int,
    val scripts: Boolean,
    val type: Int,
    val menuType: Int,
    val contentType: Int,
    val originalX: Int,
    val originalY: Int,
    val originalWidth: Int,
    val originalHeight: Int,
    val opacity: Int,
    val parentId: Int,
    val hoverSibling: Int,
    val operationType: List<Int>,
    val operandRhs: List<Int>,
    val instructionCountCs1: List<IntArray>,
    val scrollHeight: Int,
    val scrollWidth: Int,
    val hidden: Boolean,
    val itemId: List<Int>,
    val itemAmount: List<Int>,
    val clickMask: Int,
    val pitchX: Int,
    val pitchY: Int,
    val offsetX: List<Int>,
    val offsetY: List<Int>,
    val sprites: List<Int>,
    val configActions: List<String?>,
    val filled: Boolean,
    val textAlignmentX: Int,
    val textAlignmentY: Int,
    val lineHeight: Int,
    val fontId: Int,
    val shadowedText: Boolean,
    val text: String,
    val altText: String,
    val textColor: Int,
    val altTextColor: Int,
    val hoverTextColor: Int,
    val altHoverTextColor: Int,
    val spriteId: Int,
    val spriteId2: Int,
    val altSpriteId: Int,
    val modelId: Int,
    val altModelId: Int,
    val modelType: Int,
    val modelZoom: Int,
    val animationId: Int,
    val altAnimationId: Int,
    val rotationX: Int,
    val rotationY: Int,
    val rotationZ: Int,
    val targetVerb: String,
    val spellName: String,
    val tooltip: String,
    val dynamicX: Int,
    val dynamicY: Int,
    val dynamicWidth: Int,
    val buttonType: Int,
    val disabledClickThrough: Boolean,
    val textureId: Int,
    val spriteTiling: Boolean,
    val borderThickness: Int,
    val verticalFlip: Boolean,
    val horizontalFlip: Boolean,
    val offsetX2d: Int,
    val offsetY2d: Int,
    val orthogonal: Boolean,
    val modelHeight: Int,
    val lineWidth: Int,
    val lineDirection: Boolean,
    val opBase: String,
    val actions: List<String?>,
    val dragRender: Boolean,
    val dragDeadZone: Int,
    val dragDeadTime: Int,
    val loadListener: List<Any>,
    val mouseOverListener: List<Any>,
    val mouseLeaveListener: List<Any>,
    val targetEnterListener: List<Any>,
    val targetLeaveListener: List<Any>,
    val varTransmitListener: List<Any>,
    val invTransmitListener: List<Any>,
    val statTransmitListener: List<Any>,
    val timerListener: List<Any>,
    val opListener: List<Any>,
    val mouseRepeatListener: List<Any>,
    val clickStartListener: List<Any>,
    val clickRepeatListener: List<Any>,
    val clickReleaseListener: List<Any>,
    val holdListener: List<Any>,
    val dragStartListener: List<Any>,
    val dragReleaseListener: List<Any>,
    val scrollWheelListener: List<Any>,
    val varTransmitTrigger: List<Int>,
    val invTransmitTrigger: List<Int>,
    val statTransmitTrigger: List<Int>
) : CacheType