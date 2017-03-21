package net.corecrafted.defuse;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;

public class DefuseCommand implements CommandExecutor {

	DefuseLaunch plugin;

	public DefuseCommand(DefuseLaunch pl) {
		plugin = pl;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(helpMsg());
		} else if (args.length == 1 && args[0].equalsIgnoreCase("give")) {
			if (!(sender.hasPermission("defuse.giveitem"))) {
				sender.sendMessage(getPrefix() + " " + getCmdNoPerm());
			} else {
				if (checkUseCustomItem() == false) {
					sender.sendMessage(getPrefix() + ChatColor.RED + " No item needed for deactivating TNT!");
				} else {
					if (sender instanceof Player) {
						Player player = (Player) sender;

						ItemStack item = getDefuseItem();

						PlayerInventory inv = player.getInventory();
						inv.addItem(item);
					} else {
						sender.sendMessage(getPrefix() + ChatColor.RED + " Please specific a player.");
					}
				}
			}
		} else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
			if (!(sender.hasPermission("defuse.giveitem"))) {
				sender.sendMessage(getPrefix() + " " + getCmdNoPerm());
			} else {
				if (checkUseCustomItem() == false) {
					sender.sendMessage(getPrefix() + ChatColor.RED + " No item needed for deactivating TNT!");
				} else {
					Player target = Bukkit.getPlayer(args[1]);
					if (target == null) {
						sender.sendMessage(
								getPrefix() + ChatColor.RED + " The player is either not online or not exist");
					} else {
						ItemStack item = getDefuseItem();

						PlayerInventory inv = target.getInventory();
						inv.addItem(item);
					}
				}
			}
		} else if (args.length == 1 && args[0].equalsIgnoreCase("useperm")) {
			if (!(sender.hasPermission("defuse.changeperm"))) {
				sender.sendMessage(getPrefix() + " " + getCmdNoPerm());
			} else {
				sender.sendMessage(ChatColor.GRAY + "-" + ChatColor.GOLD + " /defuse useperm [true/false]"
						+ ChatColor.WHITE + " - " + ChatColor.GRAY + "Toggle whether to use permission or not");
			}
		} else if (args.length == 2 && args[0].equalsIgnoreCase("useperm")) {
			if (!(sender.hasPermission("defuse.changeperm"))) {
				sender.sendMessage(getPrefix() + " " + getCmdNoPerm());
			} else {
				boolean usePerm = Boolean.parseBoolean(args[1]);
				plugin.getConfig().set("use-permission", usePerm);
				plugin.saveConfig();
				sender.sendMessage(
						getPrefix() + ChatColor.GREEN + " Use permission state updated to " + String.valueOf(usePerm));
			}
		} else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
			if (!(sender.hasPermission("defuse.reload"))) {
				sender.sendMessage(getPrefix() + " " + getCmdNoPerm());
			} else {
				plugin.reloadConfig();
				sender.sendMessage(getPrefix() + ChatColor.GREEN + " Config reloaded");
			}
		} else {
			sender.sendMessage(helpMsg());
		}
		return true;
	}

	private String helpMsg() {
		String msg = ChatColor.GRAY + "------------" + getPrefix() + "------------" + "\n" + ChatColor.GRAY + "-"
				+ ChatColor.GOLD + " /defuse give [player]" + ChatColor.WHITE + " - " + ChatColor.GRAY
				+ "Give the player a defuse item (if custom item enabled)" + "\n" + ChatColor.GRAY + "-"
				+ ChatColor.GOLD + " /defuse useperm [true/false]" + ChatColor.WHITE + " - " + ChatColor.GRAY
				+ "Toggle whether to use permission or not" + "\n" + ChatColor.GRAY + "-" + ChatColor.GOLD
				+ " /defuse reload" + ChatColor.WHITE + " - " + ChatColor.GRAY + "Reload config file";
		return msg;
	}

	private ItemStack getDefuseItem() {
		String itemType = plugin.getConfig().getString("item-id");
		String itemName = plugin.getConfig().getString("item-displayname");
		ItemStack defuseItem = new ItemStack(Material.getMaterial(itemType));
		if (itemName != "0") {
			ItemMeta defuseItemMeta = defuseItem.getItemMeta();
			defuseItemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemName));
			defuseItem.setItemMeta(defuseItemMeta);
		}
		return defuseItem;
	}

	public String getPrefix() {
		String prefix = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix"));
		return prefix;
	}

	private boolean checkUseCustomItem() {
		String check = plugin.getConfig().getString("use-custom-defuse-item");
		if (check == "true") {
			return true;
		} else {
			return false;
		}
	}

	private String getCmdNoPerm() {
		String cmdNoPerm = plugin.getConfig().getString("no-command-permission-message");
		String translatedCmdNoPerm = ChatColor.translateAlternateColorCodes('&', cmdNoPerm);
		return translatedCmdNoPerm;
	}
}
