package ru.endlesscode.rpginventory;

import net.Indyuce.inventory.MMOInventory;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @deprecated illegal workaround
 */
@Deprecated
public class RPGInventory extends JavaPlugin {
    public static RPGInventory getInstance() {
        return MMOInventory.plugin;
    }
}
