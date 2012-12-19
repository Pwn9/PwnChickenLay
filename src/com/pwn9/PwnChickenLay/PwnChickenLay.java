package com.pwn9.PwnChickenLay;

import java.util.logging.Logger;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PwnChickenLay extends JavaPlugin {
	
	private PluginManager pm;
	private Logger log = Logger.getLogger("Minecraft");
	private PwnChickenLayItemSpawnListener itemSpawnListener = new PwnChickenLayItemSpawnListener();
	//private PwnChickenLayItemSpawnListener itemSpawnListener;
	
	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		this.pm = this.getServer().getPluginManager();
		this.regListeners();
		this.logMsg("Loaded.");
	}
	
	private void regListeners() {
		pm.registerEvents(this.itemSpawnListener, this);
	}
	
	private void logMsg(String msg) {
		PluginDescriptionFile pdf = this.getDescription();
		this.log.info(pdf.getName() + " " + pdf.getVersion() + ": " + msg);
	}
	
	@Override
	public void onDisable() {
		this.logMsg("Unloaded.");	
	}	
	
	//public String layChance = PwnChickenLay.this.getConfig().getString("laychance");
		
}