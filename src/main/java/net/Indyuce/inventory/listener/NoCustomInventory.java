package net.Indyuce.inventory.listener;

import net.Indyuce.inventory.MMOInventory;
import net.Indyuce.inventory.api.event.ItemEquipEvent;
import net.Indyuce.inventory.slot.CustomSlot;
import net.Indyuce.inventory.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class NoCustomInventory implements Listener {

	@EventHandler
	public void giveItemsOnJoin(PlayerJoinEvent event) {

		Player player = event.getPlayer();
		for (CustomSlot slot : MMOInventory.plugin.getSlotManager().getCustomSlots()) {
			ItemStack current = player.getInventory().getItem(slot.getIndex());
			if (!Utils.isAir(current) && slot.checkSlotRestrictions(MMOInventory.plugin.getDataManager().getInventory(player), current))
				continue;

			player.getInventory().setItem(slot.getIndex(), slot.displayItem(player));

			/*
			 * Drops the item that was previously in that slot only if it was
			 * not a special MMOInv gui item. This happens when players log in
			 * FOR THE FIRST TIME after the server owner has toggled on the
			 * no-custom-inv option.
			 *
			 * This issue does not happen on future logins yet can be game breaking.
			 */
			if (!Utils.isAir(current) && !Utils.isGuiItem(current))
				for (ItemStack drop : player.getInventory().addItem(current).values())
					player.getWorld().dropItem(player.getLocation(), drop);
		}
	}

	private static final List<InventoryAction> supported = Arrays.asList(InventoryAction.PICKUP_ALL, InventoryAction.PLACE_ALL, InventoryAction.SWAP_WITH_CURSOR);

	/**
	 * Note: this code is very similar to the same code when a player
	 * tries to equip an item in the custom RPG inventory.
	 * <p>
	 * See {@link net.Indyuce.inventory.gui.PlayerInventoryView#whenClicked(InventoryClickEvent)}
	 * for more information. It is likely that a change in one of these two
	 * would require a change in both actually.
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void placeBackSlotItems(InventoryClickEvent event) {

		// This only works in the player's inventory
		Player player = (Player) event.getWhoClicked();
		if (!player.getInventory().equals(event.getClickedInventory()))
			return;

		// Find custom slot
		CustomSlot slot = MMOInventory.plugin.getSlotManager().get(event.getSlot());
		if (slot == null)
			return;

		if (!supported.contains(event.getAction())) {
			event.setCancelled(true);
			return;
		}

		// Player tries to pickup a slot item, without equipping any
		ItemStack item = event.getCurrentItem();
		if (Utils.isAir(event.getCursor()) && Utils.isGuiItem(item)) {
			event.setCancelled(true);
			return;
		}

		// Check for BOTH custom/vanilla slot restrictions
		ItemStack cursor = event.getCursor();
		if (!Utils.isAir(event.getCursor())) {

			// Prevents equipping stacked items
			if (MMOInventory.plugin.getConfig().getBoolean("disable-equiping-stacked-items", true) && event.getCursor().getAmount() > 1) {
				event.setCancelled(true);
				return;
			}

			// Check for vanilla AND custom slot restrictions
			if (!slot.canHost(MMOInventory.plugin.getDataManager().getInventory(player), cursor)) {
				event.setCancelled(true);
				return;
			}
		}

		// Call Bukkit event
		ItemEquipEvent.EquipAction action = Utils.isAir(event.getCursor()) ? ItemEquipEvent.EquipAction.UNEQUIP : Utils.isGuiItem(cursor) ? ItemEquipEvent.EquipAction.EQUIP : ItemEquipEvent.EquipAction.SWAP_ITEMS;
		ItemEquipEvent called = new ItemEquipEvent(player, event.getCursor(), slot, action);
		Bukkit.getPluginManager().callEvent(called);
		if (called.isCancelled()) {
			event.setCancelled(true);
			return;
		}

		/*
		 * If the player has picked up an inventory slot item, remove it
		 * instantly after checking the equip event was not canceled (bug
		 * fix)
		 */
		if (Utils.isGuiItem(item))
			event.setCurrentItem(null);

		/*
		 * If the player is taking away an item without swapping it, place
		 * the inventory slot item back in the corresponding slot
		 */
		if (Utils.isAir(event.getCursor()))
			Bukkit.getScheduler().runTask(MMOInventory.plugin, () -> player.getInventory().setItem(slot.getIndex(), slot.displayItem(player)));

		// Finally update the player's inventory
		MMOInventory.plugin.updateInventory(player);
	}

	@EventHandler
	public void removeGuiDrops(PlayerDeathEvent event) {
		if (event.getKeepInventory())
			return;

		Iterator<ItemStack> iterator = event.getDrops().iterator();
		while (iterator.hasNext()) {
			ItemStack next = iterator.next();
			if (Utils.isGuiItem(next))
				iterator.remove();
		}
	}

	@EventHandler
	public void giveItemsOnRespawn(PlayerRespawnEvent event) {
		giveItemsOnJoin(new PlayerJoinEvent(event.getPlayer(), "You found a secret dev easter egg"));
	}
}
