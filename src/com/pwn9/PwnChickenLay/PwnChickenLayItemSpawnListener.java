package com.pwn9.PwnChickenLay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class PwnChickenLayItemSpawnListener implements Listener 
{
    private final PwnChickenLay plugin;
    
	public PwnChickenLayItemSpawnListener(PwnChickenLay plugin) 
	{
	    plugin.getServer().getPluginManager().registerEvents(this, plugin);    
	    this.plugin = plugin;
	}

	// List for the ItemSpawnEvent and then do stuff with it
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
			
			// check for biome specific settings in this world
			String ebiome = event.getLocation().getWorld().getBiome(event.getLocation().getBlockX(), event.getLocation().getBlockZ()).toString();
			
			if (plugin.getConfig().getBoolean("perWorld."+world+".perBiome."+ebiome+".enabled"))
			{
				
				if (PwnChickenLay.random(plugin.getConfig().getInt("perWorld."+world+".perBiome."+ebiome+".layChance")))
				{	
					
					List<String> repWith = new ArrayList<String>();
					
					for (String key : plugin.getConfig().getConfigurationSection("perWorld."+world+".perBiome."+ebiome+".replaceWith").getKeys(false))
					{
						Integer loop = plugin.getConfig().getInt("perWorld."+world+".perBiome."+ebiome+".replaceWith."+key, 1);
						for (int x = 0; x < loop; x = x+1)
						{
							repWith.add(key);
						}
					}
					
					// Pick an item from the replacement list randomly
					String randomReplacement = repWith.get(PwnChickenLay.randomNumberGenerator.nextInt(repWith.size()));     	
					
					// Cancel event and remove the egg
					event.getEntity().remove();
					event.setCancelled(true);
					
					// doReplacement func
					this.doReplacement(eworld, eLoc, randomReplacement);			
		
				}
				
			}
			
			// not biome specific so do world default
			else 
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
					
					// Cancel event and remove the egg
					event.getEntity().remove();
					event.setCancelled(true);
					
					// doReplacement func
					this.doReplacement(eworld, eLoc, randomReplacement);			
		
				}
				
			}
			
		}
		// use global default replacement configurations
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
    		
    		// Cancel event and remove the egg
			event.getEntity().remove();
			event.setCancelled(true);
			
			// doReplacement func
			this.doReplacement(eworld, eLoc, randomReplacement);	
			
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
	
	// routine to do replacement in world at location with random replacement
	public void doReplacement(World eworld, Location eLoc, String randomReplacement ) 
	{
		String world = eworld.getName();
		
		// log if debug_log is enabled
		if (PwnChickenLay.logEnabled)
		{		
			PwnChickenLay.logToFile("Attempting to lay: " + randomReplacement + " in world: " + world);
		}	
		
		// Check the randomReplacment string here to see if it is a standard material or a specialized item with enchants and lore
		if (randomReplacement.startsWith("_")) 
		{
			String specialReplacement = randomReplacement.substring(1);
			String specialType = plugin.getConfig().getString("special."+specialReplacement+".type");
			
			String getSpecialName;
			List<String> getSpecialLore;
			String getSpecialColor;
			Map<String, Object> getSpecialEnchants;
					
			// Setup item stack
			ItemStack getSpecial = new ItemStack(Material.getMaterial(specialType), 1);
			
			// Setup special enchant map
			Map<Enchantment, Integer> specialEnchants = new HashMap<Enchantment, Integer>();
			
			// Setup special config map
			if (plugin.getConfig().isSet("special."+specialReplacement+".enchants"))
			{
				getSpecialEnchants = plugin.getConfig().getConfigurationSection("special."+specialReplacement+".enchants").getValues(false);

				for (String key : getSpecialEnchants.keySet()) 
				{
					specialEnchants.put(Enchantment.getByName(key), (Integer) getSpecialEnchants.get(key));
				}
			}
			
			getSpecial.addEnchantments(specialEnchants);
			// Set lore and displayname item meta
			if(getSpecial.hasItemMeta()) 
			{

				// create item meta variable
				ItemMeta im = getSpecial.getItemMeta();
				
				// Name
				if (plugin.getConfig().isSet("special."+specialReplacement+".name"))
				{			
					getSpecialName = plugin.getConfig().getString("special."+specialReplacement+".name");
					
					// set item meta display name
					im.setDisplayName(PwnChickenLay.colorize(getSpecialName));					
				}
				
				// Lore
				if (plugin.getConfig().isSet("special."+specialReplacement+".lore"))
				{			
					getSpecialLore = plugin.getConfig().getStringList("special."+specialReplacement+".lore");

					// set item meta lore - colorize with list string
					im.setLore(PwnChickenLay.colorize(getSpecialLore));
				}
				
				// Colors (if leather)
				if (plugin.getConfig().isSet("special."+specialReplacement+".color"))
				{			
					getSpecialColor = plugin.getConfig().getString("special."+specialReplacement+".color");

					// is it leather?
					if((im instanceof LeatherArmorMeta)) 
					{
						((LeatherArmorMeta) im).setColor(Color.fromRGB(Integer.decode(getSpecialColor)));
					}
				}

				// put updated item meta on item
				getSpecial.setItemMeta(im);				
			}	
			
			eworld.dropItem(eLoc, getSpecial);	
		}
		
		// if not a special item, then is a standard drop 
		else if ((Material.getMaterial(randomReplacement) != Material.AIR) && (Material.getMaterial(randomReplacement) != null)) 
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
