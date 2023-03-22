package org.rsmod.plugins.playground.login

import org.rsmod.plugins.api.account_info_update
import org.rsmod.plugins.api.cache.type.varbit.VarbitTypeList
import org.rsmod.plugins.api.chatbox_unlocked
import org.rsmod.plugins.api.clientScript
import org.rsmod.plugins.api.component
import org.rsmod.plugins.api.cs2
import org.rsmod.plugins.api.message
import org.rsmod.plugins.api.onLogIn
import org.rsmod.plugins.api.playermember
import org.rsmod.plugins.api.pvp_icons_layout
import org.rsmod.plugins.api.quest_tab_free_quest
import org.rsmod.plugins.api.quest_tab_members_quest
import org.rsmod.plugins.api.script_876
import org.rsmod.plugins.api.scrollbar_resize
import org.rsmod.plugins.api.setVarbit
import org.rsmod.plugins.api.settings_interface_scaling
import org.rsmod.plugins.api.varbit

private val varbits: VarbitTypeList by inject()

onLogIn {
    setVarbit(true, varbits[varbit.chatbox_unlocked])
    clientScript(cs2.account_info_update, arg1 = true, arg2 = false, arg3 = false)
    clientScript(cs2.scrollbar_resize, component.quest_tab_free_quest, component.quest_tab_members_quest, 0)
    clientScript(cs2.settings_interface_scaling, 0)
    clientScript(cs2.pvp_icons_layout, true)
    clientScript(cs2.playermember, true)
    // TODO: args(world_tick, xptracker_login_arg1, xptracker_login_arg2, xptracker_login_arg3)
    clientScript(cs2.script_876, 0, 0, displayName, "REGULAR")
    message("Welcome to RS Mod.")
}
