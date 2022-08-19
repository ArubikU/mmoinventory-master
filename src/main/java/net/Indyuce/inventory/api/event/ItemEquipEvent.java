package net.Indyuce.inventory.api.event;

import net.Indyuce.inventory.slot.CustomSlot;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public class ItemEquipEvent extends PlayerEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();

	@Nullable
	private final ItemStack item;
	private final CustomSlot slot;
	private final EquipAction action;

	private boolean cancelled = false;

	/**
	 * Event called whenever a player equips an item in a custom or
	 * vanilla slot when he has the custom inventory opened
	 *
	 * @param player Playing equipping the item
	 * @param item   Item being equipped in a custom slot, or null if the player is
	 *               unequipping it
	 * @param slot   The slot the item is equipped in
	 * @param action Action when equipping/unequipping an item
	 */
	public ItemEquipEvent(Player player, @Nullable ItemStack item, CustomSlot slot, EquipAction action) {
		super(player);

		this.item = item;
		this.action = action;
		this.slot = slot;
	}

	@Nullable
	public ItemStack getItem() {
		return item;
	}

	public boolean hasSlot() {
		return slot != null;
	}

	/**
	 * Null when action is {@link EquipAction#SHIFT_CLICK_UNEQUIP}
	 *
	 * @return The clicked slot, if there is one
	 */
	@Nullable
	public CustomSlot getSlot() {
		return slot;
	}

	public EquipAction getAction() {
		return action;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean bool) {
		cancelled = bool;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public enum EquipAction {

		/**
		 * When a player replaces a slot item with a real item.
		 */
		EQUIP,

		/**
		 * When a player unequips an item
		 */
		UNEQUIP,

		/**
		 * When a player swaps an item to equip with an already equipped item
		 */
		SWAP_ITEMS,

		/**
		 * Equipping an item using shift click
		 */
		SHIFT_CLICK_EQUIP,

		/**
		 * Unequipping an item using shift click
		 */
		SHIFT_CLICK_UNEQUIP;
	}
}
