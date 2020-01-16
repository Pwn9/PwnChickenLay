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
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class PwnChickenLayEggDrop implements Listener 
{
    private final PwnChickenLay plugin;
    
	public PwnChickenLayEggDrop(PwnChickenLay plugin) 
	{
	    plugin.getServer().getPluginManager().registerEvents(this, plugin);    
	    this.plugin = plugin;
	}

	// List for the item drop and then do stuff with it
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDrop(EntityDropItemEvent event) 
	{
		
		World eworld = event.getEntity().getWorld();
		Location eLoc = event.getEntity().getLocation();
		Item i = event.getItemDrop();
		
		// If plugin is not enabled in this world, return
		if (!PwnChickenLay.isEnabledIn(eworld.getName())) return; 
		
		// If item is not an egg, return
		if ((i.getItemStack() == null) || (i.getItemStack().getType()) == null || (i.getItemStack().getType() != Material.EGG)) return;
			
		EntityType e  = event.getEntity().getType();
		// if not a chicken, return
		if (e != EntityType.CHICKEN) return;
		
		String world = eworld.getName();
		
		// should be safe to cast as chicken now
		Chicken chic = (Chicken) event.getEntity();
		
		if (chic.getCustomName() != null) {
			PwnChickenLay.logToFile("Egg spawn event was from custom chicken: " + chic.getCustomName());

			// if there is a config for this custom name
			if (plugin.getConfig().getBoolean("custom."+chic.getCustomName()+".enabled")) 
			{
				PwnChickenLay.logToFile("Config is set for custom chicken: " + chic.getCustomName());
				
				if (PwnChickenLay.random(plugin.getConfig().getInt("custom."+chic.getCustomName()+".layChance")))
				{	
					
					List<String> repWith = new ArrayList<String>();
					
					for (String key : plugin.getConfig().getConfigurationSection("custom."+chic.getCustomName()+".replaceWith").getKeys(false))
					{
						Integer loop = plugin.getConfig().getInt("custom."+chic.getCustomName()+".replaceWith."+key, 1);
						for (int x = 0; x < loop; x = x+1)
						{
							repWith.add(key);
						}
					}
					
					// Pick an item from the replacement list randomly
					String randomReplacement = repWith.get(PwnChickenLay.randomNumberGenerator.nextInt(repWith.size()));     	
					
					// Cancel event and remove the egg
					event.getItemDrop().remove();
					event.setCancelled(true);
					
					// doReplacement func
					this.doReplacement(eworld, eLoc, randomReplacement);			
		
					return;
				}
			}
			
		}
		
		// check per world settings
		if (plugin.getConfig().getBoolean("perWorld."+world+".enabled")) 
		{
			
			// check for biome specific settings in this world
			String ebiome = eLoc.getBlock().getBiome().toString();
			
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
					event.getItemDrop().remove();
					event.setCancelled(true);
					
					// doReplacement func
					this.doReplacement(eworld, eLoc, randomReplacement);
					
					return;
		
				}
				else 
				{	
					PwnChickenLay.logToFile("Chicken laid: Default egg, in world: " + world + " Location: " + eLoc.getBlockX() + ", " + eLoc.getBlockY() + ", " + eLoc.getBlockZ());	
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
					event.getItemDrop().remove();
					event.setCancelled(true);
					
					// doReplacement func
					this.doReplacement(eworld, eLoc, randomReplacement);
					
					return;
		
				}
				else 
				{	
					PwnChickenLay.logToFile("Chicken laid: Default egg, in world: " + world + " Location: " + eLoc.getBlockX() + ", " + eLoc.getBlockY() + ", " + eLoc.getBlockZ());	
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
    		event.getItemDrop().remove();
			event.setCancelled(true);
			
			// doReplacement func
			this.doReplacement(eworld, eLoc, randomReplacement);	
			
			return;
			
		}
		else 
		{
			// log if debug_log is enabled
			if (PwnChickenLay.logEnabled)
			{		
				PwnChickenLay.logToFile("Chicken laid: Default egg, in world: " + world + " Location: " + eLoc.getBlockX() + ", " + eLoc.getBlockY() + ", " + eLoc.getBlockZ());
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
					//the above is deprecated, this is supposed to work but doesnt - specialEnchants.put(Enchantment.getByKey(NamespacedKey.minecraft(key)), (Integer) getSpecialEnchants.get(key));
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
		
		// interface to spawn an entity rather than a material
		//TODO: add enum from config check
		else if (randomReplacement.startsWith("e_")) 
		{	
			EntityType e = EntityType.valueOf(randomReplacement.substring(2));
			// so this is a bit hacky, still I want primed_tnt
			try 
			{
				eworld.spawnEntity(eLoc, e);
			}
			catch (IllegalArgumentException err) 
			{
				if (PwnChickenLay.logEnabled)
				{		
					PwnChickenLay.logToFile("Tried to lay invalid entity: " + randomReplacement.substring(2) + " - check your config for invalid values.");
				}					
			}
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
			PwnChickenLay.logToFile("Chicken laid: " + randomReplacement + " in world: " + world + " Location: " + eLoc.getBlockX() + ", " + eLoc.getBlockY() + ", " + eLoc.getBlockZ());
		}	
		
	}
}
