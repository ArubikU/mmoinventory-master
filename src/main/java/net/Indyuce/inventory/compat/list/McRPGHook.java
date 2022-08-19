package net.Indyuce.inventory.compat.list;

import net.Indyuce.inventory.compat.LevelModule;
import net.Indyuce.inventory.inventory.InventoryHandler;
import us.eunoians.mcrpg.players.PlayerManager;

public class McRPGHook implements LevelModule {

    @Override
    public int getLevel(InventoryHandler player) {
        try {
            return PlayerManager.getPlayer(player.getUniqueId()).getPowerLevel();
        } catch (Exception exception) {
            return 0;
        }
    }
}