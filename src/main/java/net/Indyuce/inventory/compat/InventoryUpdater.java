package net.Indyuce.inventory.compat;

import org.bukkit.entity.Player;

/**
 * Used by MMOInventory to list all plugins which require inventory
 * updates. The method provided by this interface is called whenever
 * an inventory update is required by plugins.
 */
public interface InventoryUpdater {

    public void updateInventory(Player player);
}
