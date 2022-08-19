package net.Indyuce.inventory.compat.mmoitems;

import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.inventory.inventory.InventoryHandler;
import net.Indyuce.inventory.inventory.InventoryItem;
import net.Indyuce.inventory.inventory.InventoryLookupMode;
import net.Indyuce.inventory.slot.CustomSlot;
import net.Indyuce.inventory.slot.restriction.SlotRestriction;
import net.Indyuce.inventory.util.LineConfig;
import org.bukkit.inventory.ItemStack;

public class UniqueRestriction extends SlotRestriction {

    /**
     * Cannot use twice the same item in two different slots
     */
    public UniqueRestriction(LineConfig config) {
        // Nothing
    }

    @Override
    public boolean isVerified(InventoryHandler provider, CustomSlot slot, ItemStack item) {
        NBTItem nbtItem = NBTItem.get(item);
        String set = nbtItem.getString("MMOITEMS_ACCESSORY_SET");

        // Get Equipped Items
        for (InventoryItem invItem : provider.getItems(InventoryLookupMode.IGNORE_RESTRICTIONS)) {

            // Forget if same slot
            if (slot.getIndex() == invItem.getSlot().getIndex())
                continue;

            // Same MMOItem?
            NBTItem nbtInvItem = NBTItem.get(invItem.getItemStack());
            boolean sameItem = getStringMMOItem(nbtItem).equalsIgnoreCase(getStringMMOItem(nbtInvItem));
            boolean sameSet = (set != null) && (set.length() > 0) && set.equalsIgnoreCase(nbtInvItem.getString("MMOITEMS_ACCESSORY_SET"));

            // Cancel if the mmoitem is the same or the set is the same.
            if (sameItem || sameSet)
                return false;
        }

        return true;
    }

    private String getStringMMOItem(NBTItem item) {
        return item.getString("MMOITEMS_ITEM_ID") + "." + item.getString("MMOITEMS_ITEM_TYPE");
    }
}
