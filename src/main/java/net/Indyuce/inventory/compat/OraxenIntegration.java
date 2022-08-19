package net.Indyuce.inventory.compat;

import io.th0rgal.oraxen.OraxenPlugin;
import net.Indyuce.inventory.MMOInventory;
import net.Indyuce.inventory.slot.CustomSlot;
import net.Indyuce.inventory.util.ConfigFile;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;

public class OraxenIntegration {

    public OraxenIntegration() {
        ConfigFile configFile = new ConfigFile(OraxenPlugin.get(), "/items", "mmoinventory-hook");
        FileConfiguration config = configFile.getConfig();

        // Clear previous config
        for (String key : config.getKeys(false))
            config.set(key, null);

        // Generate items
        for (CustomSlot slot : MMOInventory.plugin.getSlotManager().getLoaded()) {
            String key = "mmoinventory_" + getOraxenFormat(slot);
            config.set(key + ".material", slot.getItem().getType().name());
            config.set(key + ".excludeFromInventory", true);
            config.set(key + ".Pack.custom_model_data", slot.getItem().getItemMeta().getCustomModelData());
            config.set(key + ".Pack.generate_model", true);
            config.set(key + ".Pack.parent_model", "item/generated");
            config.set(key + ".Pack.textures", Arrays.asList("items/mmoinventory/" + getOraxenFormat(slot)));
        }

        configFile.save();
    }

    private String getOraxenFormat(CustomSlot slot) {
        return slot.getId().replace("-", "_");
    }
}
