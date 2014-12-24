package de.jinx.minicraft;

import org.bukkit.entity.Player;

public class PlayerInfo {
	
	public Player player;
	public Arena selectedArena;
	public Arena arena;
	public int m_teamIndex;

	public PlayerInfo(Player p) {
		player = p;
	}
}
