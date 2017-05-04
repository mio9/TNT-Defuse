package net.corecrafted.tntdefuse.items;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import net.corecrafted.tntdefuse.Main;

public class Defuser implements Listener {

	Main plugin;

	public Defuser(Main plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onDefuse(PlayerInteractEntityEvent e) {
		if (e.getRightClicked() instanceof TNTPrimed) {
			if (e.getPlayer().hasPermission("defuse.deactivate")) {
				if (plugin.getConfig().getBoolean("use-defuse-item") == true) {
					ItemStack defuser = plugin.getItemsFile().getItemStack("defuser");
					if (e.getPlayer().getInventory().getItemInMainHand().equals(defuser)) {
						removeTnt(e);
					} else {
						return;
					}
				} else {
					removeTnt(e);
				}
			} else {
				return;
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
		ArrayList<Entity> craps = (ArrayList<Entity>) setBlock.getWorld().getNearbyEntities(setBlock,
				plugin.getConfig().getDouble("clear-range"), plugin.getConfig().getDouble("clear-range"),
				plugin.getConfig().getDouble("clear-range"));

		for (Entity rubbish : craps) {
			if (rubbish instanceof TNTPrimed) {
				rubbish.remove();
			}
		}

		setBlock.getBlock().setType(Material.TNT);
	}
}
