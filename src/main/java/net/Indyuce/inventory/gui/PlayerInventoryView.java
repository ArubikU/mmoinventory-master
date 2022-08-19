package net.Indyuce.inventory.gui;

import net.Indyuce.inventory.MMOInventory;
import net.Indyuce.inventory.api.event.ItemEquipEvent;
import net.Indyuce.inventory.inventory.CustomInventoryHandler;
import net.Indyuce.inventory.slot.CustomSlot;
import net.Indyuce.inventory.slot.SlotType;
import net.Indyuce.inventory.util.LineConfig;
import net.Indyuce.inventory.util.Utils;
import net.skinsrestorer.api.PlayerWrapper;
import net.skinsrestorer.api.SkinsRestorerAPI;
import net.skinsrestorer.api.property.GenericProperty;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;

import com.herocraftonline.items.api.item.ItemFactory;

import io.lumine.cosmetics.MCCosmeticsPlugin;
import io.lumine.cosmetics.api.cosmetics.EquippedCosmetic;
import io.lumine.cosmetics.managers.back.BackManager;
import io.lumine.cosmetics.players.Profile;
import me.clip.placeholderapi.PlaceholderAPI;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

public class PlayerInventoryView implements InventoryHolder {
    private final CustomInventoryHandler data;
    private final Player player, target;
    private final Boolean offline;

    private static final List<InventoryAction> supported = Arrays.asList(InventoryAction.PICKUP_ALL,
            InventoryAction.SWAP_WITH_CURSOR,
            InventoryAction.PLACE_ALL);

    public PlayerInventoryView(Player player) {
        this(player, player, false);
    }

    /**
     * @param player Player opening the GUI and manipulating the items
     * @param target The player owning the inventory
     */
    public PlayerInventoryView(Player player, Player target, Boolean offline) {
        this.target = target;
        this.player = player;
        this.offline = offline;

        data = (CustomInventoryHandler) MMOInventory.plugin.getDataManager().getInventory(target);
    }

    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, MMOInventory.plugin.inventorySlots,
                MMOInventory.plugin.getPlaceholderParser().parse(target,
                        target.equals(player) ? MMOInventory.plugin.getTranslation("inventory-name.self")
                                : MMOInventory.plugin.getTranslation("inventory-name.other").replace("{name}",
                                        target.getName())));

        // Load custom items or vanilla items depending on slot type
        for (CustomSlot slot : MMOInventory.plugin.getSlotManager().getLoaded()) {
            ItemStack item = data.getItem(slot);
            if (slot.getType().getVanillaSlotHandler() != null) {
                item = slot.getType().getVanillaSlotHandler().retrieveItem(target);
            }
            inv.setItem(slot.getIndex(), Utils.isAir(item) ? slot.displayItem(target) : item);
        }
        for (int j = 0; j < inv.getSize(); j++) {
            ItemStack item = inv.getItem(j);
            if (!Utils.isAir(item) && MMOInventory.plugin.getSlotManager().get(j) != null) {
                if (Bukkit.getPluginManager().getPlugin("MCCosmetics") != null && MMOInventory.plugin
                        .getSlotManager().get(j).getConfig().getString("mccosmetic") != null) {
                    Profile p = MCCosmeticsPlugin.inst()
                            .getProfiles().getProfile(target);

                    if (p != null) {

                        if (p.getEquippedCosmetics()
                                .containsKey(MMOInventory.plugin.getSlotManager().get(j).getConfig()
                                        .getString("mccosmetic")
                                        .toUpperCase())) {
                            try {
                                EquippedCosmetic cos = p.getEquippedCosmetics()
                                        .get(MMOInventory.plugin.getSlotManager().get(j).getConfig()
                                                .getString("mccosmetic")
                                                .toUpperCase())
                                        .toEquippedCosmetic().orElseThrow();
                                if (cos != null) {
                                    item = cos.getVariant().getIcon().build(p, target);
                                }
                            } catch (NoSuchElementException e) {
                                e.printStackTrace();
                            }
                        }

                        inv.setItem(j, item);
                    }

                }

                if (MMOInventory.plugin.getSlotManager().get(j).getType() == SlotType.HEAD
                        && MMOInventory.plugin.getSlotManager().get(j).getConfig().getString("owner") != null) {
                    SkullMeta meta = (SkullMeta) item.getItemMeta();
                    switch (MMOInventory.plugin.getSlotManager().get(j).getConfig().getString("owner")
                            .toUpperCase()) {
                        case "TARGET": {
                            meta.setOwner(target.getName());
                            break;
                        }
                        case "PLAYER": {
                            meta.setOwner(player.getName());
                            break;
                        }
                        default:
                            meta.setOwner(target.getName());
                    }

                    item.setItemMeta(meta);
                    inv.setItem(j, item);
                }
            }
            if (Utils.isAir(item) && MMOInventory.plugin.getSlotManager().hasFiller())
                inv.setItem(j, MMOInventory.plugin.getSlotManager().getFiller().displayItem(target));
        }
        return inv;
    }

    public void open() {
        player.openInventory(getInventory());
    }

    public void fetchCommand(String line, Player arg1, Player arg2) {
        String cmd = line;
        cmd = cmd.replace("%sender%", arg1.getName())
                .replace("%target%", arg2.getName());
        cmd = PlaceholderAPI.setPlaceholders(arg2, cmd);

        if (cmd.equalsIgnoreCase("closeinv")) {
            arg1.closeInventory();
            return;
        }

        Bukkit.getServer().dispatchCommand(
                Bukkit.getServer().getConsoleSender(), cmd);
    }

    public void whenClicked(InventoryClickEvent event) {

        /*
         * Shift clicking finds the ONE slot that can host your item
         * and automatically sends it there. There's a distinction to
         * be made between clicking in the player inventory and clicking
         * on an item which has already been equipped and which has to
         * be sent back to the player's inventory.
         *
         * There's also a problem with item amounts? It's much simpler
         * to just place all the stacked items as it's anyways quite
         * rare to have accessories with max stacks size greater than 1
         */
        ItemStack picked = event.getCurrentItem();

        ConfigurationSection cnf = MMOInventory.plugin.getSlotManager().get(event.getSlot()).getConfig();

        if (MMOInventory.plugin.getSlotManager().get(event.getSlot()) != null) {
            if (MMOInventory.plugin.getSlotManager().get(event.getSlot()).getType() == SlotType.COMMAND ||
                    (MMOInventory.plugin.getSlotManager().get(event.getSlot()).getConfig().getString("sub-type") != null
                            &&
                            MMOInventory.plugin.getSlotManager().get(event.getSlot()).getConfig().getString("sub-type")
                                    .equalsIgnoreCase("COMMAND"))) {

                Boolean runcmd = true;

                if (MMOInventory.plugin.getSlotManager().get(event.getSlot()).getConfig().get("conditions") != null) {

                    for (String condition : MMOInventory.plugin.getSlotManager().get(event.getSlot()).getConfig()
                            .getStringList("conditions")) {

                        LineConfig line = new LineConfig(condition);
                        switch (line.getKey().toUpperCase()) {
                            case "OTHERPLAYER": {
                                if (event.getWhoClicked().getName().equalsIgnoreCase((target.getName()))) {
                                    runcmd = false;
                                    if (MMOInventory.plugin.getSlotManager().get(event.getSlot())
                                            .getConfig().getString(line.getString("action", "null")) != null) {
                                        String cmd = MMOInventory.plugin.getSlotManager()
                                                .get(event.getSlot())
                                                .getConfig().getString(line.getString("action", "null"));
                                        fetchCommand(cmd, (Player) event.getWhoClicked(), target);
                                    }
                                }
                                break;
                            }
                            case "SAMEPLAYER": {
                                if (!event.getWhoClicked().getName().equalsIgnoreCase((target.getName()))) {
                                    runcmd = false;
                                    if (MMOInventory.plugin.getSlotManager().get(event.getSlot())
                                            .getConfig().getString(line.getString("action", "null")) != null) {
                                        String cmd = MMOInventory.plugin.getSlotManager()
                                                .get(event.getSlot())
                                                .getConfig().getString(line.getString("action", "null"));

                                        fetchCommand(cmd, (Player) event.getWhoClicked(), target);
                                    }
                                }
                                break;
                            }
                            case "HAVEPERM": {
                                if (!event.getWhoClicked()
                                        .hasPermission(MMOInventory.plugin.getSlotManager().get(event.getSlot())
                                                .getConfig().getString(line.getString("perm", "null")))) {
                                    runcmd = false;
                                    String cmd = MMOInventory.plugin.getSlotManager()
                                            .get(event.getSlot())
                                            .getConfig().getString(line.getString("action", "null"));

                                    fetchCommand(cmd, (Player) event.getWhoClicked(), target);
                                }
                                break;
                            }
                            default:
                                break;
                        }

                    }

                }

                if (runcmd) {

                    if (cnf.getString(event.getAction().toString().toLowerCase() + ".commands") != null) {
                        List<String> commands = PlaceholderAPI.setPlaceholders(target,
                                MMOInventory.plugin.getSlotManager().get(event.getSlot()).getConfig()
                                        .getStringList(event.getAction().toString().toLowerCase() + ".commands"));
                        commands.forEach(cmd -> {
                            fetchCommand(cmd, (Player) event.getWhoClicked(), target);
                        });
                    }

                    List<String> commands = PlaceholderAPI.setPlaceholders(target,
                            MMOInventory.plugin.getSlotManager().get(event.getSlot()).getConfig()
                                    .getStringList("commands"));
                    commands.forEach(cmd -> {
                        fetchCommand(cmd, (Player) event.getWhoClicked(), target);
                    });
                }
                event.setCancelled(true);
            }
        }

        if (!event.getWhoClicked().getName().equalsIgnoreCase(target.getName())) {
            if (!event.getWhoClicked().hasPermission("mmoinventory.admin")) {
                event.setCancelled(true);
                return;
            }
        }

        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            event.setCancelled(true);

            // Nothing to do
            if (Utils.isAir(event.getCurrentItem()) || Utils.isGuiItem(picked))
                return;

            if (event.getClickedInventory().equals(event.getView().getBottomInventory())) {

                // Find the best slot available
                CustomSlot best = findBestSlot(picked);
                if (best == null)
                    return;

                ItemEquipEvent called = new ItemEquipEvent(target, event.getCurrentItem(), best,
                        ItemEquipEvent.EquipAction.SHIFT_CLICK_EQUIP);
                Bukkit.getPluginManager().callEvent(called);
                if (called.isCancelled())
                    return;

                data.setItem(best, picked);
                event.getInventory().setItem(best.getIndex(), picked);
                event.setCurrentItem(null);

                // For all active watchers
                forEachWatcher(view -> view.getTopInventory().setItem(best.getIndex(), picked));

            } else {

                // Get the clicked slot
                CustomSlot slot = MMOInventory.plugin.getSlotManager().get(event.getRawSlot());
                if (slot == null)
                    return;

                // Find a place where to put the item
                int empty = player.getInventory().firstEmpty();
                if (empty == -1)
                    return;

                ItemEquipEvent called = new ItemEquipEvent(target, event.getCurrentItem(), null,
                        ItemEquipEvent.EquipAction.SHIFT_CLICK_UNEQUIP);
                Bukkit.getPluginManager().callEvent(called);
                if (called.isCancelled())
                    return;

                data.setItem(slot, null);
                player.getInventory().setItem(empty, event.getCurrentItem());
                event.setCurrentItem(slot.displayItem(target));

                // For all active watchers
                forEachWatcher(view -> view.getTopInventory().setItem(slot.getIndex(), slot.displayItem(target)));
            }

            return;
        }

        // Only a few types of clicks are supported
        if (!supported.contains(event.getAction())) {
            event.setCancelled(true);
            return;
        }

        // Prevent any interaction with filler slots
        if (Utils.isGuiItem(picked) && Utils.getGuiItemId(picked).equals("fill")) {
            event.setCancelled(true);
            return;
        }

        // Player tries to pickup a slot item, without equipping any
        if (Utils.isGuiItem(picked) && Utils.isAir(event.getCursor())) {
            event.setCancelled(true);
            return;
        }

        // Check for clicked slot
        CustomSlot slot = MMOInventory.plugin.getSlotManager().get(event.getRawSlot());
        if (slot == null) {

            // Prevent from interacting with empty spaces
            if (event.getClickedInventory().equals(event.getInventory()))
                event.setCancelled(true);

            return;
        }

        // Check if item can be equipped (apply slot restrictions)
        ItemStack cursor = event.getCursor();
        if (!Utils.isAir(event.getCursor())) {

            // Prevents equipping stacked items
            if (MMOInventory.plugin.getConfig().getBoolean("disable-equiping-stacked-items", true)
                    && event.getCursor().getAmount() > 1) {
                event.setCancelled(true);
                return;
            }

            // Check for vanilla AND custom slot restrictions
            if (!slot.canHost(data, cursor)) {
                event.setCancelled(true);
                return;
            }
        }

        /*
         * May be called with a null item as parameter if the player is
         * unequipping an item
         */
        ItemEquipEvent.EquipAction action = Utils.isAir(event.getCursor()) ? ItemEquipEvent.EquipAction.UNEQUIP
                : Utils.isGuiItem(cursor) ? ItemEquipEvent.EquipAction.EQUIP : ItemEquipEvent.EquipAction.SWAP_ITEMS;
        ItemEquipEvent equipEvent = new ItemEquipEvent(target, event.getCursor(), slot, action);
        Bukkit.getPluginManager().callEvent(equipEvent);
        if (equipEvent.isCancelled()) {
            event.setCancelled(true);
            return;
        }

        data.setItem(slot, event.getCursor());

        // For all active watchers
        ItemStack newItem = Utils.isAir(event.getCursor()) ? slot.displayItem(target) : event.getCursor();
        forEachWatcher(view -> view.getTopInventory().setItem(slot.getIndex(), newItem));

        /*
         * If the player has picked up an inventory slot item, remove it
         * instantly after checking the equip event was not canceled (bug
         * fix)
         */
        if (Utils.isGuiItem(picked))
            event.setCurrentItem(null);

        /*
         * If the player is taking away an item without swapping it, place
         * the inventory slot item back in the corresponding slot.
         *
         * This must be done using a delayed task otherwise this will replace
         * the event's current item
         */
        if (Utils.isAir(event.getCursor()))
            Bukkit.getScheduler().runTask(MMOInventory.plugin,
                    () -> event.getInventory().setItem(slot.getIndex(), slot.displayItem(target)));

        // Finally update the player's inventory
        MMOInventory.plugin.updateInventory(player);
    }

    /**
     * Used when shift clicking to find the best slot
     * available for a specific item.
     *
     * @param item The item being shift clicked
     * @return The best slot available for that item, or none if there isn't any.
     *         Shift clicking should not do anything then
     */
    private CustomSlot findBestSlot(ItemStack item) {
        for (CustomSlot slot : MMOInventory.plugin.getSlotManager().getLoaded())
            if ((slot.getType().isCustom() || slot.getType().getVanillaSlotHandler().supportsShiftClick())
                    && !data.hasItem(slot) && slot.canHost(data, item))
                return slot;

        return null;
    }

    /**
     * This is used when an admin edits the inventory of another
     * player, while the target player COULD have his inventory opened
     * as well.
     * <p>
     * This method iterates through all the online players whose
     * open inventory matches the target's rpg inventory
     */
    public void forEachWatcher(Consumer<InventoryView> consumer) {
        for (Player online : Bukkit.getOnlinePlayers())
            if (online.getOpenInventory() != null
                    && online.getOpenInventory().getTopInventory().getHolder() instanceof PlayerInventoryView) {
                PlayerInventoryView customGui = (PlayerInventoryView) online.getOpenInventory().getTopInventory()
                        .getHolder();
                if (!equals(customGui) && customGui.target.equals(target))
                    consumer.accept(online.getOpenInventory());
            }
    }
}
