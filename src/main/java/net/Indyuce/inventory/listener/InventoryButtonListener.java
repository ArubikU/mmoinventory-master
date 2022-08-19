package net.Indyuce.inventory.listener;

import net.Indyuce.inventory.MMOInventory;
import net.Indyuce.inventory.gui.PlayerInventoryView;
import net.Indyuce.inventory.util.InventoryButton;
import net.Indyuce.inventory.util.Utils;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Iterator;

public class InventoryButtonListener implements Listener {
    private final ItemStack icon;
    private final int slot;

    public InventoryButtonListener(ConfigurationSection config) {
        slot = config.getInt("slot");
        icon = new InventoryButton(config.getConfigurationSection("item")).getItem();
    }

    @EventHandler
    public void giveItemsOnJoin(PlayerJoinEvent event) {
        event.getPlayer().getInventory().setItem(slot, icon);
    }

    @EventHandler
    public void giveItemsOnRespawn(PlayerRespawnEvent event) {
        event.getPlayer().getInventory().setItem(slot, icon);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void blockInteractions(InventoryClickEvent event) {
        if (Utils.isButton(event.getCurrentItem())) {
            new PlayerInventoryView((Player) event.getWhoClicked()).open();
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void blockDrop(PlayerDeathEvent event) {
        Iterator<ItemStack> iterator = event.getDrops().iterator();
        while (iterator.hasNext()) {
            ItemStack next = iterator.next();
            if (Utils.isButton(next))
                iterator.remove();
        }
    }
}
