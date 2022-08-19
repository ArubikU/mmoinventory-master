package net.Indyuce.inventory.util;

import net.Indyuce.inventory.MMOInventory;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class Utils {
    private static final NamespacedKey BUTTON = new NamespacedKey(MMOInventory.plugin, "Button");

    public static boolean isButton(ItemStack item) {
        return item != null && item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(BUTTON, PersistentDataType.BYTE);
    }

    @NotNull
    public static String enumName(@NotNull String str) {
        return str.toUpperCase().replace("-", "_").replace(" ", "_");
    }

    private static final NamespacedKey GUI_ITEM_ID = new NamespacedKey(MMOInventory.plugin, "GuiItemId");

    public static boolean isGuiItem(ItemStack item) {
        return item != null && item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(GUI_ITEM_ID, PersistentDataType.STRING);
    }

    public static String getGuiItemId(ItemStack item) {
        return item.getItemMeta().getPersistentDataContainer().get(GUI_ITEM_ID, PersistentDataType.STRING);
    }

    /**
     * @return Checks for both null and AIR material. Really
     *         handy for events to check if something is happening or not
     */
    public static boolean isAir(ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }
}
