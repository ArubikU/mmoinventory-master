package net.Indyuce.inventory.slot.restriction;

import net.Indyuce.inventory.inventory.InventoryHandler;
import net.Indyuce.inventory.slot.CustomSlot;
import net.Indyuce.inventory.util.LineConfig;
import org.bukkit.inventory.ItemStack;

public class PermissionRestriction extends SlotRestriction {
    private final String permission;

    public PermissionRestriction(LineConfig config) {
        config.validate("perm");
        permission = config.getString("perm");
    }

    @Override
    public boolean isVerified(InventoryHandler provider, CustomSlot slot, ItemStack item) {
        return provider.getPlayer().hasPermission(permission);
    }
}
