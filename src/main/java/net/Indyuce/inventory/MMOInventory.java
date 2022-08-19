package net.Indyuce.inventory;

import net.Indyuce.inventory.command.MMOInventoryCommand;
import net.Indyuce.inventory.command.MMOInventoryCompletion;
import net.Indyuce.inventory.compat.ClassModule;
import net.Indyuce.inventory.compat.InventoryUpdater;
import net.Indyuce.inventory.compat.LevelModule;
import net.Indyuce.inventory.compat.ModuleType;
import net.Indyuce.inventory.compat.list.DefaultHook;
import net.Indyuce.inventory.compat.mmoitems.MMOItemsCompatibility;
import net.Indyuce.inventory.compat.mmoitems.TypeRestriction;
import net.Indyuce.inventory.compat.mmoitems.UniqueRestriction;
import net.Indyuce.inventory.compat.mmoitems.UseRestriction;
import net.Indyuce.inventory.compat.OraxenIntegration;
import net.Indyuce.inventory.compat.placeholder.DefaultParser;
import net.Indyuce.inventory.compat.placeholder.PlaceholderAPIParser;
import net.Indyuce.inventory.compat.placeholder.PlaceholderParser;
import net.Indyuce.inventory.gui.PlayerInventoryView;
import net.Indyuce.inventory.inventory.SimpleInventoryHandler;
import net.Indyuce.inventory.listener.*;
import net.Indyuce.inventory.manager.DataManager;
import net.Indyuce.inventory.manager.SlotManager;
import net.Indyuce.inventory.manager.YamlDataManager;
import net.Indyuce.inventory.manager.sql.SQLDataManager;
import net.Indyuce.inventory.util.ConfigFile;
import net.Indyuce.inventory.util.Utils;
import net.Indyuce.inventory.version.ServerVersion;
import net.Indyuce.inventory.version.SpigotPlugin;
import net.Indyuce.inventory.version.wrapper.VersionWrapper;
import net.Indyuce.inventory.version.wrapper.VersionWrapper_Recent;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import ru.endlesscode.rpginventory.RPGInventory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class MMOInventory extends RPGInventory implements Listener {
    public static MMOInventory plugin;

    private final SlotManager slotManager = new SlotManager();

    /**
     * See {@link InventoryUpdater} for explanation. This is
     * the list of all the plugins which require inventory updates.
     */
    private final List<InventoryUpdater> inventoryUpdaters = new ArrayList<>();

    private LevelModule levelModule;
    private ClassModule classModule;
    private DataManager dataManager;
    private ServerVersion version;
    private VersionWrapper versionWrapper;
    private ConfigFile language;
    private PlaceholderParser placeholderParser = new DefaultParser();

    // Cached config options
    public int inventorySlots;

    public void onLoad() {
        plugin = this;

        if (Bukkit.getPluginManager().getPlugin("MMOItems") != null) {
            slotManager.registerRestriction(TypeRestriction::new, "mmoitemstype", "mmoitemtype", "mitype");
            slotManager.registerRestriction(config -> new UseRestriction(), "mmoitemslevel", "mmoitemlevel", "milevel");
            slotManager.registerRestriction(UniqueRestriction::new, "unique");
        }
    }

    public void onEnable() {

        try {
            version = new ServerVersion(Bukkit.getServer().getClass());
            getLogger().log(Level.INFO, "Detected Bukkit Version: " + version.toString());
            versionWrapper = (VersionWrapper) Class.forName("net.Indyuce.inventory.version.wrapper.VersionWrapper_" + version.toString().substring(1)).getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            versionWrapper = new VersionWrapper_Recent();
        }

        saveDefaultConfig();
        saveDefaultFile("language");
        saveDefaultFile("items");
        reload();

        // Startup plugin metrics
        // new Metrics(this, 99445);

        // Update check
        new SpigotPlugin(99445, this).checkForUpdate();

        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        Bukkit.getServer().getPluginManager().registerEvents(new GuiListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        // MySQL or YAML
        dataManager = getConfig().getBoolean("mysql.enabled") ? new SQLDataManager() : new YamlDataManager();

        // Load class module
        try {
            String moduleName = getConfig().getString("class-module");
            ModuleType moduleType = ModuleType.valueOf(Utils.enumName(moduleName));
            Validate.isTrue(moduleType.findPlugin(), "Plugin '" + moduleType.name() + "'not installed");
            Object module = moduleType.getModule();
            Validate.isTrue(module instanceof ClassModule, "Plugin '" + moduleType.name() + "' does not support classes");
            this.classModule = (ClassModule) module;
        } catch (Exception exception) {
            getLogger().log(Level.WARNING, "Could not initialize custom class module: " + exception.getMessage());
            this.classModule = new DefaultHook();
        }

        // Load level module
        try {
            String moduleName = getConfig().getString("level-module");
            ModuleType moduleType = ModuleType.valueOf(Utils.enumName(moduleName));
            Validate.isTrue(moduleType.findPlugin(), "Plugin '" + moduleType.name() + "'not installed");
            Object module = moduleType.getModule();
            Validate.isTrue(module instanceof LevelModule, "Plugin '" + moduleType.name() + "' does not support levels");
            this.levelModule = (LevelModule) module;
        } catch (Exception exception) {
            getLogger().log(Level.WARNING, "Could not initialize custom level module: " + exception.getMessage());
            this.levelModule = new DefaultHook();
        }

        if (getConfig().getBoolean("resource-pack.enabled"))
            Bukkit.getServer().getPluginManager().registerEvents(new ResourcePack(getConfig().getConfigurationSection("resource-pack")), this);

        if (getConfig().getBoolean("no-custom-inventory")) {
            dataManager.setInventoryProvider(SimpleInventoryHandler::new);
            Bukkit.getPluginManager().registerEvents(new NoCustomInventory(), this);
        } else {

            if (getConfig().getBoolean("save-on-leave"))
                Bukkit.getPluginManager().registerEvents(new SaveOnLeave(), this);

            if (getConfig().getBoolean("inventory-button.enabled"))
                Bukkit.getPluginManager().registerEvents(new InventoryButtonListener(getConfig().getConfigurationSection("inventory-button")), this);

            if (getConfig().getBoolean("drop-on-death"))
                Bukkit.getServer().getPluginManager().registerEvents(new DeathDrops(), this);
        }

        getCommand("mmoinventory").setExecutor(new MMOInventoryCommand());
        getCommand("mmoinventory").setTabCompleter(new MMOInventoryCompletion());

        // /reload friendly
        Bukkit.getOnlinePlayers().forEach(dataManager::setupData);

        if (Bukkit.getPluginManager().getPlugin("MMOItems") != null) {
            new MMOItemsCompatibility();
            getLogger().log(Level.INFO, "Hooked onto MMOItems");
        }

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            placeholderParser = new PlaceholderAPIParser();
            getLogger().log(Level.INFO, "Hooked onto PlaceholderAPI");
        }

        if (getServer().getPluginManager().getPlugin("Oraxen") != null) {
            new OraxenIntegration();
            getLogger().log(Level.INFO, "Hooked onto Oraxen");
        }
    }

    public void onDisable() {

        // Close open inventories
        for (Player player : Bukkit.getOnlinePlayers())
            if (player.getOpenInventory() != null && player.getOpenInventory().getTopInventory().getHolder() instanceof PlayerInventoryView)
                player.closeInventory();

        dataManager.save();
    }

    public void reload() {
        reloadConfig();
        language = new ConfigFile("language");
        slotManager.reload();

        try {
            inventorySlots = getConfig().getInt("inventory-slots");
            Validate.isTrue(inventorySlots > 0 && inventorySlots < 55, "Number must be greater than 9 and lower than 54");
            Validate.isTrue(inventorySlots % 9 == 0, "Number must be a multiple of 9");

        } catch (IllegalArgumentException exception) {
            inventorySlots = 36;
            getLogger().log(Level.WARNING, "Invalid inventory slot number: " + exception.getMessage());
        }
    }

    public VersionWrapper getVersionWrapper() {
        return versionWrapper;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public SlotManager getSlotManager() {
        return slotManager;
    }

    public ServerVersion getVersion() {
        return version;
    }

    public LevelModule getLevelModule() {
        return levelModule;
    }

    public ClassModule getClassModule() {
        return classModule;
    }

    public PlaceholderParser getPlaceholderParser() {
        return placeholderParser;
    }

    public void registerInventoryUpdater(InventoryUpdater updater) {
        Validate.notNull(updater, "Updater cannot be null");

        inventoryUpdaters.add(updater);
    }

    /**
     * Iterates through all registered {@link InventoryUpdater} and
     * updates the player for every plugin that needs it.
     *
     * @param player Player which inventory requires an update
     */
    public void updateInventory(Player player) {
        for (InventoryUpdater updater : inventoryUpdaters)
            updater.updateInventory(player);
    }

    public String getTranslation(String path) {
        return ChatColor.translateAlternateColorCodes('&', language.getConfig().getString(path));
    }

    private void saveDefaultFile(String path) {
        try {
            File file = new File(getDataFolder(), path + ".yml");
            if (!file.exists())
                Files.copy(getResource("default/" + path + ".yml"), file.getAbsoluteFile().toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
