package de.buddelbubi.listeners;

import java.util.ArrayList;
import java.util.List;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntitySpawnEvent;
import cn.nukkit.level.Position;
import de.buddelbubi.Reaxt;
import de.buddelbubi.utils.ReaxtApi;
import de.buddelbubi.utils.ReaxtDetectionEvent;
import de.buddelbubi.utils.ReaxtLog;

public class WorldEntityListener implements Listener{
	
	List<String> inqueue = new ArrayList<>();
	
	int allowedEntitys = 0;
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void on(EntitySpawnEvent e) {
		
		if(e.isHuman()) return;
		Position pos = e.getPosition();
		
		if(!ReaxtApi.getIgnoredWorlds().contains(pos.getLevel().getName())) {
			
			if(ReaxtApi.getWorldEntityLimit() > -1) {
				
				if(pos.getLevel().getEntities().length - allowedEntitys >= ReaxtApi.getWorldEntityLimit()) {
					
					if(ReaxtApi.preventWorldEntityLimitOverflow()) {
						e.setCancelled(true);
					}
					
					if(ReaxtApi.getWorldEntityLimitCleanupTicks() > -1) {
						
						String level = String.valueOf(e.getPosition().getLevel().getId());
						
						if(inqueue.contains(level)) return;
						for(Player p : e.getPosition().getLevel().getPlayers().values()) {
							if(!ReaxtLog.logUser.contains(p.getName()))
							p.sendMessage(Reaxt.prefix + "Clearing entities in §f" + ReaxtApi.getWorldEntityLimitCleanupTicks()/20 + " §7seconds.");
						}
						inqueue.add(level);
						ReaxtLog.warning(pos.getLevel().getName() + " reached the world entity limit!");
						Server.getInstance().getPluginManager().callEvent(new ReaxtDetectionEvent(pos.level.getPlayers().values(), null,  "World Entity Overflow"));
						Server.getInstance().getScheduler().scheduleDelayedTask(new Runnable() {
							
							@Override
							public void run() {
								
								int cleared = 0;
								try {
									for(Entity entity : e.getPosition().getLevel().getEntities()) {
										
										if(entity instanceof EntityHuman) continue;
										
										if(ReaxtApi.getIgnoredEntitys().contains(entity.getNetworkId())) {
											allowedEntitys++;
											continue;
										}
										if((ReaxtApi.ignoreNametagEntitys() && entity.hasCustomName()) || entity instanceof Player) continue;
										cleared++;
										entity.close();
									}
								} catch (Exception e2) {
									
								}
								
								for(Player p : e.getPosition().getLevel().getPlayers().values()) {
										p.sendMessage(Reaxt.prefix + "Cleared §f" + cleared + " §7entitys in your world.");
								}
								
								inqueue.remove(level);
								
							}
						}, ReaxtApi.getWorldEntityLimitCleanupTicks());
						
					}
					
				}
				
			}
			
		}
		
	}
	
}
