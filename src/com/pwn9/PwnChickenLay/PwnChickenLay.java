package com.pwn9.PwnChickenLay;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class PwnChickenLay extends JavaPlugin 
{
	// Init vars
	public static File dataFolder;
	public final Logger logger = Logger.getLogger("Minecraft.PwnChickenLay");   	
	public static List<String> enabledWorlds;
	public static List<String> replaceWith;
	public static int layChance;
	public static Boolean logEnabled;
	static Random randomNumberGenerator = new Random();
	public static PluginDescriptionFile pdfFile;
	
	@Override
	public void onEnable() 
	{
    	this.saveDefaultConfig();
    	
    	// Init Listener
    	new PwnChickenLayItemSpawnListener(this);
    	
    	// Get Data Folder
    	PwnChickenLay.dataFolder = getDataFolder();
    	
    	// Load Config File
    	this.loadConfig();
    	
    	// Load plugin.yml
    	PwnChickenLay.pdfFile = this.getDescription(); //Gets plugin.yml
    	
    	// Command Executor
    	getCommand("pwnlay").setExecutor(new PwnChickenLayCommands(this));
    		
		// Start Metrics
		try 
		{
		    MetricsLite metricslite = new MetricsLite(this);
		    metricslite.start();
		} 
		catch (IOException e) 
		{
		    // Failed to submit the stats :-(
		}  
		
		if (PwnChickenLay.logEnabled)
		{
			PwnChickenLay.logToFile(PwnChickenLay.pdfFile.getName() + " version " + PwnChickenLay.pdfFile.getVersion() + " [enabled]");
		}	
	}
	
	public void onDisable() 
	{
		if (PwnChickenLay.logEnabled)
		{		
			PwnChickenLay.logToFile(PwnChickenLay.pdfFile.getName() + " version " + PwnChickenLay.pdfFile.getVersion() + " [disabled]");
		}	
	}	
	
	// Main function to receive event data and determine if it is a chicken laying the egg, then do stuff...
	public void spawnCheck(ItemSpawnEvent event, String world) 
	{
		// if event is already cancelled by another just pass it on
		if(event.isCancelled()) 
		{
			return;
		}
		
		Item test = (Item) event.getEntity();
		
		ItemStack is = test.getItemStack();
		
		// if item is anything other than an egg, return
		if(is.getType() != Material.EGG) 
		{
			return;
		}
		
		List<Entity> nearby = test.getNearbyEntities(0.01, 0.3, 0.01);
		
		// check if a player entity is at the same location as the ItemSpawnEvent - if so, probably not a chicken lay and return
		for(int i = 0; i < nearby.size(); i++) 
		{
			if (nearby.get(i) instanceof Player)
			{
				return;
			}
		}
		
		// check per world settings
		if (getConfig().getBoolean("perWorld."+world+".enabled")) 
		{
			if (PwnChickenLay.random(getConfig().getInt("perWorld."+world+".layChance")))
			{	
				List<String> repWith = getConfig().getStringList("perWorld."+world+".replaceWith");		
	    		String randomReplacement = repWith.get(randomNumberGenerator.nextInt(repWith.size()));     	
				//event.setCancelled(true);
				is.setType(Material.getMaterial(randomReplacement));
				
				// log if debug_log is enabled
				if (PwnChickenLay.logEnabled)
				{		
					PwnChickenLay.logToFile("Chicken laid: " + randomReplacement + " in world: " + world);
				}
			}			
		}
		else if (PwnChickenLay.random(layChance)) 
		{	
    		String randomReplacement = replaceWith.get(randomNumberGenerator.nextInt(replaceWith.size()));     	
			//event.setCancelled(true);
			is.setType(Material.getMaterial(randomReplacement));
			
			// log if debug_log is enabled
			if (PwnChickenLay.logEnabled)
			{		
				PwnChickenLay.logToFile("Chicken laid: " + randomReplacement + " in world: " + world);
			}			
		}
	}	
	
	// Generate a random number and return bool
	static boolean random(int percentChance) 
	{
		return randomNumberGenerator.nextInt(100) > percentChance;
	}
	
	// Check enabled worlds list and return bool
	public static boolean isEnabledIn(String world) 
	{
		return enabledWorlds.contains(world);
	}	
	
	// Load all of our config file
	public void loadConfig() 
	{
		PwnChickenLay.enabledWorlds = getConfig().getStringList("enabled_worlds");
		PwnChickenLay.layChance = getConfig().getInt("layChance");
		PwnChickenLay.replaceWith = getConfig().getStringList("replaceWith");		
		PwnChickenLay.logEnabled = getConfig().getBoolean("debug_log");
	}	
	
	// Debug logging
    public static void logToFile(String message) 
    {   
	    	try 
	    	{		    
			    if(!dataFolder.exists()) 
			    {
			    	dataFolder.mkdir();
			    }
			     
			    File saveTo = new File(dataFolder, "pwnchickenlay.log");
			    if (!saveTo.exists())  
			    {
			    	saveTo.createNewFile();
			    }
			    
			    FileWriter fw = new FileWriter(saveTo, true);
			    PrintWriter pw = new PrintWriter(fw);
			    pw.println(getDate() +" "+ message);
			    pw.flush();
			    pw.close();
		    } 
		    catch (IOException e) 
		    {
		    	e.printStackTrace();
		    }
    }
    
    public static String getDate() 
    {
    	  String s;
    	  Format formatter;
    	  Date date = new Date(); 
    	  formatter = new SimpleDateFormat("[yyyy/MM/dd HH:mm:ss]");
    	  s = formatter.format(date);
    	  return s;
    }		
	
}