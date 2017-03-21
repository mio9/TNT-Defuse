package net.corecrafted.defuse;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;

public class Defuse implements Listener {


	private DefuseLaunch plugin;

	public Defuse(DefuseLaunch pl) {
		plugin = pl;
	}
	
	@EventHandler
	public void onDefuse(PlayerInteractEntityEvent e) {
		
		if (e.getRightClicked() instanceof TNTPrimed) {
			if (e.getHand() == EquipmentSlot.OFF_HAND) {
				return;
			}

			if (plugin.getConfig().getBoolean("use-custom-defuse-item") == true) {
				try {
					getDefuseItemType();
				} catch (NullPointerException exception) {
					plugin.logger.warning(ChatColor.RED+plugin.getConfig().getString("item-id")+" is not a proper item");
					runRemoveTnt(e);
				} finally {
					ItemStack item = getDefuseItemType();
					ItemMeta itemMeta = item.getItemMeta();
					String itemDisplayName = plugin.getConfig().getString("item-displayname");
					itemDisplayName = ChatColor.translateAlternateColorCodes('&', itemDisplayName);
					itemMeta.setDisplayName(itemDisplayName);
					item.setItemMeta(itemMeta);

					if (e.getPlayer().getInventory().getItemInMainHand().equals(item)) {
						runRemoveTnt(e);
					} else {
						return;
					}
				}
			} else {
				runRemoveTnt(e);
			}
		}
	}

	private void removeTnt(PlayerInteractEntityEvent event) {
		Entity tnt = event.getRightClicked();
		int x = tnt.getLocation().getBlockX();
		int y = tnt.getLocation().getBlockY();
		int z = tnt.getLocation().getBlockZ();
		tnt.remove();
		Location setBlock = new Location(tnt.getWorld(), x, y, z);
		ArrayList<Entity> craps = (ArrayList<Entity>) setBlock.getWorld().getNearbyEntities(setBlock, 0.5, 0.5, 0.5);

		for (Entity rubbish : craps) {
			if (rubbish instanceof TNTPrimed) {
				rubbish.remove();
			}
		}

		setBlock.getBlock().setType(Material.TNT);
	}

	private ItemStack getDefuseItemType() {
		String itemtype = plugin.getConfig().getString("item-id");
		ItemStack defuseItem = new ItemStack(Material.getMaterial(itemtype));
		return defuseItem;
	}

	private boolean checkPermission(Player p) {

		if (plugin.getConfig().getBoolean("use-permission") == true) {
			if (p.hasPermission("defuse.deactivate")) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	private void runRemoveTnt(PlayerInteractEntityEvent e) {
		boolean isPermitted = checkPermission(e.getPlayer());

		if (isPermitted == true) {
			removeTnt(e);
		} else {
			e.getPlayer().sendMessage(getNoPermMsg());
			return;
		}
	}
	
	private String getNoPermMsg(){
		String msg = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix")+" "+plugin.getConfig().getString("no-permission-message"));
		return msg;
	}
}
