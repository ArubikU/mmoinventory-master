package net.Indyuce.inventory.listener;

import net.Indyuce.inventory.gui.PlayerInventoryView;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class GuiListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void registerClicks(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof PlayerInventoryView)
            ((PlayerInventoryView) event.getInventory().getHolder()).whenClicked(event);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void registerDrags(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof PlayerInventoryView)
            event.setCancelled(true);
    }

	// @EventHandler
	// public void b(InventoryCloseEvent event) {
	// if (event.getInventory().getHolder() instanceof PlayerInventoryView)
	// ((PlayerInventoryView)
	// event.getInventory().getHolder()).whenClosed(event);
	// }
}
