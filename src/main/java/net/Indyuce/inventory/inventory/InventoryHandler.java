package net.Indyuce.inventory.inventory;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class InventoryHandler {
    private final UUID uuid;

    /**
     * Player instance is not final because it needs to be updated every time
     * the player joins; this instance is used to equip vanilla items
     */
    @Nullable
    protected Player player;

    private long lastLogActivity;

    public InventoryHandler(Player player) {
        this.uuid = player.getUniqueId();
        updatePlayer(player);
    }

    public UUID getUniqueId() {
        return uuid;
    }

    @NotNull
    public Player getPlayer() {
        return Objects.requireNonNull(player, "Player is offline");
    }

    public void updatePlayer(@Nullable Player player) {
        this.player = player;
        this.lastLogActivity = System.currentTimeMillis();
    }

    /**
     * @param lookupMode The way MMOInv collects and filters
     *                   items in the returned collection
     * @return The extra items from the player's custom inventory
     */
    public Collection<InventoryItem> getItems(InventoryLookupMode lookupMode) {
        Set<InventoryItem> items = new HashSet<>();
        for (InventoryItem invItem : retrieveItems())
            if (lookupMode == InventoryLookupMode.IGNORE_RESTRICTIONS || invItem.getSlot().checkSlotRestrictions(this, invItem.getItemStack()))
                items.add(invItem);
        return items;
    }

    /**
     * @return A collection of all the extra items (vanilla slots put aside) ie
     *         accessories placed in custom RPG slots. This should include all items even
     *         the ones not usable by the player.
     */
    protected abstract Collection<InventoryItem> retrieveItems();
}
