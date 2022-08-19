package net.Indyuce.inventory.compat.list;

import net.Indyuce.inventory.compat.ClassModule;
import net.Indyuce.inventory.compat.LevelModule;
import net.Indyuce.inventory.inventory.InventoryHandler;

public class DefaultHook implements LevelModule, ClassModule {

    @Override
    public int getLevel(InventoryHandler player) {
        return player.getPlayer().getLevel();
    }

    /**
     * Although there is no player class in vanilla Minecraft,
     * this method is implemented in the default MMOCore class
     * module so that the user can use 'default' in the config
     */
    @Override
    public String getClass(InventoryHandler player) {
        return "None";
    }
}