package de.buddelbubi.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.network.protocol.ToastRequestPacket;
import cn.nukkit.utils.Config;
import de.buddelbubi.Reaxt;

public class ReaxtApi {

	private static Config config;

	private static List<Integer> IgnoredEntitys = new ArrayList<>();
	
	public static void load() {
		if(config == null) {
		File configfile = new File(Reaxt.get().getDataFolder(), "config.yml");
		//if(!configfile.exists()) Reaxt.get().saveResource("config.yml");
		Reaxt.get().saveDefaultConfig();
		config = new Config(configfile);
		
		//Load Data
		for(String s : config.getStringList("IgnoredEntitys")) {
			try {
				Entity ent = Entity.createEntity(s.replace(" ", ""), Server.getInstance().getDefaultLevel().getSpawnLocation());
				IgnoredEntitys.add(ent.getNetworkId());
				ent.despawnFromAll();
				ent.close();
				
			} catch (Exception e) {
				e.printStackTrace();
				ReaxtLog.error("Could not find an Entity called " + s);
			}
		}
		
		} else Reaxt.get().getLogger().error("Reaxt Api was already loaded.");
	}
	
	public static List<String> getIgnoredWorlds(){
		
		return config.getStringList("IgnoredWorlds");
		
	}
	
	public static int getPlayerLookupRange() {
		return config.getInt("PlayerLookupRange");
	}
	
	public static List<Integer> getIgnoredEntitys() {
		
		return IgnoredEntitys;
		
	}
	
	public static boolean isOperatorDefaultLogger() {
		
		return config.getBoolean("OperatorDefaultLogger");
		
	}
	
	public static boolean ignoreNametagEntitys() {
		
		return config.getBoolean("ignoreNametagEntitys");
		
	}
	
	public static int getChunkEntityLimit() {
		
		return config.getInt("ChunkEntityLimit.Limit");
		
	}
	
	public static boolean preventChunkEntityLimitOverflow() {
		
		return config.getBoolean("ChunkEntityLimit.PreventOverflow");
		
	}
	
	public static int getChunkCenterNearbyRangeExtention() {
		
		return config.getInt("ChunkEntityLimit.ChunkCenterNearbyRangeExtention");
		
	}
	
	public static int getChunkEntityLimitCleanupTicks() {
		
		return config.getInt("ChunkEntityLimit.ClearAfter");
		
	}
	
	public static boolean shouldNotifyAffectedChunk() {
		
		return config.getBoolean("ChunkEntityLimit.NotifyAffected");
		
	}
	
	public static int getWorldEntityLimit() {
		
		return config.getInt("WorldEntityLimit.Limit");
		
	}
	
	public static boolean preventWorldEntityLimitOverflow() {
		
		return config.getBoolean("WorldEntityLimit.PreventOverflow");
		
	}
	
	public static int getWorldEntityLimitCleanupTicks() {
		
		return config.getInt("WorldEntityLimit.ClearAfter");
		
	}
	
	public static boolean shouldNotifyAffectedWorld() {
		
		return config.getBoolean("WorldEntityLimit.NotifyAffected");
		
	}
	
	public static int getScannerTicks() {
		
		return config.getInt("Scanner.ScheduleTicks");
		
	}
 	
	public static boolean isScannerComparingLocations() {
		
		return config.getBoolean("Scanner.CompareLocations.Enabled");
		
	}
	
	public static boolean shouldScanKillStacks() {
		
		return config.getBoolean("Scanner.CompareLocations.KillStacks");
		
	}
	
	public static int getScannerStack() {
		
		return config.getInt("Scanner.CompareLocations.IdentifyAsStack");
		
	}
	
	public static boolean shouldNotifyAffectedStack() {
		
		return config.getBoolean("Scanner.CompareLocations.NotifyAffected");
		
	}
	
	public static int getScannerModulo() {
		
		return config.getInt("Scanner.ScanModuloEntitySize");
		
	}
	
	public static int getLimitationScheduleTicks() {
		
		return config.getInt("Limitation.ScheduleTicks");
		
	}
	
	public static int getRedstoneLimit() {
		
		return config.getInt("Limitation.Redstone");
		
	}
	
	public static int getBlockUpdateLimit() {
		
		return config.getInt("Limitation.BlockUpdate");
		
	}
	
	public static int getExplosionLimit() {
		
		return config.getInt("Limitation.Explosions");
		
	}
	
	public static boolean shouldNotifyAffectedLimit() {
		
		return config.getBoolean("Limitation.NotifyAffected");
		
	}
	
	public static boolean shouldLogPlayers() {
		
		return config.getBoolean("Logger.Players");
		
	}
	
	public static boolean shouldLogEvents() {
		
		return config.getBoolean("Logger.Events");
		
	}
	
	public static void sendNotification(Player player, String title, String content) {
		
		ToastRequestPacket packet = new ToastRequestPacket();
		packet.title = title;
		packet.content = content;
		player.dataPacket(packet);
		
	}
	
}
