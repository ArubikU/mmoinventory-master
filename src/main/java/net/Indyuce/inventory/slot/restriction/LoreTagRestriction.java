package net.Indyuce.inventory.slot.restriction;

import net.Indyuce.inventory.inventory.InventoryHandler;
import net.Indyuce.inventory.slot.CustomSlot;
import net.Indyuce.inventory.util.LineConfig;
import net.Indyuce.inventory.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * This easy implementation of a slot restriction can be used
 * when LoreAttributesRecoded is installed to easily implement
 * item types.
 * <p>
 * Item types are basically a small colored lore tag like '&cSword'
 * at the beginning of the item lore (it can be positionned anywhere).
 * <p>
 * Checking if the item has a specific item type is basically looking
 * for that tag in the item lore.
 */
public class LoreTagRestriction extends SlotRestriction {
    private final String loreTag;
    private final CheckType checkType;

    public LoreTagRestriction(LineConfig config) {
        config.validate("tag");
        loreTag = ChatColor.translateAlternateColorCodes('&', config.getString("tag"));
        checkType = config.contains("check") ? CheckType.valueOf(Utils.enumName(config.getString("check"))) : CheckType.EQUALS;
    }

    @Override
    public boolean isVerified(InventoryHandler provider, CustomSlot slot, ItemStack item) {
        if (!item.hasItemMeta())
            return false;

        ItemMeta meta = item.getItemMeta();
        if (!meta.hasLore())
            return false;

        for (String checked : meta.getLore())
            if (checkType.operation.check(checked, loreTag))
                return true;

        return false;
    }

    enum CheckType {
        EQUALS(String::equals),
        CONTAINS(String::contains),
        STARTS_WITH(String::startsWith),
        ENDS_WITH(String::endsWith);

        final Operation operation;

        CheckType(Operation operation) {
            this.operation = operation;
        }
    }

    @FunctionalInterface
    interface Operation {
        boolean check(String container, String pattern);
    }
}
