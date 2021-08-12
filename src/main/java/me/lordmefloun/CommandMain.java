package me.lordmefloun;

import me.lordmefloun.LordMefloun;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class CommandMain implements CommandExecutor {


    FileConfiguration config = LordMefloun.getPlugin(LordMefloun.class).getConfig();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {


        if (!(sender instanceof Player)){
            sender.sendMessage("VRR");
            return false;
        }

        Player p = (Player) sender;

        if (args.length == 0){
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&m-----------------------------------------------------"));
            p.sendMessage("");
            LordMefloun.sendCenteredMessage(p, "&b&lDiscord Linking");
            p.sendMessage("");
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "          &b/dlink link &8- &eLink your discord account with minecraft"));
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "          &b/dlink sync &8- &eSync your discord role"));
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "          &b/dlink unlink &8- &eUnlink your discord"));
            p.sendMessage("");
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&m-----------------------------------------------------"));
            return true;
        }

        if (args[0].equalsIgnoreCase("link")){
            if(!LordMefloun.Hooking.containsKey(p.getUniqueId().toString())){
                if (LordMefloun.VerifyCodes.containsKey(p)){
                    ArrayList<String> str = new ArrayList<String>();
                    str.add("&eWe've already generated code for you!!");
                    str.add("&ego to #" + config.getString("channelName") + " channel in our discord server");
                    str.add("&eand type command &c?link " + LordMefloun.VerifyCodes.get(p));
                    LordMefloun.sendEmbedMessage(p, str);
                }
                else{
                    Random rand = new Random();

                    int r = (rand.nextInt(10000) + 10000);
                    String code = Integer.toString(r);

                    LordMefloun.VerifyCodes.put(p, code);

                    ArrayList<String> str = new ArrayList<String>();
                    str.add("&eWe've generated code for you,");
                    str.add("&ego to &b#" + config.getString("channelName") + " &echannel in our discord server");
                    str.add("&eand type command &b?link " + LordMefloun.VerifyCodes.get(p));
                    LordMefloun.sendEmbedMessage(p, str);
                }
            }
            else{
                ArrayList<String> str = new ArrayList<String>();
                str.add("&cYour account has been already linked!");
                LordMefloun.sendEmbedMessage(p, str);
            }
            return false;

        }
        if (args[0].equalsIgnoreCase("unlink")){

            if(LordMefloun.Hooking.containsKey(p.getUniqueId().toString())){
                User discord = LordMefloun.getDiscord(p);
                Guild guild = LordMefloun.jda.getGuildById(config.getString("guildID"));
                Member member = guild.retrieveMember(discord).complete();

                LordMefloun.data.getConfig().set("links." + p.getUniqueId().toString(), null);
                LordMefloun.data.saveConfig();


                List<Role> rolestoremove = new ArrayList<>();

                for(String rolename: config.getStringList("groups")){

                    rolestoremove.add(guild.getRolesByName(rolename, true).get(0));

                }

                guild.modifyMemberRoles(member, null, rolestoremove).queue();
                LordMefloun.Hooking.remove(p.getUniqueId().toString());
                ArrayList<String> str = new ArrayList<String>();
                str.add("&aYour account has been");
                str.add("&2unlinked&a link it by typing command");
                str.add("&2/dlink link");
                LordMefloun.sendEmbedMessage(p, str);
            }
            else {
                ArrayList<String> str = new ArrayList<String>();
                str.add("&cYour account hasn't been yet");
                str.add("&4linked&c link it by typing command");
                str.add("&4/dlink link");
                LordMefloun.sendEmbedMessage(p, str);
            }
            return false;
        }
        if (args[0].equalsIgnoreCase("sync")){
            if (!p.isOp()){
                if (!p.hasPermission("*")){
                    if(LordMefloun.Hooking.containsKey(p.getUniqueId().toString())){
                        LordMefloun.Synchronize(p, config);
                        ArrayList<String> str = new ArrayList<String>();
                        str.add("&eWe've synchronized role with your");
                        str.add("&eingame group, check out your new benefits!");
                        LordMefloun.sendEmbedMessage(p, str);


                        EmbedBuilder eb = new EmbedBuilder();

                        User discord = LordMefloun.getDiscord(p);

                        eb.setAuthor(discord.getName(), null, discord.getAvatarUrl());
                        eb.setColor(Color.RED);
                        eb.setFooter(LordMefloun.jda.getGuildById(config.getString("guildID")).getName());
                        eb.setTitle("Synchronizing");




                        eb.setColor(Color.GREEN);
                        eb.setDescription("Synchronized data for: " + LordMefloun.getMentionFromUser(LordMefloun.getDiscord(p)));
                        LordMefloun.chatChannel.sendMessage(eb.build()).queue();
                    }
                    else{
                        ArrayList<String> str = new ArrayList<String>();
                        str.add("&cYour account hasn't been yet");
                        str.add("&4linked&c link it by typing command");
                        str.add("&4/dlink link");
                        LordMefloun.sendEmbedMessage(p, str);
                    }
                }
                else{
                    p.sendMessage(ChatColor.RED + "I'm unable to get your role because you have * permission");
                }
            }
            else{
                p.sendMessage(ChatColor.RED + "I'm unable to get your role because you are OP");
            }
            return false;

        }


        p.performCommand("dlink");

        return false;
    }

    public static String getPlayerGroup(Player player, Collection<String> possibleGroups) {
        for (String group : possibleGroups) {
            if (player.hasPermission("group." + group)) {
                return group;
            }
        }
        return null;
    }
}
