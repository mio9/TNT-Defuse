package net.corecrafted.defuse;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class DefuseLaunch extends JavaPlugin{

	ConsoleCommandSender console = this.getServer().getConsoleSender();
	PluginDescriptionFile pdf = this.getDescription();
	
	public void onEnable(){
		console.sendMessage(ChatColor.GREEN+"Defuse v-"+pdf.getVersion()+" has been enabled");
		this.getServer().getPluginManager().registerEvents(new Defuse(), this);
	}
	
	public void onDisable(){
		console.sendMessage(ChatColor.AQUA+"Defuse v-"+pdf.getVersion()+" has been unloaded");
	}
}
