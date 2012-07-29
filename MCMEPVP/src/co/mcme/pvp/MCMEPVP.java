package co.mcme.pvp;
/**
 * @author doebi
 * @author Fawkes07
 * @author Henry Slawniak
 */
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class MCMEPVP extends JavaPlugin {
    //create a HashMap and a count for player teams

    public static HashMap<String, String> PlayerTeams;
    public static HashMap<String, Inventory> Classes;
    public static HashMap<String, Vector> Spawns;
    public static int BlueMates;
    public static int RedMates;
    public static int GameStatus;
    public static int GameMode;
    public static int Participants;
    public static String[] GameModes = {"TDM", "FFA"};
    public static GameType[] GameTypes = GameType.values();

    //onEnable, run when server starts
    @Override
    public void onEnable() {
        //registering Listener
        getServer().getPluginManager().registerEvents(new MCMEPVPListener(this), this);
        Classes = new HashMap<String, Inventory>();
        Spawns = new HashMap<String, Vector>();
        //load spawns into HashMap
        Spawns.put("blue", (Vector) this.getConfig().get("spawns.blue"));
        Spawns.put("red", (Vector) this.getConfig().get("spawns.red"));
        Spawns.put("spectator", (Vector) this.getConfig().get("spawns.spectator"));
        resetGame();
    }

    public static void addTeam(Player player, String Team) {
        //clear Inventory
        player.getInventory().clear();
        if (Team.equals("spectator")) {
            //TODO player.setFlying(true);
            PlayerTeams.put(player.getName(), "spectator");
            setName(player, ChatColor.WHITE);
            player.getInventory().setArmorContents(null);
        } else {
            //TODO gives error :( 
            //player.setFlying(false);
            player.sendMessage(ChatColor.YELLOW
                    + "You're now in Team "
                    + Team.toUpperCase() + "!");
            if (Team.equals("red")) {
                RedMates++;
                PlayerTeams.put(player.getName(), "red");
                setName(player, ChatColor.RED);
                player.getInventory().setHelmet(new ItemStack(35, 1, (short) 0, (byte) 14));
                player.getInventory().addItem(new ItemStack(358, 1, (short) 0, (byte) 1));
            } else {
                if (Team.equals("blue")) {
                    BlueMates++;
                    PlayerTeams.put(player.getName(), "blue");
                    setName(player, ChatColor.BLUE);
                    player.getInventory().setHelmet(new ItemStack(35, 1, (short) 0, (byte) 11));
                    player.getInventory().addItem(new ItemStack(358, 1, (short) 0, (byte) 2));
                }
            }
        }
    }

    public static void removeTeam(Player player) {
        //set player Spectator
        String Team = PlayerTeams.get(player.getName());
        addTeam(player, "spectator");
        //clear Inventory
        player.getInventory().clear();
        //adjust Playercounter
        if (Team.equals("red")) {
            //showStats();
            RedMates--;
        } else {
            if (Team.equals("blue")) {
                //showStats();
                BlueMates--;
            }
        }
    }

    public static void sendToTeam(String Message, Player chatter) {
        if (Message.startsWith("u00a")) {
            //player has WorldEdit CUI
        } else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                String Team = PlayerTeams.get(chatter.getName());
                String PlayerTeam = PlayerTeams.get(player.getName());
                String label = "[Jerk] ";
                if (Team.equals("red")) {
                    label = ChatColor.RED + "[Team Red] ";
                }
                if (Team.equals("blue")) {
                    label = ChatColor.BLUE + "[Team Blue] ";
                }
                if (Team.equals("spectator")) {
                    label = "[Spectator] ";
                }
                if (Team.equals("participant")) {
                    label = ChatColor.AQUA + "[Participant] ";
                }
                if (PlayerTeam.equals("spectator")
                        || PlayerTeam.equals("participant")
                        || PlayerTeam.equals(Team)) {
                    player.sendMessage(label + chatter.getName() + ": " + ChatColor.WHITE + Message);
                }
            }
        }
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
                    if (GameStatus != 1) {
                        String Team = PlayerTeams.get(player.getName());
                        //Check if player has a team already
                        if (Team.equals("participant")) {
                            player.sendMessage(ChatColor.DARK_RED
                                    + "You are already participating in the PVP Event!");
                        } else {
                            //queue Player
                            queuePlayer(player);
                        }
                        return true;
                    } else {
                        player.sendMessage(ChatColor.DARK_RED
                                + "You can't join a running Event!");
                        return true;
                    }
                }
                //CLASS
				/*
                if(method.equalsIgnoreCase("class")){
                String classname = args[1];
                //Allow mods to create classes by storing their inventory in a hashmap
                //with the class as the key
                //set a player's inventory to that of the given class if it exists
                Inventory classinventory = Classes.get(classname);
                if(classinventory == null){
                player.sendMessage(ChatColor.DARK_RED + "No such class '" + classname + "'!");
                }
                else{
                player.getInventory().clear();
                ItemStack[] contents = classinventory.getContents();
                player.getInventory().setContents(contents);
                }
                return true;
                }*/
                //START
                if (method.equalsIgnoreCase("start")) {
                    if (player.hasPermission("mcmepvp.admin")) {
                        if (Participants >= 2) {
                            startGame();
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
                            if (args[1].equalsIgnoreCase("class")) {
                                if (args.length < 3) {
                                    player.sendMessage(ChatColor.DARK_RED
                                            + "Provide a name for the class you want to create!");
                                    return true;
                                } else {
                                    String classname = args[2];
                                    PlayerInventory inv = player.getInventory();
                                    Classes.put(classname, inv);
                                    player.sendMessage(ChatColor.YELLOW
                                            + "Saved your inventory as Class '"
                                            + classname + "'!");
                                    return true;
                                }
                            }
                            if (args[1].equalsIgnoreCase("blue")) {
                                Vector loc = player.getLocation().toVector();
                                Spawns.put("blue", loc);
                                this.getConfig().set("spawns.blue", loc);
                                this.saveConfig();
                                player.sendMessage(ChatColor.YELLOW
                                        + "Saved your Location as Team Blue's Spawn!");
                                return true;
                            }
                            if (args[1].equalsIgnoreCase("red")) {
                                Vector loc = player.getLocation().toVector();
                                Spawns.put("red", loc);
                                this.getConfig().set("spawns.red", loc);
                                this.saveConfig();
                                player.sendMessage(ChatColor.YELLOW
                                        + "Saved your Location as Team Red's Spawn!");
                                return true;
                            }
                            if (args[1].equalsIgnoreCase("spectator")) {
                                Vector loc = player.getLocation().toVector();
                                Spawns.put("spectator", loc);
                                this.getConfig().set("spawns.spectator", loc);
                                this.saveConfig();
                                player.sendMessage(ChatColor.YELLOW
                                        + "Saved your Location as Spectator's Spawn!");
                                return true;
                            }
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
                        Bukkit.getServer().broadcastMessage(ChatColor.GREEN
                                + "The PVP Event has been aborted by an admin!");
                        resetGame();
                        return true;
                    }
                }
            }
        }
        if (cmd.getName().equalsIgnoreCase("shout")) {
            if (args.length != 0) {
                String msg = args[0];
                if (args.length > 1) {
                    for (int i = 1; i < args.length; i++) {
                        msg += " " + args[i];
                    }
                }
                if (PlayerTeams.get(player.getName()).equals("spectator")) {
                    player.sendMessage(ChatColor.DARK_RED
                            + "Spectators aren't allowed to shout!");
                } else {
                    Bukkit.getServer().broadcastMessage(ChatColor.GRAY
                            + player.getName() + " shouts: " + msg);
                }
                return true;
            }
        }
        return false;
    }

    static void resetGame() {
        BlueMates = 0;
        RedMates = 0;
        Participants = 0;
        PlayerTeams = new HashMap<String, String>();
        //Set Every Player as Spectator
        Vector vec = Spawns.get("spectator");
        //TODO delay the teleportation when game ends
        World world = Bukkit.getWorld("pvp");
        Location loc = new Location(world, vec.getX(), vec.getY(), vec.getZ());
        for (Player currentplayer : Bukkit.getOnlinePlayers()) {
            addTeam(currentplayer, "spectator");
            if (GameStatus == 1) {
                currentplayer.teleport(loc);
            }
        }
        GameStatus = 0;
        GameMode = 0;
    }

    private void queuePlayer(Player player) {
        player.setPlayerListName(ChatColor.AQUA + player.getName());
        Participants++;
        PlayerTeams.put(player.getName(), "participant");
        player.sendMessage(ChatColor.YELLOW
                + "You are participating! Wait for the next Event to start!");
        if (Bukkit.getServer().getOnlinePlayers().length == Participants && Participants > 1) {
            startGame();
        }
    }

    private void startGame() {
        GameStatus = 1;
        //Broadcast
        Bukkit.getServer().broadcastMessage(ChatColor.GREEN
                + "The PVP Event starts in a few seconds! GameMode is "
                + GameModes[GameMode] + "!");
        Bukkit.getServer().broadcastMessage(ChatColor.GREEN
                + "All Participants will be assigned to a team and teleported to their spawn!");
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

            public void run() {
                for (Player user : Bukkit.getOnlinePlayers()) {
                    //TODO team assigning more random
                    //Assign Teams
                    if (PlayerTeams.get(user.getName()).equals("participant")) {
                        if (BlueMates > RedMates) {
                            addTeam(user, "red");
                        } else {
                            if (BlueMates < RedMates) {
                                addTeam(user, "blue");
                            } else {
                                boolean random = (Math.random() < 0.5);
                                if (random == true) {
                                    addTeam(user, "red");
                                } else {
                                    addTeam(user, "blue");
                                }
                            }
                        }
                        //heal
                        user.setHealth(20);
                        user.setFoodLevel(20);
                        //Give Weapons and Armour
                        PlayerInventory inv = user.getInventory();
                        inv.setItemInHand(new ItemStack(267));//Sword
                        inv.setChestplate(new ItemStack(307));//Armour
                        inv.setLeggings(new ItemStack(308));//Leggins
                        inv.setBoots(new ItemStack(309));//Boots
                        inv.addItem(new ItemStack(261), new ItemStack(262, 32));//Bow + Arrows
                        inv.addItem(new ItemStack(333));
                        inv.addItem(new ItemStack(364, 2));
                    }
                    //Teleport User
                    Vector vec = Spawns.get(PlayerTeams.get(user.getName()));
                    World world = Bukkit.getWorld("pvp");
                    Location loc = new Location(world, vec.getX(), vec.getY(), vec.getZ());
                    user.teleport(loc);
                }
                //Broadcast
                Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "The Fight begins!");
            }
        }, 100L);
    }

    public static void setName(Player pl, ChatColor color) {
        String oldname = pl.getName();
        String newname = oldname;
        if (pl.getName().length() == 15) {
            int len = oldname.length();
            int removechars = len - 1;
            newname = oldname.substring(0, removechars);
        }
        if (pl.getName().length() == 16) {
            int len = oldname.length();
            int removechars = len - 2;
            newname = oldname.substring(0, removechars);
        }
        pl.setPlayerListName(color + newname);
        pl.setDisplayName(color + newname);
    }
    public String getPlayerTeam(Player player){
        String playername = player.getName();
        return PlayerTeams.get(playername);
    }
}
