package net.corecrafted.tntdefuse;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import net.corecrafted.tntdefuse.items.Defuser;
import net.corecrafted.tntdefuse.items.TNTHolder;
import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin {

	ConsoleCommandSender console = this.getServer().getConsoleSender();
	PluginDescriptionFile pdf = this.getDescription();
	Logger logger = this.getLogger();
	PluginManager pm = this.getServer().getPluginManager();
	private FileConfiguration itemsFile = null;
	private File itemsFileFile = null;

	public void onEnable() {
		iniEventHandlerItems();
		regConfig();
		regItemsFile();
		console.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("prefix")) + " "
				+ ChatColor.GREEN + "TNTDefuse v" + pdf.getVersion() + " has been loaded");
		this.getCommand("tntdefuse").setExecutor(new Commands(this));
	}

	public void onDisable() {
		console.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("prefix")) + " "
				+ ChatColor.AQUA + "TNTDefuse v" + pdf.getVersion() + " has been unloaded");
	}

	public FileConfiguration getItemsFile() {
		return itemsFile;
	}

	public void regItemsFile() {
		if (itemsFileFile == null) {
			itemsFileFile = new File(this.getDataFolder(), "items.yml");
		}
		if (!(itemsFileFile.exists())) {
			this.saveResource("items.yml", false);
		}
		FileConfiguration itemsFile = YamlConfiguration.loadConfiguration(itemsFileFile);
		this.itemsFile = itemsFile;
	}

	public void saveItemFile() {
		try {
			getItemsFile().save(itemsFileFile);
		} catch (IOException e) {
			logger.warning("Something is wrong and caused the items file cannot be saved");
			e.printStackTrace();
		}
	}

	public String getPrefix() {
		return ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("prefix"));
	}

	private void regConfig() {
		if (!(this.getDataFolder().exists())) {
			this.getDataFolder().mkdir();
		}
		this.saveDefaultConfig();
		logger.info("Config loaded");
	}

	private void iniEventHandlerItems() {
		pm.registerEvents(new Defuser(this), this);
		pm.registerEvents(new TNTHolder(this), this);
		// pm.registerEvents(new TNTHolder(), this);
	}

}
