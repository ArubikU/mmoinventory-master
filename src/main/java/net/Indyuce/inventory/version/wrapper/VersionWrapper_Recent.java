package net.Indyuce.inventory.version.wrapper;

import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;

public class VersionWrapper_Recent implements VersionWrapper {
    @Override
    public boolean isHelmet(Material material) {
        return material.getEquipmentSlot() == EquipmentSlot.HEAD;
    }
}
