package net.Indyuce.inventory.compat.list;

import net.Indyuce.inventory.compat.ClassModule;
import net.Indyuce.inventory.compat.LevelModule;
import net.Indyuce.inventory.inventory.InventoryHandler;
import net.Indyuce.mmocore.api.player.PlayerData;

public class MMOCoreHook implements LevelModule, ClassModule {

    @Override
    public int getLevel(InventoryHandler player) {
        return getPlayerData(player).getLevel();
    }

    @Override
    public String getClass(InventoryHandler player) {
        return getPlayerData(player).getProfess().getName();
    }

    private PlayerData getPlayerData(InventoryHandler player) {
        return PlayerData.get(player.getUniqueId());
    }
}