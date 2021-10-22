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
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
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
    	new PwnChickenLayEggDrop(this);
    	
    	// Get Data Folder
    	PwnChickenLay.dataFolder = getDataFolder();
    	
    	// Load Config File
    	this.loadConfig();
    	
    	// Load plugin.yml
    	PwnChickenLay.pdfFile = this.getDescription(); //Gets plugin.yml
    	
    	// Command Executor
    	getCommand("pwnlay").setExecutor(new PwnChickenLayCommands(this));
    		
		// Start Metrics
		Metrics metricslite = new Metrics(this, 3261);
		
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
	
    // function to colorize strings from the config
    public static String colorize(String message)
    {
        return ChatColor.translateAlternateColorCodes('&', message);
    }    

    // function to colorize strings from the config
    public static List<String> colorize(List<String> message)
    {
        return message.stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList());
    }    
}