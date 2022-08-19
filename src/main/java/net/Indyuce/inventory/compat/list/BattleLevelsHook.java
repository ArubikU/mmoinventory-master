package net.Indyuce.inventory.compat.list;

import me.robin.battlelevels.api.BattleLevelsAPI;
import net.Indyuce.inventory.compat.LevelModule;
import net.Indyuce.inventory.inventory.InventoryHandler;

public class BattleLevelsHook implements LevelModule {

    @Override
    public int getLevel(InventoryHandler player) {
        return BattleLevelsAPI.getLevel(player.getUniqueId());
    }
}