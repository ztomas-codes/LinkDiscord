package me.lordmefloun;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;

public class onJoinEvent implements Listener {

    FileConfiguration config = LordMefloun.getPlugin(LordMefloun.class).getConfig();


    @EventHandler
    public void onJoinEvent(PlayerJoinEvent e){

        Player p = e.getPlayer();
        if(LordMefloun.Hooking.containsKey(p.getUniqueId().toString())) {
            if (LordMefloun.getDiscord(p) != null) {
                if (!p.isOp())
                    if (!p.hasPermission("*"))
                        LordMefloun.Synchronize(p, config);
            }
        }
        else {
            ArrayList<String> str = new ArrayList<String>();
            str.add("&eYou haven't yet linked your account");
            str.add("&ewith your discord account");
            str.add("&etype &b/dlink link&e for link");
            LordMefloun.sendEmbedMessage(p, str);
        }
    }
}
