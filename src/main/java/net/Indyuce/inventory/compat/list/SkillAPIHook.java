package net.Indyuce.inventory.compat.list;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import net.Indyuce.inventory.compat.ClassModule;
import net.Indyuce.inventory.compat.LevelModule;
import net.Indyuce.inventory.inventory.InventoryHandler;

public class SkillAPIHook implements LevelModule, ClassModule {

    @Override
    public int getLevel(InventoryHandler player) {
        PlayerData playerData = getPlayerData(player);
        return playerData.hasClass() ? playerData.getMainClass().getLevel() : 0;
    }

    @Override
    public String getClass(InventoryHandler player) {
        PlayerData playerData = getPlayerData(player);
        return playerData.hasClass() ? playerData.getMainClass().getData().getName() : "";
    }

    private PlayerData getPlayerData(InventoryHandler player) {
        return SkillAPI.getPlayerAccountData(player.getPlayer()).getActiveData();
    }
}