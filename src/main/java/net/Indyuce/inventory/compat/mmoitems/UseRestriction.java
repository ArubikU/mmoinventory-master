package net.Indyuce.inventory.compat.mmoitems;

import net.Indyuce.inventory.inventory.InventoryHandler;
import net.Indyuce.inventory.slot.CustomSlot;
import net.Indyuce.inventory.slot.restriction.SlotRestriction;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.bukkit.inventory.ItemStack;

/**
 * Checks if the player has the required level/class/etc. to use the item.
 */
public class UseRestriction extends SlotRestriction {

	@Override
	public boolean isVerified(InventoryHandler provider, CustomSlot slot, ItemStack item) {
		return PlayerData.get(provider.getPlayer()).getRPG().canUse(io.lumine.mythic.lib.api.item.NBTItem.get(item), false);
	}
}
