package co.mcme.pvp;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SpectatorTools {
    
    public static void setSpectator(Player player){
        player.setAllowFlight(true);
        for (Player derps : Bukkit.getOnlinePlayers()){
            if (!MCMEPVP.PlayerTeams.get(derps.getName()).equals("spectator")){
                derps.hidePlayer(player);
            }
            player.showPlayer(derps);
        }
    }
}
