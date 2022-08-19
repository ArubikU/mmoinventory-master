package net.Indyuce.inventory.listener;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;

import net.Indyuce.inventory.MMOInventory;

public class ResourcePack implements Listener {
	private final String url;
	private final int delay;

	public ResourcePack(ConfigurationSection config) {
		url = config.getString("url");
		delay = config.getInt("delay");

		Validate.notNull(url, "Could not load resource pack URL");

		if (config.getBoolean("kick.enabled"))
			Bukkit.getPluginManager().registerEvents(new KickIfDisabled(config.getConfigurationSection("kick")), MMOInventory.plugin);
	}

	@EventHandler
	public void sendResourcePack(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Bukkit.getScheduler().scheduleSyncDelayedTask(MMOInventory.plugin, () -> player.setResourcePack(url), delay);
	}

	public class KickIfDisabled implements Listener {
		private final String message;

		public KickIfDisabled(ConfigurationSection config) {
			Validate.notNull(config.getString("message"), "Could not read kick message");
			message = ChatColor.translateAlternateColorCodes('&', config.getString("message"));
		}

		@EventHandler
		public void kickPlayer(PlayerResourcePackStatusEvent event) {
			if (event.getStatus() == Status.DECLINED || event.getStatus() == Status.FAILED_DOWNLOAD)
				event.getPlayer().kickPlayer(message);
		}
	}
}
