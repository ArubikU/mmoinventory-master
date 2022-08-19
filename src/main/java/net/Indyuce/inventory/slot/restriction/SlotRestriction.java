package net.Indyuce.inventory.slot.restriction;

import net.Indyuce.inventory.inventory.InventoryHandler;
import net.Indyuce.inventory.slot.CustomSlot;
import org.bukkit.inventory.ItemStack;

public abstract class SlotRestriction {

	/**
	 * Called when the player tries to equip an item in a specific slot.
	 *
	 * @param provider Information about the player trying to equip an item
	 * @param slot     The slot the item is being equipped in
	 * @param item     The item being equipped
	 * @return If the item can be equipped in that custom slot
	 */
	public abstract boolean isVerified(InventoryHandler provider, CustomSlot slot, ItemStack item);
}
