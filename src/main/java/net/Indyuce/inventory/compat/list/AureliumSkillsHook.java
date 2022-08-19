package net.Indyuce.inventory.compat.list;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.data.PlayerData;
import net.Indyuce.inventory.compat.LevelModule;
import net.Indyuce.inventory.inventory.InventoryHandler;
import org.bukkit.Bukkit;

public class AureliumSkillsHook implements LevelModule {
    private final AureliumSkills aSkills = (AureliumSkills) Bukkit.getPluginManager().getPlugin("AureliumSkills");

    @Override
    public int getLevel(InventoryHandler player) {
        com.archyx.aureliumskills.data.PlayerData playerData = aSkills.getPlayerManager().getPlayerData(player.getUniqueId());
        return playerData.getPowerLevel();
    }

    private PlayerData getPlayerData(InventoryHandler player) {
        return aSkills.getPlayerManager().getPlayerData(player.getUniqueId());
    }
}