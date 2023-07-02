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
import cn.nukkit.math.Vector2;
import de.buddelbubi.Reaxt;
import de.buddelbubi.utils.ReaxtApi;
import de.buddelbubi.utils.ReaxtDetectionEvent;
import de.buddelbubi.utils.ReaxtLog;

public class ChunkEntityListener implements Listener{
	
	List<Long> inqueue = new ArrayList<>();
	
	int allowedEntitys = 0;
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void on(EntitySpawnEvent e) {
		
		if(e.getEntity() instanceof EntityHuman) return;
		
		Position pos = e.getPosition();
		
		if(!ReaxtApi.getIgnoredWorlds().contains(pos.getLevel().getName())) {
			
			if(ReaxtApi.getChunkEntityLimit() > -1) {
				
				if(pos.getChunk().getEntities().size() - allowedEntitys >= ReaxtApi.getChunkEntityLimit()) {
					
					if(ReaxtApi.preventChunkEntityLimitOverflow()) {
						e.setCancelled(true);
					}
					
					if(ReaxtApi.getChunkEntityLimitCleanupTicks() > -1) {
						
						long chunklocation = pos.getChunk().getIndex();
						
						if(inqueue.contains(chunklocation)) return;
						List<Player> nearby = new ArrayList<>();
						if(ReaxtApi.getPlayerLookupRange() > -1) {
							for(Player p : pos.level.getPlayers().values()) {
								if(p.getLocation().distance(pos) <= ReaxtApi.getPlayerLookupRange()) nearby.add(p);
							}
						} else nearby.addAll(pos.getLevel().getChunkPlayers(pos.getChunkX(), pos.getChunkZ()).values());
						
						if(ReaxtApi.shouldNotifyAffectedChunk())
						for(Player p : nearby) {
							if(!ReaxtLog.logUser.contains(p.getName()))
							p.sendMessage(Reaxt.prefix + "Clearing nearby entities in §f" + ReaxtApi.getChunkEntityLimitCleanupTicks() / 20 + " §7seconds.");
						}
						
						inqueue.add(chunklocation);
						ReaxtLog.warning("Chunk " + pos.getChunkX() + ":" + pos.getChunkZ() + " (" + pos.getLevel().getName() + ") reached the chunk entity limit!");
						Server.getInstance().getPluginManager().callEvent(new ReaxtDetectionEvent(nearby, null, "Chunk Entity Overflow"));
						Server.getInstance().getScheduler().scheduleDelayedTask(new Runnable() {
							
							@Override
							public void run() {
								
								int cleared = 0;
								try {
									
									if(ReaxtApi.getChunkCenterNearbyRangeExtention() > -1) {
										
										Vector2 center = new Vector2(pos.getChunkX()*16+8, pos.getChunkZ()*16+8);
										for(Entity entity : pos.level.getEntities()) {
											
											if(entity instanceof EntityHuman) continue;
											
											Vector2 entpos = new Vector2(entity.x, entity.z);
											if(entpos.distance(center) <= ReaxtApi.getChunkCenterNearbyRangeExtention() + 8 ) {
												if(ReaxtApi.getIgnoredEntitys().contains(entity.getNetworkId())) {
													allowedEntitys++;
													continue;
												}
												if((ReaxtApi.ignoreNametagEntitys() && entity.hasCustomName()) || entity instanceof Player) continue;
												cleared++;
												entity.close();
											}
											
										}
										
									} else {
										for(Entity entity : pos.getChunk().getEntities().values()) {
											
											if(entity instanceof EntityHuman) continue;
											
											if(ReaxtApi.getIgnoredEntitys().contains(entity.getNetworkId())) {
												allowedEntitys++;
												continue;
											}
											if((ReaxtApi.ignoreNametagEntitys() && entity.hasCustomName()) || entity instanceof Player) continue;
											cleared++;
											entity.close();
										}
									}
								} catch (Exception e2) {
									
								}
								if(ReaxtApi.shouldNotifyAffectedChunk())
								for(Player p : nearby) {
										p.sendMessage(Reaxt.prefix + "Cleared §f" + cleared + " §7entitys in your area.");
								}
								
								inqueue.remove(chunklocation);
								
							}
						}, ReaxtApi.getChunkEntityLimitCleanupTicks());
						
					}
					
				}
				
			}
			
		}
		
	}
	
}
