package de.buddelbubi.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntitySpawnEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import de.buddelbubi.Reaxt;

public class LagScanner implements Listener {

	private static boolean running = false;
	
	private static HashMap<String, Integer> stacks = null;
	
	@SuppressWarnings("deprecation")
	public static void start() {
		
		if(running) return;
		
		if(ReaxtApi.getScannerTicks() > -1) {
			
			Server.getInstance().getScheduler().scheduleDelayedRepeatingTask(new Runnable() {
				
				@Override
				public void run() {
					scan();
				}
				
			}, ReaxtApi.getScannerTicks(), ReaxtApi.getScannerTicks());
			
		}
		
		
	}
	
	@EventHandler
	public void on(EntitySpawnEvent e) {
		if(!ReaxtApi.getIgnoredWorlds().contains(e.getPosition().getLevel().getName())) {
			if(ReaxtApi.getScannerModulo() > -1) {
				if(e.getPosition().getLevel().getEntities().length % ReaxtApi.getScannerModulo() == 0) {
					scan();
				}
				
			}
		}
		
	}
	
	
	private static void scan() {
		stacks = new HashMap<>();
		for(Level level : Server.getInstance().getLevels().values()) {
			for(Entity entity : level.getEntities()) {
				String key = level.getId() + "#" + (int) entity.x + "#" + (int) entity.y + "#" + (int) entity.z;
				if(stacks.containsKey(key)) {
					stacks.put(key, stacks.get(key) +1);
				} else stacks.put(key, 1);
			}
		}
		for(String s : stacks.keySet()) {
			if(stacks.get(s) >= ReaxtApi.getScannerStack()) {
				Level l = Server.getInstance().getLevel(Integer.parseInt(s.split("#")[0]));
				int x = Integer.parseInt(s.split("#")[1]);
				int y = Integer.parseInt(s.split("#")[2]);
				int z = Integer.parseInt(s.split("#")[3]);
				Location pos = new Location(x, y, z, l);
				List<Player> nearby = new ArrayList<>();
				if(ReaxtApi.getPlayerLookupRange() > -1) {
					for(Player p : pos.level.getPlayers().values()) {
						if(p.getLocation().distance(pos) <= ReaxtApi.getPlayerLookupRange()) nearby.add(p);
					}
				} else nearby.addAll(pos.getLevel().getChunkPlayers(pos.getChunkX(), pos.getChunkZ()).values());
				
				if(ReaxtApi.shouldScanKillStacks()) {

					for(Entity entity : l.getEntities()) {
						if((int) entity.x == x && (int) entity.z == z && !(entity instanceof Player)) {
							entity.close();
						}
					}
					if(ReaxtApi.shouldNotifyAffectedStack())
					for(Player p : nearby){
						if(!ReaxtLog.logUser.contains(p.getName()))
						p.sendMessage(Reaxt.prefix + "Stack of " + stacks.get(s) + " entities got detected and deleted.");
					}
					
				}
				ReaxtDetectionEvent e = new ReaxtDetectionEvent(nearby, null, "Entity Stack Detection");
				Server.getInstance().getPluginManager().callEvent(e);
				ReaxtLog.warning((ReaxtApi.shouldScanKillStacks() ? "Cleared" : "Detected") + " a Stack of " + stacks.get(s) + " entities at " + x  +":" + z + " (" + l.getName() + ")");
			}
		}
		stacks = null;
	}
	
}
