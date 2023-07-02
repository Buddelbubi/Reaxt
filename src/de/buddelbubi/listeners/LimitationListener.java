package de.buddelbubi.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.item.EntityMinecartTNT;
import cn.nukkit.entity.item.EntityPrimedTNT;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockUpdateEvent;
import cn.nukkit.event.entity.EntityExplosionPrimeEvent;
import cn.nukkit.event.entity.EntitySpawnEvent;
import cn.nukkit.event.redstone.RedstoneUpdateEvent;
import cn.nukkit.level.Location;
import cn.nukkit.level.ParticleEffect;
import cn.nukkit.level.Sound;
import de.buddelbubi.Reaxt;
import de.buddelbubi.utils.ReaxtApi;
import de.buddelbubi.utils.ReaxtDetectionEvent;
import de.buddelbubi.utils.ReaxtLog;

public class LimitationListener implements Listener {

	private static boolean explosionaknownledged = false;
	private static HashMap<String, Integer> explosioncounter = new HashMap<>();
	private static HashMap<String, Integer> redstonecounter = new HashMap<>();
	private static HashMap<String, Integer> blockupdatecounter = new HashMap<>();
	private static boolean redstoneaknownledged = false;
	private static boolean blockupdateaknownledged = false;
	
	@SuppressWarnings("deprecation")
	public LimitationListener() {
		
		Server.getInstance().getScheduler().scheduleDelayedRepeatingTask(new Runnable() {
			
			@Override
			public void run() {

				explosioncounter = new HashMap<>();
				explosionaknownledged = false;
				redstonecounter = new HashMap<>();
				blockupdatecounter = new HashMap<>();
				redstoneaknownledged = false;
				blockupdateaknownledged = false;

			}
		}, ReaxtApi.getLimitationScheduleTicks(), ReaxtApi.getLimitationScheduleTicks());
		
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void on(EntityExplosionPrimeEvent e) {
		if(e.isCancelled()) return;
		if(ReaxtApi.getIgnoredWorlds().contains(e.getEntity().getLevel().getName())) return;
		String key = e.getEntity().getLevel().getId() + "#" + (int) e.getEntity().getChunkX() + "#" + (int) e.getEntity().getChunkZ();
		if(explosioncounter.containsKey(key)) {
			if(explosioncounter.get(key) >= ReaxtApi.getExplosionLimit()) {
				if(!explosionaknownledged) {
					Location pos = e.getEntity().getLocation();
					List<Player> nearby = new ArrayList<>();
					if(ReaxtApi.getPlayerLookupRange() > -1) {
						for(Player p : pos.level.getPlayers().values()) {
							if(p.getLocation().distance(pos) <= ReaxtApi.getPlayerLookupRange()) nearby.add(p);
						}
					} else nearby.addAll(pos.getLevel().getChunkPlayers(pos.getChunkX(), pos.getChunkZ()).values());
					explosionaknownledged = true;
					if(ReaxtApi.shouldNotifyAffectedLimit())
						for(Player p : nearby) {
							if(!ReaxtLog.logUser.contains(p.getName()))
							p.sendMessage(Reaxt.prefix + "There are to many explosions in your nearby area.");
						}
					ReaxtLog.warning("Chunk " + pos.getChunkX() + ":" + pos.getChunkZ() + " (" + pos.getLevel().getName() + ") reached the explosions limit.!");
					Server.getInstance().getPluginManager().callEvent(new ReaxtDetectionEvent(nearby, pos, "Explosion Limit Overflow"));
					
				}
				e.setCancelled(true);	
			}else explosioncounter.put(key, explosioncounter.get(key) +1);
		} else explosioncounter.put(key, 1);
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void on(EntitySpawnEvent e) {
		if(!(e.getEntity() instanceof EntityPrimedTNT || e.getEntity() instanceof EntityMinecartTNT)) return;
		if(e.isCancelled()) return;
		if(ReaxtApi.getIgnoredWorlds().contains(e.getEntity().getLevel().getName())) return;
		String key = e.getEntity().getLevel().getId() + "#" + (int) e.getEntity().getChunkX() + "#" + (int) e.getEntity().getChunkZ();
		if(explosioncounter.containsKey(key)) {
			if(explosioncounter.get(key) >= ReaxtApi.getExplosionLimit()) {
				if(!explosionaknownledged) {
					Location pos = e.getPosition().getLocation();
					List<Player> nearby = new ArrayList<>();
					if(ReaxtApi.getPlayerLookupRange() > -1) {
						for(Player p : pos.level.getPlayers().values()) {
							if(p.getLocation().distance(pos) <= ReaxtApi.getPlayerLookupRange()) nearby.add(p);
						}
					} else nearby.addAll(pos.getLevel().getChunkPlayers(pos.getChunkX(), pos.getChunkZ()).values());
					if(ReaxtApi.shouldNotifyAffectedLimit())
					for(Player p : nearby) {
						if(!ReaxtLog.logUser.contains(p.getName()))
						p.sendMessage(Reaxt.prefix + "There are to many explosions in your nearby area.");
					}
					ReaxtLog.warning("Chunk " + pos.getChunkX() + ":" + pos.getChunkZ() + " (" + pos.getLevel().getName() + ") reached the explosions limit.!");
					explosionaknownledged = true;
					Server.getInstance().getPluginManager().callEvent(new ReaxtDetectionEvent(nearby, pos, "Explosion Limit Overflow (TNT)"));
				}
				e.setCancelled(true);	
			}else explosioncounter.put(key, explosioncounter.get(key) +1);
		} else explosioncounter.put(key, 1);
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void on(RedstoneUpdateEvent e) {
		if(ReaxtApi.getIgnoredWorlds().contains(e.getBlock().getLevel().getName())) return;
		if(e.isCancelled()) return;
		Block b = e.getBlock();
		String key = b.getLevel() + "#" + (int) b.getChunkX() + "#" + (int) b.getChunkZ();
		if(redstonecounter.containsKey(key)) {
			if(redstonecounter.get(key) >= ReaxtApi.getRedstoneLimit() || blockupdateaknownledged) {
				if(!redstoneaknownledged && !blockupdateaknownledged) {
					Location pos = e.getBlock().getLocation();
					List<Player> nearby = new ArrayList<>();
					if(ReaxtApi.getPlayerLookupRange() > -1) {
						for(Player p : pos.level.getPlayers().values()) {
							if(p.getLocation().distance(pos) <= ReaxtApi.getPlayerLookupRange()) nearby.add(p);
						}
					} else nearby.addAll(pos.getLevel().getChunkPlayers(pos.getChunkX(), pos.getChunkZ()).values());
					redstoneaknownledged = true;
					if(ReaxtApi.shouldNotifyAffectedLimit())
						for(Player p : nearby) {
							if(!ReaxtLog.logUser.contains(p.getName()))
							p.sendMessage(Reaxt.prefix + "Redstone overflow in your area detected and prevented.");
						}
					ReaxtLog.warning("Chunk " + pos.getChunkX() + ":" + pos.getChunkZ() + " (" + pos.getLevel().getName() + ") reached the Redstone Limit.!");

					Server.getInstance().getPluginManager().callEvent(new ReaxtDetectionEvent(nearby, null, "Redstone Limit Overflow"));
				}
				e.setCancelled(true);
				e.getBlock().level.addParticleEffect(b, ParticleEffect.BASIC_SMOKE);
				e.getBlock().level.addSound(e.getBlock(), Sound.EXTINGUISH_CANDLE,1,1);
			} else redstonecounter.put(key, redstonecounter.get(key) +1);
		} else redstonecounter.put(key, 1);
		
	}
	
	@EventHandler
	public void on(BlockUpdateEvent e) {
		if(e.isCancelled()) return;
		if(ReaxtApi.getIgnoredWorlds().contains(e.getBlock().getLevel().getName())) return;
		Block b = e.getBlock();
		if(b.getId() == 0) return;
		String key = b.getLevel().getId() + "#" + (int) b.getChunkX() + "#" + (int) b.getChunkZ();
		if(blockupdatecounter.containsKey(key)) {
			if(blockupdatecounter.get(key) >= ReaxtApi.getBlockUpdateLimit() || redstoneaknownledged) {
				if(!blockupdateaknownledged && !redstoneaknownledged) {
					blockupdateaknownledged = true;
					Location pos = e.getBlock().getLocation();
					List<Player> nearby = new ArrayList<>();
					if(ReaxtApi.getPlayerLookupRange() > -1) {
						for(Player p : pos.level.getPlayers().values()) {
							if(p.getLocation().distance(pos) <= ReaxtApi.getPlayerLookupRange()) nearby.add(p);
						}
					} else nearby.addAll(pos.getLevel().getChunkPlayers(pos.getChunkX(), pos.getChunkZ()).values());
					blockupdateaknownledged = true;
					if(ReaxtApi.shouldNotifyAffectedLimit())
						for(Player p : nearby) {
							if(!ReaxtLog.logUser.contains(p.getName()))
							p.sendMessage(Reaxt.prefix + "There were to many Block Updates in your area.");
						}
					ReaxtLog.warning("Chunk " + pos.getChunkX() + ":" + pos.getChunkZ() + " (" + pos.getLevel().getName() + ") reached the Block Update limit.!");

					Server.getInstance().getPluginManager().callEvent(new ReaxtDetectionEvent(nearby, pos, "Block Update Overflow"));
				}
				e.setCancelled(true);
			}else blockupdatecounter.put(key, blockupdatecounter.get(key) +1);
		} else blockupdatecounter.put(key, 1);
	}
	
}
