package com.pwn9.PwnChickenLay;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class PwnChickenLayCommands implements CommandExecutor 
{
	private PwnChickenLay plugin;
	
	public PwnChickenLayCommands(PwnChickenLay plugin) 
	{  
	   this.plugin = plugin;
	}	

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{		
		if (!(sender instanceof Player) && !(sender instanceof ConsoleCommandSender))
		{
			sender.sendMessage("§cThis command can only be run by a player.");
		}
		else
		{
			if (cmd.getName().equalsIgnoreCase("pwnlay"))
			{
				pwnlay(sender, cmd, label, args);
				return true;
			}
		}
		return false;
   }	
   
	public void pwnlay(CommandSender sender, Command cmd, String label, String[] args)
	{	
		if(args.length > 0) 
		{
           if(args[0].equalsIgnoreCase("reload")) 
           {
              plugin.reloadConfig();
              plugin.loadConfig();
              sender.sendMessage("§cPwnChickenLay: Settings reloaded!");
           } 
           else if(args[0].equalsIgnoreCase("save")) 
           {
              plugin.saveConfig();
              plugin.loadConfig();
              sender.sendMessage("§cPwnChickenLay: Config saved!");
           }
           else 
           {
        	   sender.sendMessage("§cPwnChickenLay Usage: /pwnlay <reload|save>"); 
           }
        	   
		}
		else 
		{
    	   sender.sendMessage("§cUsage: pwnlay <reload|save>");     
	    }
	}
	
 
}
