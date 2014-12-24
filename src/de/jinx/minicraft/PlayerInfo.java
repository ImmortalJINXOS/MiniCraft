package de.jinx.minicraft;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerInfo {
	
	public Player player;
	public Arena selectedArena;
	public Arena arena;
	public int teamIndex;
	public int m_t;
	public Location m_l;

	public PlayerInfo(Player p) {
		player = p;
	}
}
