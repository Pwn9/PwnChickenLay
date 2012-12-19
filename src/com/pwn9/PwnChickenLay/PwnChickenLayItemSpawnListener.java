package com.pwn9.PwnChickenLay;

import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;

public class PwnChickenLayItemSpawnListener implements Listener {

	//private PwnChickenLay plugin;
	
	//public PwnChickenLayItemSpawnListener(PwnChickenLay instance) {
	//	plugin = instance;
	//}
	
	@EventHandler
	public void onSpawn(ItemSpawnEvent event) {
		if(event.isCancelled()) {
			return;
		}
		
		Item test = (Item) event.getEntity();
		
		ItemStack is = test.getItemStack();
		if(is.getType() != Material.EGG) {
			return;
		}
		
		List<Entity> nearby = test.getNearbyEntities(0.01, 0.3, 0.01);
		
		for(int i = 0; i < nearby.size(); i++) {
			// Player: org.bukkit.craftbukkit.entity.CraftPlayer
			if(nearby.get(i).getClass().getName() == "org.bukkit.craftbukkit.entity.CraftPlayer") {
				return;
			}
		}
		
		Random random = new Random();
		int randomInt = random.nextInt(100);
		//if (randomInt > Integer.parseInt(plugin.layChance)) {
		if (randomInt > 10) {		
			event.setCancelled(true);
		}
	}	
}