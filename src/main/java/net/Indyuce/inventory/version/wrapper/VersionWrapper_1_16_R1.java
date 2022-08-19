package net.Indyuce.inventory.version.wrapper;

import org.bukkit.Material;

public class VersionWrapper_1_16_R1 implements VersionWrapper {
    @Override
    public boolean isHelmet(Material material) {
        return material.name().endsWith("HELMET") || material == Material.CARVED_PUMPKIN
                || material == Material.PLAYER_HEAD || material == Material.CREEPER_HEAD
                || material == Material.SKELETON_SKULL || material == Material.WITHER_SKELETON_SKULL;
    }
}
