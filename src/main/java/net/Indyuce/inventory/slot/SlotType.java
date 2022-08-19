package net.Indyuce.inventory.slot;

import net.Indyuce.inventory.MMOInventory;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public enum SlotType {

	/**
	 * The vanilla helmet slot
	 */
	HELMET(new VanillaSlotHandler() {

		@Override
		public void equip(Player player, ItemStack item) {
			player.getInventory().setHelmet(item);
		}

		@Override
		public boolean canEquip(ItemStack item) {
			return MMOInventory.plugin.getVersionWrapper().isHelmet(item.getType());
		}

		@Override
		public ItemStack retrieveItem(Player player) {
			return player.getInventory().getHelmet();
		}

		@Override
		public boolean supportsShiftClick() {
			return true;
		}
	}),

	/**
	 * The vanilla chestplate slot
	 */
	CHESTPLATE(new VanillaSlotHandler() {

		@Override
		public void equip(Player player, ItemStack item) {
			player.getInventory().setChestplate(item);
		}

		@Override
		public boolean canEquip(ItemStack item) {
			return item.getType().name().endsWith("_CHESTPLATE") || item.getType() == Material.ELYTRA;
		}

		@Override
		public ItemStack retrieveItem(Player player) {
			return player.getInventory().getChestplate();
		}

		@Override
		public boolean supportsShiftClick() {
			return true;
		}
	}),

	/**
	 * The vanilla leggings slot
	 */
	LEGGINGS(new VanillaSlotHandler() {

		@Override
		public void equip(Player player, ItemStack item) {
			player.getInventory().setLeggings(item);
		}

		@Override
		public boolean canEquip(ItemStack item) {
			return item != null && item.getType().name().endsWith("_LEGGINGS");
		}

		@Override
		public ItemStack retrieveItem(Player player) {
			return player.getInventory().getLeggings();
		}

		@Override
		public boolean supportsShiftClick() {
			return true;
		}
	}),

	/**
	 * The vanilla boots slot
	 */
	BOOTS(new VanillaSlotHandler() {

		@Override
		public void equip(Player player, ItemStack item) {
			player.getInventory().setBoots(item);
		}

		@Override
		public boolean canEquip(ItemStack item) {
			return item != null && item.getType().name().endsWith("BOOTS");
		}

		@Override
		public ItemStack retrieveItem(Player player) {
			return player.getInventory().getBoots();
		}

		@Override
		public boolean supportsShiftClick() {
			return true;
		}
	}),

	/**
	 * The vanilla off hand slot
	 */
	OFF_HAND(new VanillaSlotHandler() {

		@Override
		public void equip(Player player, ItemStack item) {
			player.getInventory().setItemInOffHand(item);
		}

		@Override
		public boolean canEquip(ItemStack item) {
			return true;
		}

		@Override
		public ItemStack retrieveItem(Player player) {
			return player.getInventory().getItemInOffHand();
		}

		@Override
		public boolean supportsShiftClick() {
			return false;
		}
	}),
	MAIN_HAND(new VanillaSlotHandler() {

		@Override
		public void equip(Player player, ItemStack item) {
			player.getInventory().setItemInMainHand(item);
		}

		@Override
		public boolean canEquip(ItemStack item) {
			return true;
		}

		@Override
		public ItemStack retrieveItem(Player player) {
			return player.getInventory().getItemInMainHand();
		}

		@Override
		public boolean supportsShiftClick() {
			return false;
		}
	}),

	/**
	 * Slot type which must be used when registering custom accessory/RPG slots.
	 */
	ACCESSORY(null),
	COMMAND(null),
	TILE(null),
	HEAD(null),

	/**
	 * Slot type used for filler items in the GUI
	 */
	FILL(null);

	private final VanillaSlotHandler vanilla;

	private SlotType(VanillaSlotHandler vanilla) {
		this.vanilla = vanilla;
	}

	/**
	 * @return If the custom slot is NOT a vanilla slot
	 */
	public boolean isCustom() {
		Validate.isTrue(this != FILL, "FILL is not an item slot");

		return (this == ACCESSORY || this == COMMAND || this == TILE || this == HEAD);
	}

	public VanillaSlotHandler getVanillaSlotHandler() {
		return vanilla;
	}
}
