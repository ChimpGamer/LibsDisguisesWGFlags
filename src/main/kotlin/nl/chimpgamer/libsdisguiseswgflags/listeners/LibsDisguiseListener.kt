package nl.chimpgamer.libsdisguiseswgflags.listeners

import me.libraryaddict.disguise.events.DisguiseEvent
import nl.chimpgamer.libsdisguiseswgflags.LibsDisguisesWGFlagsPlugin
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class LibsDisguiseListener(private val plugin: LibsDisguisesWGFlagsPlugin) : Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun DisguiseEvent.onDisguise() {
        if (commandSender == null) return
        if (entity !is Player) return
        val player = entity as Player
        if (!plugin.testFlag(player, player.location, plugin.blockDisguisesFlag)) {
            isCancelled = true
        }
    }
}