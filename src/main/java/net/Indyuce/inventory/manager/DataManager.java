package net.Indyuce.inventory.manager;

import net.Indyuce.inventory.inventory.CustomInventoryHandler;
import net.Indyuce.inventory.inventory.InventoryHandler;
import org.apache.commons.lang.Validate;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public abstract class DataManager {
	private final Map<UUID, InventoryHandler> inventories = new HashMap<>();

	/**
	 * Function used to generate an inventoryProvider instance given a player.
	 * There are two types of inventoryProviders: Complex, which are used when
	 * the custom inventory is used, and Simple when the 'no-custom-inventory'
	 * is toggled on
	 */
	private Function<Player, InventoryHandler> load = (player -> new CustomInventoryHandler(player));

	/**
	 * Used by the 'no-custom-inventory' config option
	 * 
	 * @param load
	 *            A function which takes a player as input and returns a new
	 *            inventoryProvider corresponding to the player
	 */
	public void setInventoryProvider(Function<Player, InventoryHandler> load) {
		Validate.notNull(load, "Function cannot be null");

		this.load = load;
	}

	public InventoryHandler getInventory(OfflinePlayer player) {
		return inventories.get(player.getUniqueId());
	}

	public InventoryHandler getInventory(UUID uuid) {
		return inventories.get(uuid);
	}

	public void setupData(Player player) {

		// Setup inventory data if not setup
		if (!inventories.containsKey(player.getUniqueId()))
			inventories.put(player.getUniqueId(), load.apply(player));

			// Or else refresh player instance
		else
			getInventory(player.getUniqueId()).updatePlayer(player);
	}

	public void unloadData(OfflinePlayer player) {
		unloadData(player.getUniqueId());
	}

	public void unloadData(UUID uuid) {
		inventories.remove(uuid);
	}

	public Collection<InventoryHandler> getLoaded() {
		return inventories.values();
	}

	/**
	 * Called when the server stops
	 */
	public abstract void save();

	/**
	 * This only needs to do something when the custom inventory handler
	 * is enabled. If items are stored in the player vanilla item, nothing
	 * will be called.
	 *
	 * @param player Player data to save
	 */
	public void save(OfflinePlayer player) {
		InventoryHandler handler = getInventory(player);
		if (handler instanceof CustomInventoryHandler)
			save((CustomInventoryHandler) handler);
	}

	/**
	 * Called either when the server stops, or when the 'save-on-log-off'
	 * option is toggled on and the player leaves the server. This has
	 * the effect of saving player data
	 */
	public abstract void save(CustomInventoryHandler data);

	/**
	 * Called when a player logs on the server and his data has to be loaded.
	 */
	public abstract void load(CustomInventoryHandler data);
}
