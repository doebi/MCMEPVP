package co.mcme.pvp;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
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

public class MCMEPVPListener implements Listener {

    public MCMEPVPListener(MCMEPVP instance) {
    }

    @EventHandler(priority = EventPriority.HIGH)
    void onPlayerJoin(final PlayerLoginEvent event) {
		MCMEPVP.PlayerStatus.put(event.getPlayer().getName(), "spectator");
    	if(MCMEPVP.GameStatus == 0){
    		//TODO General code when no Game is running
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
    	}
    }

    @EventHandler(priority = EventPriority.HIGH)
    void onPlayerChat(final AsyncPlayerChatEvent event) {
    	if(MCMEPVP.GameStatus == 0){
    		//TODO General code when no Game is running
    	}else{
    		MCMEPVP.CurrentGame.onPlayerchat(event);
    	}
    }

    @EventHandler(priority = EventPriority.HIGH)
    void onPlayerDeath(final PlayerDeathEvent event) {
    	if(MCMEPVP.GameStatus == 0){
    		//TODO General code when no Game is running
    	}else{
    		MCMEPVP.CurrentGame.onPlayerdie(event);
    	}
    }

    @EventHandler(priority = EventPriority.HIGH)
    void onPlayerDamage(final EntityDamageByEntityEvent event) {
    	if(event.getDamager().getType().equals(EntityType.PLAYER) && event.getEntity().getType().equals(EntityType.PLAYER)){
        	if(MCMEPVP.GameStatus == 0){
        		//TODO General code when no Game is running
        	}else{
        		MCMEPVP.CurrentGame.onPlayerhit(event);
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