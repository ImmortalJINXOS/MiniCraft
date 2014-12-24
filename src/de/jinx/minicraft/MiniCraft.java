package de.jinx.minicraft;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;

import de.jinx.minicraft.gamemodes.GameMode;
import de.jinx.minicraft.gamemodes.Shooter;
import de.jinx.minicraft.weapons.testing;

public class MiniCraft extends JavaPlugin {

	public static String[] Colors = new String[]{"White", "Orange", "Magenta", "Light Blue", "Yellow", "Lime", "Pink", "Gray", "Light Gray", "Cyan", "Purple", "Blue", "Brown", "Green", "Red", "Black"};
	public static char[] ColorCodes = new char[]{'f', '6', 'd', 'b', 'e', 'a', 'c', '8', '7', '3', '5', '1', '4', '2', 'c', '0'};
	
	List<Arena> arenas = new ArrayList<Arena>();
	List<PlayerInfo> playerInfos = new ArrayList<PlayerInfo>();
	ItemStack arenaToolTeam1;
	ItemStack arenaToolTeam2;
	Inventory colorSelectInventory;
	
	public void openSpawnSelection(Player p, Location l, int teamIndex)
	{
		PlayerInfo pl = getPlayerInfo(p, true);
		if (pl.selectedArena == null) return;
		pl.m_l = l;
		pl.m_t = teamIndex;
		
		Inventory inv = Bukkit.createInventory(null, 9, "Select spawn");
		for (int i = 0; i < 9; i++)
		{
			ItemStack is = new ItemStack(Material.WOOL);
			ItemMeta im = is.getItemMeta();
			MaterialData id = is.getData();
			im.setDisplayName("Spawn #" + i);
			is.setItemMeta(im);
			if ((teamIndex == 0 && pl.selectedArena.team1Spawns[i] != null) || (teamIndex == 1 && pl.selectedArena.team2Spawns[i] != null))
			{
				id.setData((byte)5);
				is.setData(id);
				inv.addItem(is);
			}
			else
			{
				inv.addItem(is);
			}
		}
	}
	
	private void prepareTools()
	{	
		arenaToolTeam1 = new ItemStack(org.bukkit.Material.WOOD_AXE);
		ItemMeta im = arenaToolTeam1.getItemMeta();
		im.setDisplayName("§6Arena Tool Team 1");
		List<String> list = new ArrayList<String>();
		list.add("Admin tool to assign spawn locations for");
		list.add("arenas. Use /arena select {id} to select");
		list.add("an arena to use. Simply delete the item");
		list.add("when you're done");
		im.setLore(list);
		arenaToolTeam1.setItemMeta(im);
		
		arenaToolTeam2 = new ItemStack(org.bukkit.Material.WOOD_AXE);
		im = arenaToolTeam2.getItemMeta();
		im.setDisplayName("§aArena Tool Team 2");
		list = new ArrayList<String>();
		list.add("Admin tool to assign spawn locations for");
		list.add("arenas. Use /arena select {id} to select");
		list.add("an arena to use. Simply delete the item");
		list.add("when you're done");
		im.setLore(list);
		arenaToolTeam2.setItemMeta(im);

		colorSelectInventory = Bukkit.createInventory(null, 18, "Select a color");
		for (Byte i = 0; i < 16; i++)
		{
			ItemStack is = new ItemStack(Material.WOOL, 1, (short)0, i);
			im = is.getItemMeta();
			im.setDisplayName(Colors[i]);
			is.setItemMeta(im);
			colorSelectInventory.addItem(is);
		}
	}
	
	@Override
	public void onEnable()
	{
		this.saveDefaultConfig();
		
		prepareTools();
		
		GameMode.load(getLogger());
		
		getServer().getPluginManager().registerEvents(new EventListener(this), this);
		getServer().getPluginManager().registerEvents(new testing(this), this);
	}
	
	@Override
	public void onDisable()
	{
		saveConfig();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (cmd.getName().equals("arena") && cmd.testPermission(sender))
		{
			if (args.length == 0) // If the player simply typed /arena print info of currently selected arena
			{
				if (sender instanceof Player)
				{
					PlayerInfo pi = getPlayerInfo((Player)sender, true);
					if (pi.selectedArena != null)
					{
						pi.selectedArena.printInfo(sender);
					}
					else
					{
						sender.sendMessage("Select an arena with /arena select {id} first");
						sender.sendMessage("or use /arena help for a list of commands");
					}
				}
				else
				{
					sender.sendMessage("Only players can use this command.");
				}
			}
			else if (args[0].equals("tool")) // Give the player the arena tool
			{
				if (sender instanceof Player)
				{
					Player player = (Player)sender;
					player.getInventory().setItemInHand(arenaToolTeam1.clone());
				}
				else
				{
					sender.sendMessage("Only players can use this command.");
				}
			}
			else if (args[0].equals("gamemode"))
			{
				if (args.length < 2)
				{
					sender.sendMessage("Usage: /arena gamemode [name]");
					return true;
				}
				if (sender instanceof Player)
				{
					PlayerInfo pi = getPlayerInfo((Player)sender, true);
					if (pi.selectedArena == null)
					{
						sender.sendMessage("Select an arena with /arena select {id} first");
					}
					GameMode g = GameMode.getByName(args[1]);
					if (g == null)
					{
						sender.sendMessage("A gamemode with this name doesn't exist");
					}
					else
					{
						pi.selectedArena.setGameMode(g);
						sender.sendMessage("Gamemode set to: " + args[1]);
					}
				}
				else
				{
					sender.sendMessage("Only players can use this command.");
				}
			}
			else if (args[0].equals("select"))
			{
				if (args.length < 2)
				{
					sender.sendMessage("Usage: /arena select [arenaId]");
					return true;
				}
				int arenaId = Integer.parseInt(args[1]);
				if (sender instanceof Player)
				{
					Arena arena = null;
					for (Arena a : arenas)
					{
						if (a.ID == arenaId)
						{
							arena = a;
							break;
						}
					}
					if (arena == null)
					{
						sender.sendMessage("An arena with this ID does not exist, use /arena create [ID] to create one");
						return true;
					}
					PlayerInfo pi = getPlayerInfo((Player)sender, true);
					pi.selectedArena = arena;
					pi.player.sendMessage("Selected Arena #" + arena.ID);
				}
				else
				{
					sender.sendMessage("Only players can use this command.");
				}
			}
			else if (args[0].equals("create"))
			{
				if (sender instanceof Player)
				{
					if (args.length < 2)
					{
						sender.sendMessage("Usage: /arena create [arenaId]");
						return true;
					}
					int id = Integer.parseInt(args[1]);
					for (Arena a : arenas)
					{
						if (a.ID == id)
						{
							sender.sendMessage("An arena with this ID already exists");
							return true;
						}
					}
					Arena arena = new Arena(id);
					arenas.add(arena);
					Player player = (Player)sender;
					PlayerInfo pi = getPlayerInfo(player, true);
					pi.selectedArena = arena;
					pi.m_t = 0;
					player.openInventory(colorSelectInventory);
					player.sendMessage("Arena #" + id + " created");
					player.sendMessage("Select color for Team 1");
				}
				else
				{
					sender.sendMessage("Only players can use this command.");
				}
			}
			else
			{
				sender.sendMessage("There is no such arena command");
			}
			return true;
		}
		return false;
	}
	
	
	public PlayerInfo getPlayerInfo(Player p, boolean createIfNessessary)
	{
		for (PlayerInfo pi : playerInfos) // Loop through the list and delete the player if found
		{
			if (pi.player == p)
			{
				return pi;
			}
		}
		if (createIfNessessary)
		{
			PlayerInfo pt = new PlayerInfo(p);
			playerInfos.add(pt);
			return pt;
		}
		return null;
	}
}
