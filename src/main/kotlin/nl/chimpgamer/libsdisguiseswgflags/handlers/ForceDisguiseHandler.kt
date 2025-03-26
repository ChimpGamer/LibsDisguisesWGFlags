package nl.chimpgamer.libsdisguiseswgflags.handlers

import com.sk89q.worldedit.util.Location
import com.sk89q.worldedit.world.World
import com.sk89q.worldguard.LocalPlayer
import com.sk89q.worldguard.bukkit.BukkitPlayer
import com.sk89q.worldguard.protection.ApplicableRegionSet
import com.sk89q.worldguard.session.MoveType
import com.sk89q.worldguard.session.Session
import com.sk89q.worldguard.session.handler.FlagValueChangeHandler
import com.sk89q.worldguard.session.handler.Handler
import me.libraryaddict.disguise.DisguiseAPI
import me.libraryaddict.disguise.disguisetypes.Disguise
import me.libraryaddict.disguise.utilities.parser.DisguiseParser
import nl.chimpgamer.libsdisguiseswgflags.LibsDisguisesWGFlagsPlugin

class ForceDisguiseHandler(session: Session?) :
    FlagValueChangeHandler<String?>(session, LibsDisguisesWGFlagsPlugin.instance.forceDisguiseFlag) {

    companion object {
        fun createFactory(): Handler.Factory<*> = Factory()
    }

    class Factory : Handler.Factory<ForceDisguiseHandler>() {
        override fun create(session: Session?): ForceDisguiseHandler {
            return ForceDisguiseHandler(session)
        }
    }

    /**
    * Remember the original disguise the player was using prior
    */
    private var originalDisguise: Disguise? = null

    override fun onInitialValue(player: LocalPlayer, set: ApplicableRegionSet?, value: String?) {
        handleValue(player, player.world, value)
    }

    override fun onSetValue(
        player: LocalPlayer,
        from: Location?,
        to: Location,
        toSet: ApplicableRegionSet?,
        currentValue: String?,
        lastValue: String?,
        moveType: MoveType?
    ): Boolean {
        handleValue(player, to.extent as World, currentValue, lastValue)
        return true
    }

    override fun onAbsentValue(
        player: LocalPlayer,
        from: Location?,
        to: Location,
        toSet: ApplicableRegionSet?,
        lastValue: String?,
        moveType: MoveType?
    ): Boolean {
        handleValue(player, to.extent as World, null, lastValue)
        return true
    }

    private fun handleValue(localPlayer: LocalPlayer, world: World, value: String?, lastValue: String? = null) {
        val bukkitPlayer = (localPlayer as BukkitPlayer).player

        if (this.session.manager.hasBypass(localPlayer, world)) {
            if (originalDisguise != null) {
                DisguiseAPI.disguiseToAll(bukkitPlayer, originalDisguise)
            } else {
                DisguiseAPI.undisguiseToAll(bukkitPlayer)
            }
            return
        }

        if (value != null) {
            val playerDisguise = DisguiseAPI.getDisguise(bukkitPlayer)
            if (lastValue == null) {
                originalDisguise = playerDisguise
            }
            val newDisguise = runCatching { DisguiseParser.parseDisguise(value) }.getOrNull()
            if (playerDisguise == newDisguise) return
            if (newDisguise != null) {
                DisguiseAPI.disguiseToAll(bukkitPlayer, newDisguise)
            }
        } else {
            if (originalDisguise != null) {
                DisguiseAPI.disguiseToAll(bukkitPlayer, originalDisguise)
            } else {
                DisguiseAPI.undisguiseToAll(bukkitPlayer)
            }
        }
    }
}