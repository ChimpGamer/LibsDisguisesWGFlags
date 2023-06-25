package nl.chimpgamer.libsdisguiseswgflags

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.bukkit.WorldGuardPlugin
import com.sk89q.worldguard.protection.flags.StateFlag
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException
import com.sk89q.worldguard.session.SessionManager
import nl.chimpgamer.libsdisguiseswgflags.listeners.LibsDisguiseListener
import nl.chimpgamer.libsdisguiseswgflags.handlers.BlockDisguisesHandler
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level

class LibsDisguisesWGFlagsPlugin : JavaPlugin() {
    val blockDisguisesFlag = StateFlag("block-disguises", true) // Default is allowed

    private val blockDisguisesHandler = BlockDisguisesHandler.createFactory()

    private val sessionManager: SessionManager get() = WorldGuard.getInstance().platform.sessionManager

    override fun onLoad() {
        instance = this
        registerFlag(blockDisguisesFlag)
    }

    override fun onEnable() {
        sessionManager.registerHandler(blockDisguisesHandler, null)
        server.pluginManager.registerEvents(LibsDisguiseListener(this), this)
    }

    override fun onDisable() {
        sessionManager.unregisterHandler(blockDisguisesHandler)
    }

    fun testFlag(player: Player, location: Location, stateFlag: StateFlag): Boolean {
        val localPlayer = WorldGuardPlugin.inst().wrapPlayer(player)
        val regionContainer = WorldGuard.getInstance().platform.regionContainer
        val regionQuery = regionContainer.createQuery()
        return sessionManager.hasBypass(localPlayer, localPlayer.world) || regionQuery.testState(
            BukkitAdapter.adapt(
                location
            ), localPlayer, stateFlag
        )
    }

    fun registerFlag(stateFlag: StateFlag) {
        val flagRegistry = WorldGuard.getInstance().flagRegistry
        try {
            flagRegistry.register(stateFlag)
        } catch (ex: FlagConflictException) {
            logger.log(Level.SEVERE, "Unable to register flag ${stateFlag.name}", ex)
        }
    }

    companion object {
        lateinit var instance: LibsDisguisesWGFlagsPlugin
    }
}