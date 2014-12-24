package de.jinx.minicraft.gamemodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import de.jinx.minicraft.Arena;
import de.jinx.minicraft.PlayerInfo;

public abstract class GameMode {

	public Arena arena;
	public static int GameModeID = 0;
	public static Map<String, Class> GameModes = new HashMap<String, Class>();
	private static Logger logger;
	
	public abstract void applyEffects(PlayerInfo player);
	
	public static void register(String name, Class gm)
	{
		try
		{
			int id = GameModes.size() + 1;
			gm.getField("GameModeID").set(null, id);
			GameModes.put(name, gm);
			logger.info("Loaded gamemode '" + name + "' with ID: " + id);
		}
		catch (Exception e)
		{
			logger.warning("Failed to load gamemode '" + name + "'");
		}
	}
	
	public static void load(Logger logger)
	{
		GameMode.logger = logger;
		register("Shooter", Shooter.class);
	}
	
	public static GameMode getByName(String name)
	{
		try
		{
			if (!GameModes.containsKey(name)) return null;
			else return (GameMode) GameModes.get(name).newInstance();
		}
		catch (Exception e)
		{
			return null;
		}
	}
}
