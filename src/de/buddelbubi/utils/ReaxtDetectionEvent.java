package de.buddelbubi.utils;

import java.util.Collection;
import java.util.Iterator;

import cn.nukkit.Player;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Location;

public class ReaxtDetectionEvent extends Event {
	
	 final Collection<Player> players;
	 final String cause;
	 final Long time;
	 final Location approxiatedloc;
	
	private static final HandlerList handlers = new HandlerList();
	
	public ReaxtDetectionEvent(Collection<Player> player, Location location,  String cause) {
	
		this.players = player;
		this.cause = cause;
		this.time = System.currentTimeMillis();
		if(location == null) {
			Iterator<Player> it = player.iterator();
	    	if(it.hasNext()) {
	    		Location loc = it.next().getLocation();
	        	while(it.hasNext()) {
	        		Player pl = it.next();
	        		loc.x = (loc.x+pl.x)/2d;
	        		loc.z = (loc.z+pl.z)/2d;
	        	}
	        	this.approxiatedloc = loc;
	    	} else approxiatedloc = null;
		} else approxiatedloc = location;
	}
	
	public Collection<Player> getPlayers() {
		return this.players;
	}
	
	public String getCause() {
		return this.cause;
	}
	
	public Long getTime() {
		return this.time;
	}

    public static HandlerList getHandlers() {
        return handlers;
    }
    
    public Location getApproximatedLocation() {
    	return this.approxiatedloc;
    }
	
}
