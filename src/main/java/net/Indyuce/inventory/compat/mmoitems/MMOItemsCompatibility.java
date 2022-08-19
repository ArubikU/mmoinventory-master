package net.Indyuce.inventory.compat.mmoitems;

import net.Indyuce.inventory.MMOInventory;
import net.Indyuce.inventory.api.event.ItemEquipEvent;
import net.Indyuce.inventory.compat.InventoryUpdater;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.comp.inventory.PlayerInventory;
import net.Indyuce.mmoitems.comp.inventory.RPGInventoryHook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * No need to implement {@link PlayerInventory} because MMOItems
 * already consider RPGInventory to be installed. Therefore we
 * have to consider that an instance of {@link RPGInventoryHook}
 * is already registered in MMOItems
 */
public class MMOItemsCompatibility implements Listener, InventoryUpdater {
    public MMOItemsCompatibility() {
        Bukkit.getPluginManager().registerEvents(this, MMOInventory.plugin);
        MMOInventory.plugin.registerInventoryUpdater(this);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void updateWhenEquippingItem(ItemEquipEvent event) {
        Bukkit.getScheduler().runTaskLater(MMOInventory.plugin, () -> PlayerData.get(event.getPlayer()).updateInventory(), 0);
    }

    @Override
    public void updateInventory(Player player) {
        PlayerData.get(player).updateInventory();
    }
}
