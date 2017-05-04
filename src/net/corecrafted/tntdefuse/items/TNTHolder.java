package net.corecrafted.tntdefuse.items;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import net.corecrafted.tntdefuse.Main;
import net.md_5.bungee.api.ChatColor;

public class TNTHolder implements Listener {

	Main plugin;

	public TNTHolder(Main plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onCapture(PlayerInteractEntityEvent e) {
		if (e.getRightClicked() instanceof TNTPrimed) {
			if (plugin.getConfig().getBoolean("enable-holder") == false) {
				return;
			}
			if (e.getHand().equals(EquipmentSlot.OFF_HAND)) {
				return;
			}
			if (e.getPlayer().hasPermission("defuse.pickup")) {
				PlayerInventory inv = e.getPlayer().getInventory();
				ItemStack playerHolding = inv.getItemInMainHand();
				ItemStack requiredItem = plugin.getItemsFile().getItemStack("holder");
				if (playerHolding.equals(requiredItem)) {
					
					TNTPrimed tnt = (TNTPrimed) e.getRightClicked();
					int fuseTick = tnt.getFuseTicks();
					inv.setItemInMainHand(setItemFuse(playerHolding, fuseTick));
					tnt.remove();
					return;
					// Get TNT itself
					// get fuseticks of tnt -> store value to lore of the item
				}

			} else {
				return;
			}
		}

	}

	@EventHandler
	public void onRelease(PlayerInteractEvent e) {
		if (e.getPlayer().hasPermission("defuse.pickup")) {
			if (plugin.getConfig().getBoolean("enable-holder") == false) {
				return;
			}
			if (e.getHand().equals(EquipmentSlot.OFF_HAND)) {
				return;
			}
			if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
				PlayerInventory inv = e.getPlayer().getInventory();
				ItemStack itemHolding = inv.getItemInMainHand();
				if (itemHolding.getItemMeta().getEnchantLevel(Enchantment.PROTECTION_ENVIRONMENTAL)==593 && itemHolding.getType()==plugin.getItemsFile().getItemStack("holder").getType()){
					int tntFuse = getItemFuse(itemHolding);
					inv.setItemInMainHand(plugin.getItemsFile().getItemStack("holder"));
					Location tntSpawnLoc = getClickedLoc(e.getClickedBlock(), e.getBlockFace());
					TNTPrimed tnt = e.getPlayer().getWorld().spawn(tntSpawnLoc, TNTPrimed.class);
					tnt.setFuseTicks(tntFuse);
					return;
				} else {
					return;
				}
				
			}
			// get tnt values from item lore
			// clear item lore
			// spawn in tnt on the side the block clicked (with fuse data)
		}
	}

	/*
	 * @EventHandler public void onDebug(PlayerInteractEvent e){ if
	 * (e.getHand().equals(EquipmentSlot.OFF_HAND)){ return; }
	 * e.getPlayer().sendMessage(e.getBlockFace().toString()); }
	 */
	private ItemStack setItemFuse(ItemStack item, int fuseTick) {
		ItemStack holdingItem = item;
		List<String> lore = new ArrayList<String>();
		lore.add("");
		lore.add(ChatColor.GRAY + "Storing TNT with fuse tick:");
		lore.add(String.valueOf(fuseTick));
		ItemMeta meta = holdingItem.getItemMeta();
		meta.setLore(lore);
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 593, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		holdingItem.setItemMeta(meta);
		return holdingItem;
	}

	private int getItemFuse(ItemStack item) {
		List<String> lore = item.getItemMeta().getLore();
		int fuseTickNumber = Integer.parseInt(lore.get(2));
		return fuseTickNumber;
	}

	private Location getClickedLoc(Block block, BlockFace face) {
		Location clickBlockLoc = block.getLocation();
		if (face.equals(BlockFace.DOWN)) {
			return clickBlockLoc.subtract(0, 1, 0);
		} else if (face.equals(BlockFace.UP)) {
			return clickBlockLoc.add(0, 1, 0);
		} else if (face.equals(BlockFace.EAST)) {
			return clickBlockLoc.add(1, 0, 0);
		} else if (face.equals(BlockFace.SOUTH)) {
			return clickBlockLoc.add(0, 0, 1);
		} else if (face.equals(BlockFace.WEST)) {
			return clickBlockLoc.subtract(1, 0, 0);
		} else if (face.equals(BlockFace.NORTH)) {
			return clickBlockLoc.subtract(0, 0, 1);
		} else {
			return null;
		}
	}
}
