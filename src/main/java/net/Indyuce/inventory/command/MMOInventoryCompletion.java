package net.Indyuce.inventory.command;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class MMOInventoryCompletion implements TabCompleter {
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> list = new ArrayList<>();

		if (args.length == 1) {
			if (sender.hasPermission("mmoinventory.admin"))
				list.add("reload");
			if (sender.hasPermission("mmoinventory.open.other"))
				Bukkit.getOnlinePlayers().forEach(online -> list.add(online.getName()));
		}

		return args[args.length - 1].isEmpty() ? list : list.stream().filter(string -> string.toLowerCase().startsWith(args[args.length - 1].toLowerCase())).collect(Collectors.toList());
	}
}
