package net.Indyuce.inventory.slot.restriction;

import net.Indyuce.inventory.MMOInventory;
import net.Indyuce.inventory.inventory.InventoryHandler;
import net.Indyuce.inventory.slot.CustomSlot;
import net.Indyuce.inventory.util.LineConfig;
import org.bukkit.inventory.ItemStack;

public class LevelRestriction extends SlotRestriction {
    private final int minLevel;

    public LevelRestriction(LineConfig config) {
        config.validate("min");
        minLevel = config.getInt("min");
    }

    @Override
    public boolean isVerified(InventoryHandler provider, CustomSlot slot, ItemStack item) {
        return MMOInventory.plugin.getLevelModule().getLevel(provider) >= minLevel;
    }
}