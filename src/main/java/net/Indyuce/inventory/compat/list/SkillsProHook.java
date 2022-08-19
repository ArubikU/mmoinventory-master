package net.Indyuce.inventory.compat.list;

import net.Indyuce.inventory.compat.ClassModule;
import net.Indyuce.inventory.compat.LevelModule;
import net.Indyuce.inventory.inventory.InventoryHandler;
import org.bukkit.ChatColor;
import org.skills.data.managers.SkilledPlayer;
import org.skills.main.SkillsPro;

public class SkillsProHook implements LevelModule, ClassModule {

    @Override
    public int getLevel(InventoryHandler player) {
        return getSkilledPlayer(player).getLevel();
    }

    @Override
    public String getClass(InventoryHandler player) {
        return ChatColor.stripColor(getSkilledPlayer(player).getSkill().getDisplayName());
    }

    private SkilledPlayer getSkilledPlayer(InventoryHandler player) {
        return SkillsPro.get().getPlayerDataManager().getData(player.getUniqueId());
    }
}