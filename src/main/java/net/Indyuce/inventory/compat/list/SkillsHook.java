package net.Indyuce.inventory.compat.list;

import me.leothepro555.skills.database.managers.PlayerInfo;
import me.leothepro555.skills.main.Skills;
import net.Indyuce.inventory.compat.ClassModule;
import net.Indyuce.inventory.compat.LevelModule;
import net.Indyuce.inventory.inventory.InventoryHandler;

public class SkillsHook implements LevelModule, ClassModule {

    @Override
    public int getLevel(InventoryHandler player) {
        return getPlayerInfo(player).getLevel();
    }

    @Override
    public String getClass(InventoryHandler player) {
        return getPlayerInfo(player).getSkill().getLanguageName().getDefault();
    }

    private PlayerInfo getPlayerInfo(InventoryHandler player) {
        return Skills.get().getPlayerDataManager().getOrLoadPlayerInfo(player.getPlayer());
    }
}