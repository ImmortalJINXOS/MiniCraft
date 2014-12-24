package de.jinx.minicraft.weapons;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import de.jinx.minicraft.MiniCraft;

public class testing implements Listener{
	

	MiniCraft miniCraft;
	
	public testing(MiniCraft plugin)
	{
		miniCraft = plugin;
	}
	
	@EventHandler
	public void testweapon(PlayerInteractEvent event){
		Action eventAction = event.getAction();
		Player player = event.getPlayer();
		
		if(!player.getItemInHand().getType().equals(Material.GOLD_INGOT)) {
			player.sendMessage("returned(not gold ingot)");
			return;
		}
		
		if(eventAction != Action.RIGHT_CLICK_AIR && eventAction != Action.RIGHT_CLICK_BLOCK)
		{
			player.sendMessage("returned(not right click)");
			return;
		}
		
		shootbullet(player, (double) 0, player.getEyeLocation());
	}

	
	private void shootbullet(LivingEntity shooter, Double speed, Location shootLocation){
		Vector directionVector = shootLocation.getDirection().normalize();
		double startShift = 1;
		Vector shootShiftVector = new Vector(directionVector.getX(), directionVector.getY(), directionVector.getZ());
		shootLocation = shootLocation.add(shootShiftVector.getX(), shootShiftVector.getY(), shootShiftVector.getZ());
		
		SmallFireball bullet = shootLocation.getWorld().spawn(shootLocation, SmallFireball.class);
		bullet.setVelocity(directionVector.multiply(speed));
		
		if(bullet instanceof SmallFireball){
			bullet.setIsIncendiary(false);
			bullet.setShooter(shooter);
		}
		
	}
	
}
