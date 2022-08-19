package net.Indyuce.inventory.inventory;

import net.Indyuce.inventory.MMOInventory;
import net.Indyuce.inventory.slot.CustomSlot;
import net.Indyuce.inventory.util.Utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SimpleInventoryHandler extends InventoryHandler {

    /**
     * Used when MMOInventory does not use the custom inventory and when the
     * items must be placed in the vanilla player's inventory
     */
    public SimpleInventoryHandler(Player player) {
        super(player);
    }

    @Override
    public Collection<InventoryItem> retrieveItems() {
        Set<InventoryItem> set = new HashSet<>();

        // For each special slot
        for (CustomSlot slot : MMOInventory.plugin.getSlotManager().getCustomSlots()) {

            // Get that item
            ItemStack item = player.getInventory().getItem(slot.getIndex());

            // Is the item not a 'default' MMOInventory display thingy
            if (!Utils.isAir(item) && !Utils.isGuiItem(item))
                set.add(new InventoryItem(item, slot));
        }

        return set;
    }
}
