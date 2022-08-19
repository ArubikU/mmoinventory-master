package net.Indyuce.inventory.compat.list;

import me.baks.rpl.PlayerList;
import net.Indyuce.inventory.compat.LevelModule;
import net.Indyuce.inventory.inventory.InventoryHandler;

/**
 * This plugin is old AF... still implements player data
 * storage using player names instead of UUIDs
 */
public class RPGPlayerLevelingHook implements LevelModule {

    @Override
    public int getLevel(InventoryHandler player) {
        PlayerList var2 = PlayerList.getByName(player.getPlayer().getName());
        return var2 == null ? 0 : var2.getPlayerLevel();
    }
}