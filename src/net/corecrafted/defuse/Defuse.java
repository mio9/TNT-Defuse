package net.corecrafted.defuse;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class Defuse implements Listener {

	@EventHandler
	public void onDefuse(PlayerInteractEntityEvent e) {
		if (e.getRightClicked() instanceof TNTPrimed) {
			e.getPlayer().sendMessage("WOW");
			Entity tnt = e.getRightClicked();
			int x = tnt.getLocation().getBlockX();
			int y = tnt.getLocation().getBlockY();
			int z = tnt.getLocation().getBlockZ();
			tnt.remove();
			Location setBlock = new Location(tnt.getWorld(), x, y, z);
			ArrayList<Entity> craps = (ArrayList<Entity>) setBlock.getWorld().getNearbyEntities(setBlock, 2 , 2, 2);
			
			for (Entity rubbish : craps){
				if (rubbish instanceof TNTPrimed){
					rubbish.remove();
				}
			}
			
			setBlock.getBlock().setType(Material.TNT);
			

		}
	}

}
