package net.Indyuce.inventory.util;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.Indyuce.inventory.MMOInventory;

public class ConfigFile {
	private final File file;
	private final FileConfiguration config;

	public ConfigFile(Player player) {
		this(player.getUniqueId());
	}

	public ConfigFile(UUID uuid) {
		this(MMOInventory.plugin, "/userdata", uuid.toString());
	}

	public ConfigFile(String name) {
		this(MMOInventory.plugin, "", name);
	}

	public ConfigFile(String folder, String name) {
		this(MMOInventory.plugin, folder, name);
	}

	public ConfigFile(Plugin plugin, String folder, String name) {
		config = YamlConfiguration.loadConfiguration(file = new File(plugin.getDataFolder() + folder, name + ".yml"));
	}

	public FileConfiguration getConfig() {
		return config;
	}

	public void save() {
		try {
			config.save(file);
		} catch (IOException e2) {
			MMOInventory.plugin.getLogger().log(Level.SEVERE, "Could not save " + file.getName() + ".yml!");
		}
	}
}