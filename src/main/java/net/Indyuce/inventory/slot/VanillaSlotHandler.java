package net.Indyuce.inventory.slot;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface VanillaSlotHandler {

	/**
	 * Called when a player equips an item in a vanilla slot
	 *
	 * @param player Player equipping the item
	 * @param item   Item being equipped
	 */
	public void equip(Player player, ItemStack item);

	/**
	 * Called first before equipping the item
	 *
	 * @param item
	 *            Item being equipped
	 * @return If an item can be equipped in a vanilla slot
	 */
	public boolean canEquip(ItemStack item);

	/**
	 * Used when displayed the equipped item when the player opens up the GUI
	 * for the first time.
	 *
	 * @param player The player to retrieve the item from
	 * @return The item currently equipped in that slot
	 */
	public ItemStack retrieveItem(Player player);

	/**
	 * The main issue is adding support for shift clicks in the custom inventory.
	 * In what slot do we put the diamond chestplate? In the chestplate slot, simple
	 * enough... Although it's also possible to place it in the off hand slot...
	 * <p>
	 * In fact, it's better to entirely disable shift click for the off hand.
	 *
	 * @return If the corresponding slot supports shift clicks.
	 */
	public boolean supportsShiftClick();
}
