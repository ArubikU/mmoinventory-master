package net.Indyuce.inventory.command;

import net.Indyuce.inventory.MMOInventory;
import net.Indyuce.inventory.gui.PlayerInventoryView;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;

import io.lumine.mythic.lib.api.player.MMOPlayerData;

public class MMOInventoryCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// Open own inventory
		if (args.length < 1 && sender instanceof Player) {
			if (!sender.hasPermission("mmoinventory.open")) {
				sender.sendMessage(MMOInventory.plugin.getTranslation("not-enough-perms"));
				return true;
			}

			new PlayerInventoryView((Player) sender).open();
			return true;
		}

		if (args.length == 0)
			return true;

		// Reload command
		if (args[0].equalsIgnoreCase("reload")) {
			if (!sender.hasPermission("mmoinventory.admin")) {
				sender.sendMessage(MMOInventory.plugin.getTranslation("not-enough-perms"));
				return true;
			}

			MMOInventory.plugin.reload();
			sender.sendMessage(MMOInventory.plugin.getTranslation("reload").replace("{version}",
					MMOInventory.plugin.getDescription().getVersion()));
			return true;
		}

		// Open inventory of someone else
		if (!sender.hasPermission("mmoinventory.open.other")) {
			sender.sendMessage(MMOInventory.plugin.getTranslation("not-enough-perms"));
			return true;
		}

		Player target = Bukkit.getPlayer(args[0]);

		if (target == null) {
			sender.sendMessage(MMOInventory.plugin.getTranslation("wrong-player").replace("{arg}", args[0]));
			return true;
		}

		new PlayerInventoryView((Player) sender, target, false).open();
		return true;
	}

}
