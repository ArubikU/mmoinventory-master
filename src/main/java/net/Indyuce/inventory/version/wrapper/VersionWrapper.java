package net.Indyuce.inventory.version.wrapper;

import org.bukkit.Material;

/**
 * NMS free use of version wrapper. Since no NMS is used
 * to handle NBT tags from items, the plugin is not compatible
 * with spigot builds from 1.12 to 1.13
 */
public interface VersionWrapper {
    boolean isHelmet(Material material);
}
