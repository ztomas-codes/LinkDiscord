package me.lordmefloun.discord;

import me.lordmefloun.LordMefloun;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;

public class MessageListener extends ListenerAdapter {

    FileConfiguration config = LordMefloun.getPlugin(LordMefloun.class).getConfig();

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent e) {


        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(e.getAuthor().getName(), null, e.getAuthor().getAvatarUrl());
        eb.setColor(Color.RED);
        eb.setFooter(e.getGuild().getName() ,e.getGuild().getIconUrl());
        eb.setTitle("Linking minecraft account");

        String[] message = e.getMessage().getContentRaw().split( " ");

        if(message[0].equalsIgnoreCase("?link")){
            if(message.length == 2){

                String code = message[1];
                Player p = (Player) LordMefloun.getKeyFromValue(LordMefloun.VerifyCodes, code);

                if(p == null){
                    eb.setDescription("<@" + e.getAuthor().getId() + ">" + " you don't have or it's wrong authentication code");
                    LordMefloun.chatChannel.sendMessage(eb.build()).queue();
                    return;
                }

                LordMefloun.VerifyCodes.remove(p);

                ArrayList<String> str = new ArrayList<String>();
                str.add("&aSuccessfully linked your minecraft account");
                str.add("&awith your discord account!");
                str.add("&aType &b/dlink sync&a to synchronize role");
                LordMefloun.sendEmbedMessage(p, str);


                long id = e.getAuthor().getIdLong();
                LordMefloun.Hooking.put(p.getUniqueId().toString(), id);
                eb.setColor(Color.GREEN);
                eb.setDescription(LordMefloun.getMentionFromUser(LordMefloun.getDiscord(p))+ ", your account has been linked with your minecraft account");
                LordMefloun.chatChannel.sendMessage(eb.build()).queue();




            }
            else{
                eb.setDescription("<@" + e.getAuthor().getId() + ">" + " wrong use of command ?link <authentication code>");
                LordMefloun.chatChannel.sendMessage(eb.build()).queue();
            }
        }
    }
}
