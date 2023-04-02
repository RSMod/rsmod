package org.rsmod.plugins.content.gameframe.build

import org.rsmod.plugins.api.enum
import org.rsmod.plugins.api.gameframe_fixed
import org.rsmod.plugins.api.interf
import org.rsmod.plugins.api.model.ui.StandardGameframe
import org.rsmod.plugins.cache.config.enums.EnumTypeList
import org.rsmod.plugins.content.gameframe.gameframe_fixed_component_map
import org.rsmod.plugins.content.gameframe.util.GameframeUtil
import org.rsmod.plugins.types.NamedComponent
import org.rsmod.plugins.types.NamedInterface
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
public class GameframeFixed @Inject constructor(enums: EnumTypeList) : StandardGameframe {

    override val topLevel: NamedInterface = interf.gameframe_fixed

    override val overlays: Iterable<NamedComponent> = GameframeUtil.build(
        reference = GameframeResizeNormal,
        componentEnum = enums[enum.gameframe_fixed_component_map]
    )
}
