package net.Indyuce.inventory.compat.list;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import net.Indyuce.inventory.compat.ClassModule;
import net.Indyuce.inventory.compat.LevelModule;
import net.Indyuce.inventory.inventory.InventoryHandler;

public class HeroesHook implements LevelModule, ClassModule {

    @Override
    public int getLevel(InventoryHandler player) {
        return getHero(player).getHeroLevel();
    }

    @Override
    public String getClass(InventoryHandler player) {
        return getHero(player).getHeroClass().getName();
    }

    private Hero getHero(InventoryHandler player) {
        return Heroes.getInstance().getCharacterManager().getHero(player.getPlayer());
    }
}