package de.buddelbubi;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginBase;
import de.buddelbubi.commands.ReaxtCommand;
import de.buddelbubi.listeners.ChunkEntityListener;
import de.buddelbubi.listeners.LimitationListener;
import de.buddelbubi.listeners.PlayerListener;
import de.buddelbubi.listeners.WorldEntityListener;
import de.buddelbubi.utils.LagScanner;
import de.buddelbubi.utils.ReaxtApi;
import de.buddelbubi.utils.ReaxtLog;


public class Reaxt extends PluginBase {

	private static Plugin plugin;

	public static final String prefix = "§fRea§9x§ft §8» §7";

	@Override
	public void onEnable() {
		
		plugin = this;
		ReaxtApi.load();
		LagScanner.start();
		ReaxtLog.build(); //Creates the folders for the Log Files
		
		Command command = new ReaxtCommand("reaxt");
		command.setPermission("reaxt");
		Server.getInstance().getCommandMap().register(command.getName(), command);
		
		this.registerListener();
		
		if(ReaxtApi.isOperatorDefaultLogger()) {
			for(Player p : Server.getInstance().getOnlinePlayers().values()) {
				if(p.isOp()) {
					ReaxtLog.logUser.add(p.getName());
					ReaxtLog.info(p.getName() + " joined the log mode.");
				}
			}
		}
		
	}
	
	@Override
	public void onDisable() {
		
		if(ReaxtApi.shouldLogEvents() && ReaxtLog.getEventLog().split("\n").length >= 4) {
			
			String log = saveLog();
			ReaxtLog.info("Reaxt Event Log created as " + log);
			
		}
		
	}
	
	public static String saveLog() {
		
		File events = new File(Reaxt.get().getDataFolder() + "/logs", "events"); 
		
		@SuppressWarnings("deprecation")
		//String time = new SimpleDateFormat("yyyy.MM.dd-HH:mm").format(new Date(System.currentTimeMillis()));
		String time = new Timestamp(System.currentTimeMillis()).toLocaleString().replace(":", ".");
		File eventfile = new File(events, time + ".log");
			try {
				if(!eventfile.exists()) eventfile.createNewFile();
				 BufferedWriter writer = new BufferedWriter(new FileWriter(eventfile));
				 writer.write(ReaxtLog.getEventLog());
				 writer.close();
				 return time + ".log";
			} catch (IOException e) {
				ReaxtLog.error("Could not create Log " + time + ".log");
				return null;
			}
			
	}
	
	public static Plugin get() {
		return plugin;
	}
	
	private void registerListener() {
		
		Server.getInstance().getPluginManager().registerEvents(new ChunkEntityListener(), this);
		Server.getInstance().getPluginManager().registerEvents(new WorldEntityListener(), this);
		Server.getInstance().getPluginManager().registerEvents(new LagScanner(), this);
		Server.getInstance().getPluginManager().registerEvents(new PlayerListener(), this);
		Server.getInstance().getPluginManager().registerEvents(new ReaxtLog(), this);
		Server.getInstance().getPluginManager().registerEvents(new LimitationListener(), this);
		
	}
	
}
