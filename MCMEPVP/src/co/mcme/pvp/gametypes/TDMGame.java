package co.mcme.pvp.gametypes;

import co.mcme.pvp.GameType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import co.mcme.pvp.Game;

public class TDMGame extends Game{
    public GameType GameType;

    public TDMGame(GameType gt) {
        super(gt);
        //TODO: Code on Game Start
    }

	public void onPlayerjoinServer(PlayerLoginEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void onPlayerleaveServer(PlayerQuitEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void onPlayerchat(PlayerChatEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void onPlayerdie(PlayerDeathEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void onPlayerhit(EntityDamageByEntityEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void onPlayerjoinGame(Player player) {
		// TODO Auto-generated method stub
		
	}

	public void onAdminset(String[] args) {
		// TODO Auto-generated method stub
		
	}
    
    
}