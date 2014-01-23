package com.weebly.turtleplays.ranks;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class Ranks extends JavaPlugin implements Listener {

    public void onEnable(){
        getLogger().info("Tickets has been enabled");
        getServer().getPluginManager().registerEvents(this, this);
        reloadRanks();
    }

    public void onDisable(){
        getLogger().info("Tickets has been disabled");
        saveConfig();
        saveRanks();
    }

    private FileConfiguration ranks = null;
    private File ranksFile = null;

    //reload ranks
    public void reloadRanks(){
        if(ranksFile==null){
            ranksFile = new File(getDataFolder(), "ranks.yml");
        }
        ranks = YamlConfiguration.loadConfiguration(ranksFile);
    }

    //get ranks
    public FileConfiguration getRanks() {
        if (ranksFile == null) {
            reloadRanks();
        }
        return ranks;
    }


    public void saveRanks() {
        if (ranks == null || ranksFile == null) {
            return;
        }
        try {
            getRanks().save(ranksFile);
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Could not save config to " + ranksFile, ex);
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e){
        Player player = e.getPlayer();
        String rank = this.getConfig().getString(player.getName());
        String chat = this.getRanks().getString(rank);
        if (chat!=null){
            e.setFormat("["+chat+"]§f"+player.getDisplayName()+": "+e.getMessage());
        }
    }


    @EventHandler
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        Player player = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("setrank")){
            if (args.length!=2){
                return false;
            }
            if (this.getRanks().getString(args[1])==null){
                player.sendMessage("This Rank Does not exist");
                return true;
            }
            this.getConfig().set(args[0], args[1]);
            player.sendMessage("§5You have set "+args[0] + "'s Rank to "+ args[1]);
            return true;
        }
        else if (cmd.getName().equalsIgnoreCase("newrank")){
            if (args.length!=2){
                return false;
            }
            if (this.getRanks().getString(args[0])!=null){
                player.sendMessage("This rank already exists");
                return true;
            }
            String text = args[1];
            String[] characters = text.split("");
            String output = "";
            if (text.contains("&")){
                for (int i=0;i<characters.length;i++){
                    if (characters[i].equals("&")){
                        output = output + "§";
                    }
                    else {
                        output = output + characters[i];
                    }
                }
            }
            player.sendMessage(output);
            this.getRanks().set(args[0], output);
            player.sendMessage("You have created the new rank: "+ args[0]);
            return true;
        }
        else if (cmd.getName().equalsIgnoreCase("delrank")){
            if (args.length!=1){
                return false;
            }
            this.getRanks().set(args[0], null);
            player.sendMessage("Rank "+ args[0]+ " deleted");
            return true;
        }
        else if (cmd.getName().equalsIgnoreCase("ranks")){
            for (String names : this.getRanks().getKeys(true)){
                player.sendMessage(names+" : "+this.getRanks().getString(names));
            }
            return true;
        }
        return false;
    }

}
