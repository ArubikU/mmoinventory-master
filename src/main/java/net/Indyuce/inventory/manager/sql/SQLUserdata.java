package net.Indyuce.inventory.manager.sql;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.inventory.ItemStack;

public class SQLUserdata {
	private final Map<Integer, ItemStack> inventory = new HashMap<>();
	
	public Set<Entry<Integer, ItemStack>> get() {
		return inventory.entrySet();
	}

	public void put(int index, ItemStack stack) {		
		inventory.put(index, stack);
	}
}
