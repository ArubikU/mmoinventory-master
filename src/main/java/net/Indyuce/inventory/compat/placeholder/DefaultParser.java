package net.Indyuce.inventory.compat.placeholder;

import org.bukkit.OfflinePlayer;

public class DefaultParser implements PlaceholderParser {
	@Override
	public String parse(OfflinePlayer player, String string) {
		return string;
	}
}
