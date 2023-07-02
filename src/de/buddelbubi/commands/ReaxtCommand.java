package de.buddelbubi.commands;

import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.element.ElementToggle;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.utils.Config;
import de.buddelbubi.Reaxt;
import de.buddelbubi.utils.ReaxtApi;
import de.buddelbubi.utils.ReaxtLog;

public class ReaxtCommand extends Command{

	public ReaxtCommand(String name) {
		super(name);
		String[] sub = new String[] {"logger", "player#Player", "top"};
		
		for(String subcommand : sub) {
			String[] args = subcommand.split("#");
			String subname = args[0];
			LinkedList<CommandParameter> parameters = new LinkedList<>();
			parameters.add(CommandParameter.newEnum(subname, new String[] {subname}));
			if(args.length > 1) {
				for(int i = 1; i < args.length; i++) {
					parameters.add(CommandParameter.newType(args[i], false, CommandParamType.TARGET));
				}
			}
			this.commandParameters.put(subcommand, parameters.toArray(new CommandParameter[parameters.size()]));
		}
	}

	@Override
	public boolean execute(CommandSender arg0, String arg1, String[] args) {
		
		if(args.length >= 1) {
			
			if(args[0].equalsIgnoreCase("logger")) {
				
				if(arg0.hasPermission("reaxt.logger")) {
				
					if(arg0 instanceof Player) {
						if(ReaxtLog.logUser.contains(arg0.getName())) {
							ReaxtLog.info(arg0.getName() + " left the Log Mode.");
							ReaxtLog.logUser.remove(arg0.getName());
						} else {
							ReaxtLog.logUser.add(arg0.getName());
							ReaxtLog.info(arg0.getName() + " entered the Log Mode.");
						}
					} else arg0.sendMessage(Reaxt.prefix + "Console is always in Log Mode.");
				} else arg0.sendMessage(Reaxt.prefix + "You are lacking the permission 'reaxt.logger'.");
				
				
			} else if(args[0].equalsIgnoreCase("player")) {
				
				if(arg0.hasPermission("reaxt.player")) {
					
					if(args.length == 2) {
						
						if(arg0 instanceof Player) {
							
							File file = new File(Reaxt.get().getDataFolder() + "/logs/players", args[1] + ".yml");
							if(file.exists()) {
								
								FormWindowCustom fw = new FormWindowCustom(Reaxt.prefix + args[1]);
								List<String> badstuff = new Config(file).getStringList("events");
								fw.addElement(new ElementLabel("This user was near " + badstuff.size() + " Reaxt triggering Events.\nWhich of them do you want to see?"));
								fw.addElement(new ElementToggle("Chunk Entity Overflow", true));
								fw.addElement(new ElementToggle("World Entity Overflow", true));
								fw.addElement(new ElementToggle("Entity Stack Detection", true));
								fw.addElement(new ElementToggle("Explosion Limit Overflow", true));
								fw.addElement(new ElementToggle("Redstone Event Overflow", true));
								fw.addElement(new ElementToggle("Block Update Overflow", true));
								
								Player p = (Player) arg0;
								p.showFormWindow(fw, "reaxtplayer".hashCode());
								
							} else arg0.sendMessage(Reaxt.prefix + "§aThis user has a clean slate!");
							
						} else arg0.sendMessage(Reaxt.prefix + "§cThis command can only be used ingame!");
						
					} else arg0.sendMessage(Reaxt.prefix + "§cDo /reaxt player (Player)");
			
				} else arg0.sendMessage(Reaxt.prefix + "You are lacking the permission 'reaxt.player'.");
				
			} else if(args[0].equalsIgnoreCase("top")) {
				
				if(arg0.hasPermission("reaxt.top")) {
					
					if(args.length == 1 || args.length == 2) {
						File folder =  new File(Reaxt.get().getDataFolder() + "/logs/players");
						HashMap<String, Integer> hm = new HashMap<>();
						int limit = folder.listFiles().length;
						FormWindowSimple fw = new FormWindowSimple(Reaxt.prefix + "\"Top §f" + limit + "§7 Lag responibles.", "");
						
						if(args.length == 2) {
							try {
								limit = Integer.parseInt(args[1]);
							} catch (Exception e) {
								 arg0.sendMessage(Reaxt.prefix + "§cYour index has to be an integer.");
								return false;
							}
						}
						if(limit > folder.listFiles().length) {
							arg0.sendMessage(Reaxt.prefix + "§cYour index is greater than the total player count.");
							return false;
						}
						
						for(File f : folder.listFiles()) {
							if(f.getName().contains(".yml")) {
								Config c = new Config(f);
								int amount = c.getStringList("events").size();
								hm.put(f.getName().replace(".yml", ""), amount);
							}
						}
						Map<String, Integer> sortedMap = 
							     hm.entrySet().stream()
							    .sorted(Entry.comparingByValue(Comparator.reverseOrder()))
							    .collect(Collectors.toMap(Entry::getKey, Entry::getValue,
							                              (e1, e2) -> e1, LinkedHashMap::new));
						String msg = "";
						int total = 0;
						int index = 0;
						for(Map.Entry<String, Integer> ent : sortedMap.entrySet()) {
							if(index < limit) {
							index++;
							String row = "§f" + ent.getKey() + " §8|§9 " + ent.getValue() + " entrys";
							msg += (row + "\n");
							fw.addButton(new ElementButton(row));
							total += ent.getValue();
							} else break;
						}
						
						if(arg0 instanceof Player) {
							
							fw.setContent("§oThey are responsible for " + total + " lag events.\n");
							
							((Player) arg0).showFormWindow(fw, "reaxt_top".hashCode());
							
						} else {
							arg0.sendMessage(Reaxt.prefix + "Top §f" + limit + "§7 Lag responibles. §9(" + total  +" entrys)\n" + msg);
						}
						
					} else arg0.sendMessage(Reaxt.prefix + "§cDo /reaxt top [limit]*");
					
				} else arg0.sendMessage(Reaxt.prefix + "You are lacking the permission 'reaxt.top'.");
				
			} else if( args[0].equalsIgnoreCase("save")) {
				
				if(arg0.hasPermission("reaxt.save")) {
					
					if(ReaxtApi.shouldLogEvents()) {
						if(ReaxtLog.getEventLog().split("\n").length >= 4) {
						
							String file = Reaxt.saveLog();
							ReaxtLog.build();
							arg0.sendMessage(Reaxt.prefix + "§aSaved Reaxt Log as §e" + file + " §a. Starting a new log from log on!");
						
						} else arg0.sendMessage(Reaxt.prefix + "§cThe current log is empty!");
					} else arg0.sendMessage(Reaxt.prefix + "§cLogging is disabled on this server!");
				
				} else arg0.sendMessage(Reaxt.prefix + "You are lacking the permission 'reaxt.save'.");
				
			} else if( args[0].equalsIgnoreCase("current")) {
				
				if(arg0.hasPermission("reaxt.current")) {
					
					if(ReaxtApi.shouldLogEvents()) {
						if(ReaxtLog.getEventLog().split("\n").length >= 4) {
						
							if(arg0 instanceof Player) {
								FormWindowSimple fw = new FormWindowSimple(Reaxt.prefix + "Current Log", ReaxtLog.getEventLog());
								((Player) arg0).showFormWindow(fw, "reaxt_cur_log".hashCode());
							} else System.out.println("\n\n" + ReaxtLog.getEventLog());
							
						
						} else arg0.sendMessage(Reaxt.prefix + "§cThe current log is empty!");
					} else arg0.sendMessage(Reaxt.prefix + "§cLogging is disabled on this server!");
				
			} else arg0.sendMessage(Reaxt.prefix + "You are lacking the permission 'reaxt.current'.");
				
			}
			
		}
		
		return false;
	}

}
