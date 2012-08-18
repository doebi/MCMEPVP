package co.mcme.pvp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

public class MCMEPVPListener implements Listener {

    public MCMEPVPListener(MCMEPVP instance) {
    }

    @EventHandler(priority = EventPriority.HIGH)
    void onPlayerJoin(final PlayerLoginEvent event) {
		MCMEPVP.setPlayerStatus(event.getPlayer(), "spectator", ChatColor.WHITE);
    	if(MCMEPVP.GameStatus == 0){
	        Vector vec = MCMEPVP.Spawns.get("spectator");
	        Location loc = new Location(MCMEPVP.PVPWorld, vec.getX(), vec.getY(), vec.getZ());
        	event.getPlayer().teleport(loc);
    	}else{
    		MCMEPVP.CurrentGame.onPlayerjoinServer(event);
    	}
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    void onPlayerLeave(final PlayerQuitEvent event) {
    	if(MCMEPVP.GameStatus == 0){
    		//TODO General code when no Game is running
    	}else{
    		MCMEPVP.CurrentGame.onPlayerleaveServer(event);
    		MCMEPVP.setPlayerStatus(event.getPlayer(), "spectator", ChatColor.WHITE);
    	}
    }

    @EventHandler(priority = EventPriority.HIGH)
    void onPlayerChat(final AsyncPlayerChatEvent event) {
		event.setCancelled(true);
		if(event.getMessage().startsWith("u00a")){
			//player has WorldEdit CUI
		}else{
	        for (Player player : Bukkit.getOnlinePlayers()) {
	        	String Status = MCMEPVP.PlayerStatus.get(event.getPlayer().getName());
	        	String PlayerTeam = MCMEPVP.PlayerStatus.get(player.getName());
	        	String label = "[Jerk] ";
	        	if(Status == "red"){
	        		label = ChatColor.RED + "[Team Red] ";
	        	}
	        	if(Status == "blue"){
	        		label = ChatColor.BLUE + "[Team Blue] ";
	        	}
	        	if(Status == "spectator"){
	        		label = "[Spectator] ";
	        	}
	        	if(Status == "participant"){
	        		label = ChatColor.GREEN + "[Participant] ";
	        	}
	        	if(PlayerTeam == "spectator" || PlayerTeam == "participant" || PlayerTeam == Status){
	        		player.sendMessage(label + event.getPlayer().getName() + ": " + ChatColor.WHITE + event.getMessage());
	        	}
	        }
		}
    }

    @EventHandler(priority = EventPriority.HIGH)
    void onPlayerDeath(final PlayerDeathEvent event) {
        event.getDrops().clear();
    	if(MCMEPVP.GameStatus == 0){
    		//TODO General code when no Game is running
    	}else{
    		MCMEPVP.CurrentGame.onPlayerdie(event);
    	}
    }

    @EventHandler(priority = EventPriority.HIGH)
    void onPlayerDamage(final EntityDamageByEntityEvent event) {
        if(event.getEntity().getType().equals(EntityType.PLAYER)){
            Player Victim = (Player) event.getEntity();
            if(MCMEPVP.getPlayerStatus(Victim).equals("spectator")){
                event.setCancelled(true);
            }
            if(event.getDamager().getType().equals(EntityType.PLAYER)){
                if(MCMEPVP.GameStatus == 0){
                    event.setCancelled(true);
                }else{
                    MCMEPVP.CurrentGame.onPlayerhit(event);
                }
            }
        }
    }
    
    //TODO prevent player from taking off head
    public void onInventoryClick(InventoryClickEvent event) {
        HumanEntity player = event.getWhoClicked();
        if (event.getSlotType() == SlotType.ARMOR) {
            if (player.getInventory().getHelmet().getType().equals(Material.WOOL)) {
                //TODO send message to player on armor remove attempt
                event.setCancelled(true);
            }
        }
    }
}