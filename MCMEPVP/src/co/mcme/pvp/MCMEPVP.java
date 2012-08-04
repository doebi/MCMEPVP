package co.mcme.pvp;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import co.mcme.pvp.gametypes.TDMGame;

public class MCMEPVP extends JavaPlugin {

    public static Game CurrentGame;
    public static HashMap<String, String> PlayerStatus;
    public static int GameStatus;
    public static GameType[] GameTypes = GameType.values();
    public static String[] Gamers;

    //onEnable, run when server starts
    @Override
    public void onEnable() {
        //registering Listener
        getServer().getPluginManager().registerEvents(new MCMEPVPListener(this), this);
        resetGame();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        //Identify the player
        final Player player = sender.getServer().getPlayer(sender.getName());
        if (cmd.getName().equalsIgnoreCase("pvp")) {
            //What to do when a player types /pvp
            if (args.length != 0) {
                String method = args[0];
                //JOIN
                if (method.equalsIgnoreCase("join")) {
                    if (GameStatus == 0) {
                        //Check if player is participating already
                        if (PlayerStatus.get(player.getName()) != null) {
                            player.sendMessage(ChatColor.DARK_RED
                                    + "You are already participating in the PVP Event!");
                        } else {
                            CurrentGame.onPlayerjoinGame(player);
                        }
                        return true;
                    } else {
                        player.sendMessage(ChatColor.DARK_RED
                                + "You can't join a running Event!");
                        return true;
                    }
                }
                //START
                if (method.equalsIgnoreCase("start")) {
                    if (player.hasPermission("mcmepvp.admin")) {
                        if (Gamers.length >= 2) {
                        	if(args[1] == "TDM"){
                                CurrentGame = new TDMGame(null);
                        	}else{
                                CurrentGame = new Game(null);
                        	}
                            return true;
                        } else {
                            player.sendMessage(ChatColor.DARK_RED
                                    + "There need to be at least two participants!");
                            return true;
                        }
                    } else {
                        player.sendMessage(ChatColor.DARK_RED
                                + "You're not an Admin!");
                        return true;
                    }
                }
                //SET
                if (method.equalsIgnoreCase("set")) {
                    if (player.hasPermission("mcmepvp.admin")) {
                        if (args.length > 1) {
                        	CurrentGame.onAdminset(args);
                        } else {
                            player.sendMessage("/pvp set [blue|red|spectator|class]");
                            return true;
                        }
                    } else {
                        player.sendMessage(ChatColor.DARK_RED + "You're not an Admin!");
                        return true;
                    }
                }
                //STOP
                if (method.equalsIgnoreCase("stop")) {
                    if (player.hasPermission("mcmepvp.admin")) {
                        Bukkit.getServer().broadcastMessage(ChatColor.YELLOW
                                + "The PVP Event has been aborted by an admin!");
                        resetGame();
                        return true;
                    }
                }
            }
        }
        if (cmd.getName().equalsIgnoreCase("shout")) {
        	
        }
        return false;
    }

    static void resetGame() {
    	//TODO
    }

}
