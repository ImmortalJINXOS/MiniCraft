package de.jinx.minicraft;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import de.jinx.minicraft.gamemodes.GameMode;

public class Arena {
	
	public int ID;
	public List players = new ArrayList();
	public int team1PlayerCount;
	public int team2PlayerCount;
	public char team1Color;
	public char team2Color;
	public GameMode gameMode;
	public Location[] team1Spawns = new Location[9];
	public Location[] team2Spawns = new Location[9];
	
	public Arena(int id)
	{
		ID = id;
	}
	
	public boolean playerJoining(PlayerInfo player)
	{
		if (team1PlayerCount == 3 && team2PlayerCount == 3)
		{
			player.player.sendMessage("This arena is full.");
			return false;
		}
		// TODO: extra stuff
		return true;
	}
	
	public void printInfo(CommandSender cs)
	{
		cs.sendMessage("[Arena #" + ID + "]");
		cs.sendMessage("Players: " + (team1PlayerCount + team2PlayerCount) + "/6");
		cs.sendMessage("Gamemode: " + gameMode);
	}

	public void setGameMode(GameMode g)
	{
		gameMode = g;
		g.arena = this;
	}
	
	public void playerLeaving(PlayerInfo pi) {
		
		
	}
}
