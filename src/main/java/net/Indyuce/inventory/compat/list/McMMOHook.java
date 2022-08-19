package net.Indyuce.inventory.compat.list;

import com.gmail.nossr50.api.ExperienceAPI;
import net.Indyuce.inventory.compat.LevelModule;
import net.Indyuce.inventory.inventory.InventoryHandler;

public class McMMOHook implements LevelModule {

    @Override
    public int getLevel(InventoryHandler player) {
        try {
            return ExperienceAPI.getPowerLevel(player.getPlayer());
        } catch (Exception exception) {
            return 0;
        }
    }
}