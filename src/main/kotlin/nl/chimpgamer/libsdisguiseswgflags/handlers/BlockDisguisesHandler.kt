package nl.chimpgamer.libsdisguiseswgflags.handlers

import com.sk89q.worldedit.util.Location
import com.sk89q.worldedit.world.World
import com.sk89q.worldguard.LocalPlayer
import com.sk89q.worldguard.bukkit.BukkitPlayer
import com.sk89q.worldguard.protection.ApplicableRegionSet
import com.sk89q.worldguard.protection.flags.StateFlag.State
import com.sk89q.worldguard.session.MoveType
import com.sk89q.worldguard.session.Session
import com.sk89q.worldguard.session.handler.FlagValueChangeHandler
import com.sk89q.worldguard.session.handler.Handler
import me.libraryaddict.disguise.DisguiseAPI
import me.libraryaddict.disguise.disguisetypes.Disguise
import nl.chimpgamer.libsdisguiseswgflags.LibsDisguisesWGFlagsPlugin

class BlockDisguisesHandler(session: Session?) : FlagValueChangeHandler<State>(session, LibsDisguisesWGFlagsPlugin.instance.blockDisguisesFlag) {

    companion object {
        fun createFactory(): Handler.Factory<*> = Factory()
    }

    class Factory : Handler.Factory<BlockDisguisesHandler>() {
        override fun create(session: Session?): BlockDisguisesHandler {
            return BlockDisguisesHandler(session)
        }
    }

    var originalDisguise: Disguise? = null

    override fun onInitialValue(player: LocalPlayer, set: ApplicableRegionSet?, value: State?) {
        handleValue(player, player.world, value)
    }

    override fun onSetValue(
        player: LocalPlayer,
        from: Location?,
        to: Location,
        toSet: ApplicableRegionSet?,
        currentValue: State?,
        lastValue: State?,
        moveType: MoveType?
    ): Boolean {
        handleValue(player, to.extent as World, currentValue)
        return true
    }

    override fun onAbsentValue(
        player: LocalPlayer,
        from: Location?,
        to: Location,
        toSet: ApplicableRegionSet?,
        lastValue: State?,
        moveType: MoveType?
    ): Boolean {
        handleValue(player, to.extent as World, null)
        return true
    }

    private fun handleValue(localPlayer: LocalPlayer, world: World, state: State?) {
        val bukkitPlayer = (localPlayer as BukkitPlayer).player

        if (!this.session.manager.hasBypass(localPlayer, world) && state != null) {
            val value = state === State.ALLOW
            if (value && !DisguiseAPI.isDisguised(bukkitPlayer)) {
                if (originalDisguise != null) {
                    DisguiseAPI.disguiseEntity(bukkitPlayer, originalDisguise)
                }
            } else if (DisguiseAPI.isDisguised(bukkitPlayer)) {
                originalDisguise = DisguiseAPI.getDisguise(bukkitPlayer)
                DisguiseAPI.undisguiseToAll(bukkitPlayer)
            } else {
                originalDisguise = null
            }
        } else {
            if (originalDisguise != null) {
                DisguiseAPI.disguiseEntity(bukkitPlayer, originalDisguise)
            }
        }
    }
}