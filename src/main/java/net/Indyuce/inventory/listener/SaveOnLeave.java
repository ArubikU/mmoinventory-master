package net.Indyuce.inventory.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import net.Indyuce.inventory.MMOInventory;

public class SaveOnLeave implements Listener {
	@EventHandler
	public void saveOnLeave(PlayerQuitEvent event) {
		MMOInventory.plugin.getDataManager().save(event.getPlayer());
		MMOInventory.plugin.getDataManager().unloadData(event.getPlayer());
	}
}
