package nl.chimpgamer.libsdisguiseswgflags

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.bukkit.WorldGuardPlugin
import com.sk89q.worldguard.protection.flags.Flag
import com.sk89q.worldguard.protection.flags.StateFlag
import com.sk89q.worldguard.protection.flags.StringFlag
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException
import com.sk89q.worldguard.session.SessionManager
import nl.chimpgamer.libsdisguiseswgflags.listeners.LibsDisguiseListener
import nl.chimpgamer.libsdisguiseswgflags.handlers.BlockDisguisesHandler
import nl.chimpgamer.libsdisguiseswgflags.handlers.ForceDisguiseHandler
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level

class LibsDisguisesWGFlagsPlugin : JavaPlugin() {
    val blockDisguisesFlag = StateFlag("block-disguises", true) // Default is allowed
    val forceDisguiseFlag = StringFlag("force-disguise")

    private val blockDisguisesHandler = BlockDisguisesHandler.createFactory()
    private val forceDisguiseHandler = ForceDisguiseHandler.createFactory()

    private val sessionManager: SessionManager get() = WorldGuard.getInstance().platform.sessionManager

    override fun onLoad() {
        instance = this
        registerFlag(blockDisguisesFlag)
        registerFlag(forceDisguiseFlag)
    }

    override fun onEnable() {
        sessionManager.run {
            registerHandler(blockDisguisesHandler, null)
            registerHandler(forceDisguiseHandler, null)
        }
        server.pluginManager.registerEvents(LibsDisguiseListener(this), this)
    }

    override fun onDisable() {
        sessionManager.run {
            unregisterHandler(blockDisguisesHandler)
            unregisterHandler(forceDisguiseHandler)
        }
    }

    fun testFlag(player: Player, location: Location, stateFlag: StateFlag): Boolean {
        val localPlayer = WorldGuardPlugin.inst().wrapPlayer(player)
        val regionContainer = WorldGuard.getInstance().platform.regionContainer
        val regionQuery = regionContainer.createQuery()
        return sessionManager.hasBypass(localPlayer, localPlayer.world) ||
                regionQuery.testState(BukkitAdapter.adapt(location), localPlayer, stateFlag)
    }

    fun registerFlag(flag: Flag<*>) {
        val flagRegistry = WorldGuard.getInstance().flagRegistry
        try {
            flagRegistry.register(flag)
        } catch (ex: FlagConflictException) {
            logger.log(Level.SEVERE, "Unable to register flag ${flag.name}", ex)
        }
    }

    companion object {
        lateinit var instance: LibsDisguisesWGFlagsPlugin
    }
}