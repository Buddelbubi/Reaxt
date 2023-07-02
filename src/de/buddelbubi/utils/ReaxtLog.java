package de.buddelbubi.utils;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import cn.nukkit.IPlayer;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.level.Location;
import cn.nukkit.utils.Config;
import de.buddelbubi.Reaxt;

public class ReaxtLog implements Listener {
	
	public static List<String> logUser = new ArrayList<>();
	
	public static void info(String message) {
		
		Reaxt.get().getLogger().info(message);
		for(String s : logUser) {
			@SuppressWarnings("deprecation")
			IPlayer pl = Server.getInstance().getOfflinePlayer(s);
			if(pl.isOnline()) pl.getPlayer().sendMessage(Reaxt.prefix + "§aInfo §8» §7" + message);
		}
		
	}
	
	public static void warning(String message) {
		
		Reaxt.get().getLogger().warning(message);
		for(String s : logUser) {
			@SuppressWarnings("deprecation")
			IPlayer pl = Server.getInstance().getOfflinePlayer(s);
			if(pl.isOnline()) pl.getPlayer().sendMessage(Reaxt.prefix + "§eWarning §8» §7" + message);
		}
	}
	
	public static void error(String message) {
		
		Reaxt.get().getLogger().error(message);
		for(String s : logUser) {
			@SuppressWarnings("deprecation")
			IPlayer pl = Server.getInstance().getOfflinePlayer(s);
			if(pl.isOnline()) pl.getPlayer().sendMessage(Reaxt.prefix + "§cError §8» §7" + message);
		}
		
	}
	
	// File Loggers
	
	private static String log; 
	
	public static String getEventLog() {
		return log;
	}
	
	public static void build() {
		File folder = new File(Reaxt.get().getDataFolder(), "logs");
		if(!folder.exists()) folder.mkdir();
		if(ReaxtApi.shouldLogPlayers()) {
			File players = new File(folder, "players");
			if(!players.exists()) players.mkdir();
		}
		if(ReaxtApi.shouldLogEvents()) {
			File events = new File(folder, "events"); 
			if(!events.exists()) events.mkdir();
			@SuppressWarnings("deprecation")
			String time = new Timestamp(System.currentTimeMillis()).toLocaleString();
			log = "Reaxt Detection Log - Starting at " + time + "\n\n";
		}
	}
	
	private static Location latestcause = null;
	private static Long latesttime = System.currentTimeMillis();
	
	public static Location getLatestLoc() {
		return latestcause;
	}
	public static Long getLatestLocTime() {
		return latesttime;
	}
	
	@EventHandler
	public void on(ReaxtDetectionEvent e) throws IOException {
		
		if(e.getApproximatedLocation() != null) {
		for(String s : logUser) {
			
			Player pl = Server.getInstance().getPlayer(s);
			ReaxtApi.sendNotification(pl, "§l" + Reaxt.prefix + e.getCause() + "§f detected with §7" + e.getPlayers().size() + "§f nearby players!","§o§aSneak within the next 5 seconds to teleport.");
			
		}
		
		latestcause = e.getApproximatedLocation();
		latesttime = System.currentTimeMillis(); 
		}
		
		@SuppressWarnings("deprecation")
		String time = new Timestamp(e.getTime()).toLocaleString();
		
		if(ReaxtApi.shouldLogPlayers()) {
			
			File playerfolder = new File( Reaxt.get().getDataFolder() + "/logs", "players");
			for(Player p : e.getPlayers()) {
				
				File playerfile = new File(playerfolder, p.getName() + ".yml");
				if(!playerfile.exists()) playerfile.createNewFile();
				Config log = new Config(playerfile);
				List<String> events = new ArrayList<>();
				if(log.exists("events")) events = log.getStringList("events");
				events.add("[" + time + "] " + e.getCause());
				log.set("events", events);
				log.save();
				
			}
		}
		
		if(ReaxtApi.shouldLogEvents()) {
			
			String valuestring = "";
			 int index = 0;
			 for(Player o : e.getPlayers()) { 
				 index++;
				 valuestring += o.getName() + (index != e.getPlayers().size() ? ", " : "");
			 }
			 Location loc = e.getApproximatedLocation();
			log += "\n[" + time + "] " + e.getCause() + " (" + e.getPlayers().size() + " Players affected | " + valuestring + ") - " + (e.getPlayers().size() > 0 ? ("Approximated Location: " + loc.getLevelName() + ", X: " + (int) loc.x + ", Y: " + (int) loc.y + ", Z: " + (int) loc.z) : "Unknown Location!");
			
		}
	}
	
	
	
}
