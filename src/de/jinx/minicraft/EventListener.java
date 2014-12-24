package de.jinx.minicraft;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class EventListener implements Listener {

	MiniCraft miniCraft;
	
	public EventListener(MiniCraft plugin)
	{
		miniCraft = plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		miniCraft.getPlayerInfo(event.getPlayer(), true);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		PlayerInfo p = miniCraft.getPlayerInfo(event.getPlayer(), false);
		if (p != null) miniCraft.playerInfos.remove(p);
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event)
	{
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event)
	{
		InventoryAction a = event.getAction();
		if (event.getInventory().getTitle().equals("Select a color"))
		{
			PlayerInfo p = miniCraft.getPlayerInfo((Player)event.getWhoClicked(), true);
			if (p.selectedArena == null) return;
			if (p.m_t == 0)
			{
				if (a == InventoryAction.PICKUP_HALF)
				{
					p.player.sendMessage("Color selection cancelled");
					Bukkit.getScheduler().runTask(miniCraft, new Runnable() {
						@Override
						public void run() {
							p.player.closeInventory();
						}
					});
				}
				else
				{
					p.selectedArena.team1Color = MiniCraft.ColorCodes[event.getSlot()];
					p.m_t = 1;
					p.player.sendMessage("Team 1 color set to: §" + p.selectedArena.team1Color + MiniCraft.Colors[event.getSlot()]);
					p.player.sendMessage("Select color for Team 2");
				}
				event.setCancelled(true);
			}
			else if (p.m_t == 1)
			{
				if (a == InventoryAction.PICKUP_HALF)
				{
					p.m_t = 0;
					p.player.sendMessage("Select color for Team 1");
				}
				else
				{
					p.selectedArena.team2Color = MiniCraft.ColorCodes[event.getSlot()];
					p.player.sendMessage("Team 2 color set to: §" + p.selectedArena.team2Color + MiniCraft.Colors[event.getSlot()]);
					Bukkit.getScheduler().runTask(miniCraft, new Runnable() {
						@Override
						public void run() {
							p.player.closeInventory();
						}
					});
				}
				event.setCancelled(true);
			}
		}
		
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		ItemStack is = event.getItem();
		Action action = event.getAction();
		Player player = event.getPlayer();
		if (is != null)
		{
			if (is.equals(miniCraft.arenaToolTeam1))
			{
				if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
				{
					player.getInventory().setItemInHand(miniCraft.arenaToolTeam2);
				}
				else if (action == Action.LEFT_CLICK_BLOCK)
				{
					miniCraft.openSpawnSelection(player, event.getClickedBlock().getLocation(), 0);
				}
				event.setCancelled(true);
			}
			else if (is.equals(miniCraft.arenaToolTeam2))
			{
				if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
				{
					player.getInventory().setItemInHand(miniCraft.arenaToolTeam1);
				}
				else if (action == Action.LEFT_CLICK_BLOCK)
				{
					miniCraft.openSpawnSelection(player, event.getClickedBlock().getLocation(), 1);
				}
				event.setCancelled(true);
			}
		}
	}
}
