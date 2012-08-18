package co.mcme.pvp;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import co.mcme.pvp.gametypes.TDMGame;

public class MCMEPVP extends JavaPlugin {

    public static Game CurrentGame;
    public static HashMap<String, String> PlayerStatus;
    public static int GameStatus;
    public static GameType[] GameTypes = GameType.values();
    public static World PVPWorld;
    public static HashMap<String, Vector> Spawns;

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
                        if (PlayerStatus.get(player.getName()) != "spectator") {
                            player.sendMessage(ChatColor.DARK_RED
                                    + "You are already participating in the next Game!");
                            return true;
                        } else {
                        	//queuePlayer
                        	setPlayerStatus(player, "participant", ChatColor.GREEN);
                        	player.sendMessage(ChatColor.YELLOW + "You are participating! Wait for the next Game to start!");
                            return true;
                        }
                    } else {
                        player.sendMessage(ChatColor.DARK_RED
                                + "You can't join a running Game!");
                        return true;
                    }
                }
                //START
                if (method.equalsIgnoreCase("start")) {
                    if (player.hasPermission("mcmepvp.admin")) {
                    	if(GameStatus == 0){
	                    	int check = 0;
	                    	for (Player cplayer : getServer().getOnlinePlayers()) {
	                    		if(PlayerStatus.get(cplayer.toString()) == "spectator"){
	                    			check++;
	                    		}
	                    	}
	                        if (check >= 2) {
	                        	startGame(args[1]);
	                        	MCMEPVP.PVPWorld = player.getWorld();
	                            return true;
	                        } else {
	                            player.sendMessage(ChatColor.DARK_RED
	                                    + "There need to be at least two participants!");
	                            return true;
	                        }
                    	} else {
                            player.sendMessage(ChatColor.DARK_RED
                                    + "Game already running!");
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

	public static void resetGame() {
            PlayerStatus = new HashMap<String, String>();
            Spawns = new HashMap<String, Vector>();
            for (Player currentplayer : Bukkit.getOnlinePlayers()) {
        	setPlayerStatus(currentplayer, "spectator", ChatColor.WHITE);
            }
        }
    
        void startGame(String gt){
            Spawns.put("blue", (Vector) this.getConfig().get("spawns.spectator"));
            Spawns.put("red", (Vector) this.getConfig().get("spawns.red"));
            Spawns.put("spectator", (Vector) this.getConfig().get("spawns.spectator"));
            if(gt == "TDM"){
                CurrentGame = new TDMGame(GameType.TDM);
            }else{

            }
        }

	public static void setPlayerStatus(Player player, String status, ChatColor NameColor) {
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		PlayerStatus.put(player.getName(), status);
		player.setPlayerListName(NameColor + player.getName());
		player.setDisplayName(NameColor + player.getName());
	}

	public static String getPlayerStatus(Player player) {
		String status = PlayerStatus.get(player.getName());
		return status;
	}
}
