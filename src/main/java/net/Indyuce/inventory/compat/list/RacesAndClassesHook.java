package net.Indyuce.inventory.compat.list;

import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;
import net.Indyuce.inventory.compat.ClassModule;
import net.Indyuce.inventory.compat.LevelModule;
import net.Indyuce.inventory.inventory.InventoryHandler;

public class RacesAndClassesHook implements LevelModule, ClassModule {

    @Override
    public int getLevel(InventoryHandler player) {
        return getPlayer(player).getCurrentLevel();
    }

    @Override
    public String getClass(InventoryHandler player) {
        return getPlayer(player).getclass().getDisplayName();
    }

    private RaCPlayer getPlayer(InventoryHandler player) {
        return RaCPlayerManager.get().getPlayer(player.getUniqueId());
    }
}
