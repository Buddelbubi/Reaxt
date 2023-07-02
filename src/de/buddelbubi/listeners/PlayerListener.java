package de.buddelbubi.listeners;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.player.PlayerToggleSneakEvent;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.level.Location;
import cn.nukkit.utils.Config;
import de.buddelbubi.Reaxt;
import de.buddelbubi.utils.ReaxtApi;
import de.buddelbubi.utils.ReaxtLog;

public class PlayerListener implements Listener{

	@EventHandler
	public void on(PlayerQuitEvent e) {
		
		if(ReaxtLog.logUser.contains(e.getPlayer().getName())) {
			ReaxtLog.logUser.remove(e.getPlayer().getName());
			ReaxtLog.info(e.getPlayer().getName() + " left the log mode.");
		}
		if(teleported.containsKey(e.getPlayer().getName())) teleported.remove(e.getPlayer().getName());
		
	}
	
	@EventHandler
	public void on(PlayerJoinEvent e) {
		if(ReaxtApi.isOperatorDefaultLogger()) {
			if(e.getPlayer().isOp()) {
				ReaxtLog.logUser.add(e.getPlayer().getName());
				ReaxtLog.info(e.getPlayer().getName() + " joined the log mode.");
			}
		}
	}
	
	HashMap<String, Long> teleported = new HashMap<>();
	
	@EventHandler
	public void on(PlayerToggleSneakEvent e) {

		if(ReaxtLog.logUser.contains(e.getPlayer().getName()) && (!teleported.containsKey(e.getPlayer().getName()) || (teleported.containsKey(e.getPlayer().getName()) && System.currentTimeMillis()-teleported.get(e.getPlayer().getName()) >= 5000)) &&  System.currentTimeMillis()-ReaxtLog.getLatestLocTime() <=5000 ) {
			
			teleported.put(e.getPlayer().getName(), System.currentTimeMillis());
			

			Location latest = ReaxtLog.getLatestLoc();
			if(latest != null) {
				
				e.getPlayer().teleport(latest);
				e.getPlayer().sendMessage(Reaxt.prefix + "Teleported to the latest lag event.");
				
			}

		}
		
	}
	
	@EventHandler
	public void on(PlayerFormRespondedEvent e) {
		
		if(e.getResponse() != null) {
			
			if(e.getWindow() instanceof FormWindowCustom) {
				
				FormWindowCustom fw = (FormWindowCustom) e.getWindow();
				
				if(e.getFormID() == "reaxtplayer".hashCode()) {
					
					String player = fw.getTitle().replace(Reaxt.prefix, "");
					Config config = new Config(new File(Reaxt.get().getDataFolder() + "/logs/players", player + ".yml"));
					List<String> data = config.getStringList("events");
					FormWindowSimple log = new FormWindowSimple(Reaxt.prefix + player + "'s logs", "");
					String allowed = "";
					if(fw.getResponse().getToggleResponse(1)) allowed += "Chunk Entity Overflow|";
					if(fw.getResponse().getToggleResponse(2)) allowed += "World Entity Overflow|";
					if(fw.getResponse().getToggleResponse(3)) allowed += "Entity Stack Detection|";
					if(fw.getResponse().getToggleResponse(4)) allowed += "Explosion Limit Overflow|";
					if(fw.getResponse().getToggleResponse(5)) allowed += "Redstone Limit Overflow|";
					if(fw.getResponse().getToggleResponse(6)) allowed += "Block Update Overflow|";
					allowed += "deadstring";
					int entrys = 0;
					String desc = "";
					for(String s : data) {
						if(s.matches("(?i)(.*("+allowed+").*)")){
							desc += "§b" + s.split("] ")[0] + "] §f" + s.split("] ")[1] +"\n";
							entrys++;
						}
					}
					if(!desc.equals("")) {
					log.setContent("§fFound §9" + entrys + " §fEntrys according to your search results!\n\n" + desc);
					e.getPlayer().showFormWindow(log);
					} else e.getPlayer().sendMessage(Reaxt.prefix + "§cCould not find any results to your request.");
					
					
				}  
				
			} else if(e.getWindow() instanceof FormWindowSimple) {
				
				FormWindowSimple fw = (FormWindowSimple) e.getWindow();
				if(e.getFormID() == "reaxt_top".hashCode()) {
					
				
					Server.getInstance().dispatchCommand(e.getPlayer(), "/reaxt player " + fw.getResponse().getClickedButton().getText().split(" §8|§9 ")[0].replace("§f", ""));
					
				}
				
				
			}
			
		}
		
	}
	
}
