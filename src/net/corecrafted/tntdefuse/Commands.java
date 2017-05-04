package net.corecrafted.tntdefuse;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import net.md_5.bungee.api.ChatColor;

public class Commands implements CommandExecutor {

	Main plugin;

	public Commands(Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			displayHelp(sender);
			return true;
		}
		if (args[0].equalsIgnoreCase("help")) {
			displayHelp(sender);
		} else if (args[0].equalsIgnoreCase("give")) {
			if (sender.hasPermission("defuse.item")){
				if (args.length == 1) {
					sender.sendMessage(plugin.getPrefix() + ChatColor.RED + " Hey, enter a playername!");
					return true;
				}
				if (args.length == 2) {
					sender.sendMessage(plugin.getPrefix() + ChatColor.RED + " So, what you want to give? [defuser/holder]");
					return true;
				}
				Player giveTarget = Bukkit.getPlayer(args[1]);
				if (giveTarget == null) {
					sender.sendMessage(plugin.getPrefix() + ChatColor.RED
							+ " The player you looking for does not exist / not online!");
					return true;
				}
				PlayerInventory inv = giveTarget.getInventory();
				if (args[2].equalsIgnoreCase("defuser")) {
					if (getDefuserItem() == null) {
						sender.sendMessage(
								plugin.getPrefix() + ChatColor.RED + " No defuser item defined at the moment (try "
										+ ChatColor.GOLD + "/tntdefuse setitem defuser " + ChatColor.RED
										+ "while holding the item you want to define as defuser item)");
						return true;
					} else {
						inv.addItem(getDefuserItem());
						sender.sendMessage(
								plugin.getPrefix() + ChatColor.GREEN + " Defuser item given to " + giveTarget.getName());
						return true;
					}
				} else if (args[2].equalsIgnoreCase("holder")) {
					if (getHolderItem() == null) {
						sender.sendMessage(
								plugin.getPrefix() + ChatColor.RED + " No TNT holder item defined at the moment (try "
										+ ChatColor.GOLD + "/tntdefuse setitem holder " + ChatColor.RED
										+ "while holding the item you want to define as TNT holder item)");
					} else {
						inv.addItem(getHolderItem());
						sender.sendMessage(plugin.getPrefix() + ChatColor.GREEN + " Explosives holder item given to "
								+ giveTarget.getName());
						return true;
					}

				} else {
					sender.sendMessage(plugin.getPrefix() + ChatColor.RED
							+ " It seemd like you try to define something else [defuser/holder]");
					return true;
				}
			} else {
				sender.sendMessage(getNoPermMsg());
				return true;
			}
			
		} else if (args[0].equalsIgnoreCase("reload")) {
			if (sender.hasPermission("defuse.reload")){
				plugin.regItemsFile();
				plugin.reloadConfig();
				plugin.saveDefaultConfig();
				sender.sendMessage(plugin.getPrefix() + ChatColor.GREEN + " Config file reloaded");
				return true;
			} else {
				sender.sendMessage(getNoPermMsg());
				return true;
			}
			
		} else if (args[0].equalsIgnoreCase("setitem")) {
			if (sender.hasPermission("defuse.item")){
				if (!(sender instanceof Player)) {
					sender.sendMessage(plugin.getPrefix() + ChatColor.RED + " You have to be a player to do this");
					return true;
				}
				if (args.length == 1) {
					sender.sendMessage(plugin.getPrefix() + ChatColor.RED + " What you want to define? [defuser/holder]");
					return true;
				}

				Player player = (Player) sender;
				ItemStack definingItem = player.getInventory().getItemInMainHand();

				if (args[1].equalsIgnoreCase("defuser")) {
					if (definingItem.getType().equals(Material.AIR)) {
						player.sendMessage(plugin.getPrefix() + ChatColor.RED
								+ " Hmm..... I think it is not a good idea to define air as defuser (Try holding something in your hand main hand first)");
						return true;
					} else {
						setDefuserItem(definingItem);
						player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + " Defuser item set");
						player.sendMessage(plugin.getPrefix() + ChatColor.GRAY + " Please consider reloading the config file");
						if (plugin.getConfig().getBoolean("use-defuse-item") == false) {
							player.sendMessage(plugin.getPrefix() + ChatColor.DARK_GREEN
									+ " Please set \"use-defuse-item\" to true in order to limit defuse with item you just defined");
						}
						return true;
					}
				} else if (args[1].equalsIgnoreCase("holder")) {
					if (definingItem.getType().equals(Material.AIR)) {
						player.sendMessage(plugin.getPrefix() + ChatColor.RED
								+ " Well.... I suppose it does not make any sense to define air as TNT holder! (try holding something in your main hand first)");
						return true;
					} else {
						if (definingItem.getItemMeta().hasLore()){
							player.sendMessage(plugin.getPrefix()+ChatColor.RED+" Item with lores is discouraged (try removing the lore before defining)");
							return true;
						}
						setHolderItem(definingItem);
						player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + " Explosives holder item set");
						player.sendMessage(plugin.getPrefix() + ChatColor.GRAY + " Please consider reloading the config file");
						return true;
					}
				} else {
					player.sendMessage(plugin.getPrefix() + ChatColor.RED + " There is no such thing called "
							+ ChatColor.GOLD + args[1] + ChatColor.RED + " [defuser/holder]");
				} 
			} else {
				sender.sendMessage(getNoPermMsg());
				return true;
			}
			
		} else if (args[0].equalsIgnoreCase("debug")) {
			if (sender.hasPermission("defuse.debug")){
				Player player = (Player) sender;
				sender.sendMessage(player.getInventory().getItemInMainHand().toString());
				sender.sendMessage(sender.toString());
				String temp = "500";
				plugin.logger.info("Debug: "+Integer.parseInt(temp));
			} else {
				sender.sendMessage(getNoPermMsg());
				return true;
			}
			
		} else {
			sender.sendMessage(plugin.getPrefix() + ChatColor.RED + " I don't know what you mean by" + ChatColor.GOLD
					+ " " + args[0]);
			displayHelp(sender);
		}
		return true;
	}

	private void displayHelp(CommandSender sender) {
		sender.sendMessage(ChatColor.GRAY + "-----------" + plugin.getPrefix() + ChatColor.GRAY + "-----------");
		sender.sendMessage(ChatColor.GOLD + " /tntdefuse help" + ChatColor.WHITE + " - " + ChatColor.GRAY
				+ "Display this command help menu");
		sender.sendMessage(ChatColor.GOLD + " /tntdefuse give [player] [defuser/holder]" + ChatColor.WHITE + " - "
				+ ChatColor.GRAY + "Give the player a defuse item (if custom item enabled)");
		sender.sendMessage(ChatColor.GOLD + " /tntdefuse setitem [defuser/holder]" + ChatColor.WHITE + " - "
				+ ChatColor.GRAY + "Define / update the defuser and holder item");
		sender.sendMessage(ChatColor.GOLD + " /tntdefuse reload" + ChatColor.WHITE + " - " + ChatColor.GRAY
				+ "Reload config file");
	}

	private void setDefuserItem(ItemStack item) {
		plugin.getItemsFile().set("defuser", item);
		plugin.saveItemFile();
	}

	private ItemStack getDefuserItem() {
		return plugin.getItemsFile().getItemStack("defuser");
	}

	private void setHolderItem(ItemStack item) {
		plugin.getItemsFile().set("holder", item);
		plugin.saveItemFile();
	}

	private ItemStack getHolderItem() {
		return plugin.getItemsFile().getItemStack("holder");
	}
	
	private String getNoPermMsg(){
		return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("no-permission-message"));
	}
}
