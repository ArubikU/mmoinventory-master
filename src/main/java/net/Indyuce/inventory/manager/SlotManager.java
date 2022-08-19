package net.Indyuce.inventory.manager;

import net.Indyuce.inventory.MMOInventory;
import net.Indyuce.inventory.slot.CustomSlot;
import net.Indyuce.inventory.slot.SlotType;
import net.Indyuce.inventory.slot.restriction.*;
import net.Indyuce.inventory.util.ConfigFile;
import net.Indyuce.inventory.util.LineConfig;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class SlotManager {

    /**
     * Custom slot instances saved using bukkit inventory slot index
     */
    private final Map<Integer, CustomSlot> slots = new HashMap<>();

    /**
     * Used to register extra slot restrictions from external plugins
     */
    private final Map<String, Function<LineConfig, SlotRestriction>> restrictionLoader = new HashMap<>();

    /**
     * Used to fill up inventory space. Fillers are not registered in the slot
     * map but instead cached in this field to both have easier access AND make
     * them not interfere with other systems as they aren't real slots
     */
    @Nullable
    private CustomSlot fill;

    private boolean registration = true;

    public SlotManager() {

        // Default slot restrictions
        registerRestriction(LevelRestriction::new, "level");
        registerRestriction(ClassRestriction::new, "class", "classes");
        registerRestriction(PermissionRestriction::new, "permission", "perm", "permissionnode", "permnode");
        registerRestriction(LoreTagRestriction::new, "lartype", "loretag", "customtag");
    }

    /**
     * Registers a custom slot that will appear in /rpginv. Filler items are not
     * registered in the map
     *
     * @param slot The slot to register
     */
    public void register(CustomSlot slot) {
        Validate.isTrue(!slots.containsKey(slot.getIndex()), "A slot with the same index already exists");

        // only register if not filler
        if (slot.getType() == SlotType.FILL)
            fill = slot;
        else
            slots.put(slot.getIndex(), slot);
    }

    /**
     * Matches a custom slot with a GUI slot index
     *
     * @param index The GUI slot index
     * @return The corresponding custom slot or null if it could not be found
     */
    public CustomSlot get(int index) {
        return slots.getOrDefault(index, null);
    }

    public Collection<CustomSlot> getLoaded() {
        return slots.values();
    }

    /**
     * Reads a slot restriction from a config file or throws an IAE if it could
     * not find any which matches
     *
     * @param config Life config to read from
     * @return Slot restriction if found
     */
    public SlotRestriction readRestriction(LineConfig config) {
        String id = config.getKey().toLowerCase().replace("-", "_").replace(" ", "_");

        for (String check : restrictionLoader.keySet())
            if (check.equals(id))
                return restrictionLoader.get(check).apply(config);

        throw new IllegalArgumentException("Could not find restriction with ID " + config.getKey());
    }

    /**
     * Registers a slot restriction in MMOInventory
     *
     * @param function A loader function which takes into input a line config and
     *                 outputs the right slot restriction instance
     * @param ids      The line config keys which will be assigned to this slot
     *                 restriction
     */
    public void registerRestriction(Function<LineConfig, SlotRestriction> function, String... ids) {
        Validate.isTrue(registration, "Slot restriction registration is disabled");
        Validate.notNull(function, "Function cannot not be null");

        for (String id : ids) {
            id = id.toLowerCase().replace("-", "_").replace(" ", "_");
            Validate.isTrue(!restrictionLoader.containsKey(id), "Database already contains a restriction with ID " + id);
            restrictionLoader.put(id, function);
        }
    }

    public boolean hasFiller() {
        return fill != null;
    }

    @NotNull
    public CustomSlot getFiller() {
        return Objects.requireNonNull(fill, "Filler is null");
    }

    public void reload() {
        if (registration)
            registration = false;
        else {
            fill = null;
            slots.clear();
        }

        FileConfiguration config = new ConfigFile("items").getConfig();
        for (String key : config.getKeys(false))
            try {
                ConfigurationSection section = config.getConfigurationSection(key);
                Validate.notNull(section, "Could not read config section");

                register(new CustomSlot(section));
            } catch (IllegalArgumentException exception) {
                MMOInventory.plugin.getLogger().log(Level.WARNING, "Could not load slot " + key + ": " + exception.getMessage());
            }

        MMOInventory.plugin.getLogger().log(Level.INFO, "Successfully registered " + slots.size() + " inventory slots.");
    }

    /**
     * @return A set of all the registered custom slots without filler items and
     *         vanilla item slots
     */
    public Set<CustomSlot> getCustomSlots() {
        return getLoaded().stream().filter(slot -> slot.getType() == SlotType.ACCESSORY).collect(Collectors.toSet());
    }
}
