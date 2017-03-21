package net.corecrafted.defuse;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class DefuseLaunch extends JavaPlugin {

	ConsoleCommandSender console = this.getServer().getConsoleSender();
	PluginDescriptionFile pdf = this.getDescription();
	Logger logger = this.getLogger();

	public void onEnable() {
		console.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("prefix"))+ChatColor.GREEN + " Defuse v" + pdf.getVersion() + " has been enabled");
		regConfig();
		regEvent();
		this.getCommand("defuse").setExecutor(new DefuseCommand(this));
		
	}

	public void onDisable() {
		console.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("prefix"))+ChatColor.AQUA + " Defuse v" + pdf.getVersion() + " has been unloaded");
	}

	private void regConfig() {

		try {
			if (!(this.getDataFolder().exists())) {
				this.getDataFolder().mkdirs();
			}

			File file = new File(this.getDataFolder(), "config.yml");
			if (!(file.exists())) {
				logger.info("Config file not exists, creating one for you......");
				this.saveDefaultConfig();
			} else {
				logger.info("Loading config....");
			}
		} catch (Exception excep) {
			excep.printStackTrace();
		} finally {
			logger.info("Config loaded");
		}
	}
	
	private void regEvent(){
		logger.info("This server is running in "+Bukkit.getVersion());
		if (Bukkit.getVersion().contains("1.8")){
			logger.info("1.8 detected , using legacy method..");
			this.getServer().getPluginManager().registerEvents(new DefuseLegacy(this), this);
		} else {
			this.getServer().getPluginManager().registerEvents(new Defuse(this), this);
		}
		
	}

}
