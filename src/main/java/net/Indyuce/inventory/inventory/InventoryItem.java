package net.Indyuce.inventory.inventory;

import net.Indyuce.inventory.slot.CustomSlot;
import org.bukkit.inventory.ItemStack;

public class InventoryItem {
    private final ItemStack stack;
    private final CustomSlot slot;

    public InventoryItem(ItemStack stack, CustomSlot slot) {
        this.stack = stack;
        this.slot = slot;
    }

    public ItemStack getItemStack() {
        return stack;
    }

    public CustomSlot getSlot() {
        return slot;
    }
}
