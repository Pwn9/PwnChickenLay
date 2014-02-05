package com.pwn9.PwnChickenLay;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;

public class PwnChickenLayItemSpawnListener implements Listener 
{
    private final PwnChickenLay plugin;
    
	public PwnChickenLayItemSpawnListener(PwnChickenLay plugin) 
	{
	    plugin.getServer().getPluginManager().registerEvents(this, plugin);    
	    this.plugin = plugin;
	}

	// List for the ItemSpawnEvent and then kick it to the spawnCheck function 
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSpawn(ItemSpawnEvent event) 
	{
		World eworld = event.getLocation().getWorld();
		Location eLoc = event.getLocation();
		// If plugin is not enabled in this world, return
		if (!PwnChickenLay.isEnabledIn(eworld.getName())) return; 

		Item test = (Item) event.getEntity();
		ItemStack is = test.getItemStack();
		
		// if item is anything other than an egg, return
		if(is.getType() != Material.EGG) return;

		List<Entity> nearby = test.getNearbyEntities(0.01, 0.3, 0.01);
		
		// check if a player entity is at the same location as the ItemSpawnEvent - if so, probably not a chicken lay and return
		for(int i = 0; i < nearby.size(); i++) 
		{
			if (nearby.get(i) instanceof Player) return;
		}
		
		String world = eworld.getName();
		
		// check per world settings
		if (plugin.getConfig().getBoolean("perWorld."+world+".enabled")) 
		{
			if (PwnChickenLay.random(plugin.getConfig().getInt("perWorld."+world+".layChance")))
			{	
				
				List<String> repWith = new ArrayList<String>();
				
				for (String key : plugin.getConfig().getConfigurationSection("perWorld."+world+".replaceWith").getKeys(false))
				{
					Integer loop = plugin.getConfig().getInt("perWorld."+world+".replaceWith."+key, 1);
					for (int x = 0; x < loop; x = x+1)
					{
						repWith.add(key);
					}
				}
				
				// Pick an item from the replacement list randomly
				String randomReplacement = repWith.get(PwnChickenLay.randomNumberGenerator.nextInt(repWith.size()));     	
				
				event.getEntity().remove();
				event.setCancelled(true);
				
				if (Material.getMaterial(randomReplacement) != Material.AIR) 
				{
					ItemStack drop = new ItemStack(Material.getMaterial(randomReplacement), 1);
					eworld.dropItem(eLoc, drop);
				}
				
	    		// log if debug_log is enabled
				if (PwnChickenLay.logEnabled)
				{		
					PwnChickenLay.logToFile("Chicken laid: " + randomReplacement + " in world: " + world);
				}
				
			}
		}
		else if (PwnChickenLay.random(PwnChickenLay.layChance)) 
		{	

			List<String> repWith = new ArrayList<String>();
			
			for (String key : plugin.getConfig().getConfigurationSection("replaceWith").getKeys(false))
			{
				Integer loop = plugin.getConfig().getInt("replaceWith."+key, 1);
				for (int x = 0; x < loop; x = x+1)
				{
					repWith.add(key);
				}
			}
			
			// Pick an item from the replacement list randomly
    		String randomReplacement = repWith.get(PwnChickenLay.randomNumberGenerator.nextInt(repWith.size()));   
    		
			event.getEntity().remove();
			event.setCancelled(true);
			
			if (Material.getMaterial(randomReplacement) != Material.AIR) 
			{
				ItemStack drop = new ItemStack(Material.getMaterial(randomReplacement), 1);
				eworld.dropItem(eLoc, drop);
			}
			
    		// log if debug_log is enabled
			if (PwnChickenLay.logEnabled)
			{		
				PwnChickenLay.logToFile("Chicken laid: " + randomReplacement + " in world: " + world);
			}	
			
		}
		else 
		{
			// log if debug_log is enabled
			if (PwnChickenLay.logEnabled)
			{		
				PwnChickenLay.logToFile("Chicken laid: Default egg, in world: " + world);
			}					
		}
		
	}  
}
