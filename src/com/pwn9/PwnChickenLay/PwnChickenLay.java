package com.pwn9.PwnChickenLay;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class PwnChickenLay extends JavaPlugin {
	
	public final Logger logger = Logger.getLogger("Minecraft.PwnChickenLay");   	

	public void onEnable() {
    	this.saveDefaultConfig();
    	new PwnChickenLayItemSpawnListener(this);
	}
	
	public void onDisable() {

	}	
	
	public void spawnCheck(ItemSpawnEvent event) {
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
			if (nearby.get(i) instanceof Player) {
			return;
			}
		}
		
		Random random = new Random();
		int randomInt = random.nextInt(100);
    	String layChance = getConfig().getString("layChance");
		if (randomInt > Integer.parseInt(layChance)) {	
			event.setCancelled(true);
		}
	}	
}