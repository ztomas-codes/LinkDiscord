package me.lordmefloun;

import lombok.SneakyThrows;
import me.lordmefloun.discord.MessageListener;
import me.lordmefloun.files.DataManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.managers.GuildManager;
import net.dv8tion.jda.api.requests.RestAction;
import org.apache.logging.log4j.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import sun.tools.jconsole.JConsole;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class LordMefloun extends JavaPlugin {

    private final static int CENTER_PX = 154;
    public static JDA jda;
    public static TextChannel chatChannel;

    public FileConfiguration config = getConfig();

    public static HashMap<String, Long> Hooking = new HashMap<String, Long>();
    public static HashMap<Player, String> VerifyCodes = new HashMap<Player, String>();

    public static DataManager data;


    @SneakyThrows
    @Override
    public void onEnable() {

        this.data = new DataManager(this);

        saveDefaultConfig();

        String dscToken = getConfig().getString("token");

        jda = JDABuilder.createDefault(dscToken)
                .build()
                .awaitReady();
        if (jda == null){
            getServer().getLogger().warning("Couldn't connect to discord bot, check config.yml");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        String chatChannelID = getConfig().getString("channelID");
        if (chatChannelID != null){
            chatChannel = jda.getTextChannelById(chatChannelID);
        }


        jda.addEventListener(new MessageListener());
        this.getCommand("dlink").setExecutor(new CommandMain());
        this.getServer().getPluginManager().registerEvents(new onJoinEvent(), this);


        data.saveDefaultConfig();

        if(data.getConfig().contains("links"))
            for(String key : data.getConfig().getConfigurationSection("links").getKeys(false)){
                Hooking.put(key , Long.parseLong( data.getConfig().getString("links." + key)));
            }

    }

    public void onDisable(){
        if (jda != null) jda.shutdownNow();

        if (!Hooking.isEmpty())
            for (Map.Entry<String, Long> entry : Hooking.entrySet()) {
                String key = entry.getKey();
                Long value = entry.getValue();
                data.getConfig().set("links." + key, value);

            }
        data.saveConfig();
    }


    public static String getMentionFromUser(User user){
        return "<@" + user.getId() + ">";
    }

    public static String getMentionFromMember(Member member){
        return "<@" + member.getId() + ">";
    }


    public static void Synchronize(Player p, FileConfiguration con){


        User discord = getDiscord(p);
        Guild guild = jda.getGuildById(con.getString("guildID"));
        Member member = guild.retrieveMember(discord).complete();

        List<Role> rolestoremove = new ArrayList<>();

        for(String rolename: con.getStringList("groups")){

            rolestoremove.add(guild.getRolesByName(rolename, true).get(0));

        }



        guild.modifyMemberRoles(member, null, rolestoremove).queue();
        guild.addRoleToMember(member, guild.getRolesByName(CommandMain.getPlayerGroup(p, con.getStringList("groups")), true).get(0)).completeAfter( 100, TimeUnit.MILLISECONDS);

    }

    public static void sendEmbedMessage(Player p, ArrayList<String> messages){
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&m-----------------------------------------------------"));
        p.sendMessage("");
        LordMefloun.sendCenteredMessage(p, "&b&lDiscord Linking");
        p.sendMessage("");
        for(String message : messages){
            LordMefloun.sendCenteredMessage(p, message);
        }
        p.sendMessage("");
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&m-----------------------------------------------------"));
    }

    public static User getDiscord(Player p){

        long id = Hooking.get(p.getUniqueId().toString());
        return jda.retrieveUserById(id).complete();

    }














    public void sendMessage(Player player, String content, boolean contentInAuthorLine, Color Color){
        if (chatChannel == null) return;

        EmbedBuilder builder = new EmbedBuilder()
                .setAuthor(
                        contentInAuthorLine ? content : player.getDisplayName(),
                        null,
                        "https://minotar.net/avatar/" + player.getName() + "/100.png"
                );
        if (!contentInAuthorLine){
            builder.setDescription(content);
        }

        chatChannel.sendMessage(builder.build()).queue();
     }

    public static Object getKeyFromValue(HashMap hm, Object value) {
        for (Object o : hm.keySet()) {
            if (hm.get(o).equals(value)) {
                return o;
            }
        }
        return null;
    }

    public static void sendCenteredMessage(Player player, String message){
        if(message == null || message.equals("")) player.sendMessage("");
        message = ChatColor.translateAlternateColorCodes('&', message);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for(char c : message.toCharArray()){
            if(c == '§'){
                previousCode = true;
                continue;
            }else if(previousCode == true){
                previousCode = false;
                if(c == 'l' || c == 'L'){
                    isBold = true;
                    continue;
                }else isBold = false;
            }else{
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while(compensated < toCompensate){
            sb.append(" ");
            compensated += spaceLength;
        }
        player.sendMessage(sb.toString() + message);
    }

    public enum DefaultFontInfo {

        A('A', 5),
        a('a', 5),
        B('B', 5),
        b('b', 5),
        C('C', 5),
        c('c', 5),
        D('D', 5),
        d('d', 5),
        E('E', 5),
        e('e', 5),
        F('F', 5),
        f('f', 4),
        G('G', 5),
        g('g', 5),
        H('H', 5),
        h('h', 5),
        I('I', 3),
        i('i', 1),
        J('J', 5),
        j('j', 5),
        K('K', 5),
        k('k', 4),
        L('L', 5),
        l('l', 1),
        M('M', 5),
        m('m', 5),
        N('N', 5),
        n('n', 5),
        O('O', 5),
        o('o', 5),
        P('P', 5),
        p('p', 5),
        Q('Q', 5),
        q('q', 5),
        R('R', 5),
        r('r', 5),
        S('S', 5),
        s('s', 5),
        T('T', 5),
        t('t', 4),
        U('U', 5),
        u('u', 5),
        V('V', 5),
        v('v', 5),
        W('W', 5),
        w('w', 5),
        X('X', 5),
        x('x', 5),
        Y('Y', 5),
        y('y', 5),
        Z('Z', 5),
        z('z', 5),
        NUM_1('1', 5),
        NUM_2('2', 5),
        NUM_3('3', 5),
        NUM_4('4', 5),
        NUM_5('5', 5),
        NUM_6('6', 5),
        NUM_7('7', 5),
        NUM_8('8', 5),
        NUM_9('9', 5),
        NUM_0('0', 5),
        EXCLAMATION_POINT('!', 1),
        AT_SYMBOL('@', 6),
        NUM_SIGN('#', 5),
        DOLLAR_SIGN('$', 5),
        PERCENT('%', 5),
        UP_ARROW('^', 5),
        AMPERSAND('&', 5),
        ASTERISK('*', 5),
        LEFT_PARENTHESIS('(', 4),
        RIGHT_PERENTHESIS(')', 4),
        MINUS('-', 5),
        UNDERSCORE('_', 5),
        PLUS_SIGN('+', 5),
        EQUALS_SIGN('=', 5),
        LEFT_CURL_BRACE('{', 4),
        RIGHT_CURL_BRACE('}', 4),
        LEFT_BRACKET('[', 3),
        RIGHT_BRACKET(']', 3),
        COLON(':', 1),
        SEMI_COLON(';', 1),
        DOUBLE_QUOTE('"', 3),
        SINGLE_QUOTE('\'', 1),
        LEFT_ARROW('<', 4),
        RIGHT_ARROW('>', 4),
        QUESTION_MARK('?', 5),
        SLASH('/', 5),
        BACK_SLASH('\\', 5),
        LINE('|', 1),
        TILDE('~', 5),
        TICK('`', 2),
        PERIOD('.', 1),
        COMMA(',', 1),
        SPACE(' ', 3),
        DEFAULT('a', 4);

        private char character;
        private int length;

        DefaultFontInfo(char character, int length) {
            this.character = character;
            this.length = length;
        }

        public char getCharacter() {
            return this.character;
        }

        public int getLength() {
            return this.length;
        }

        public int getBoldLength() {
            if (this == DefaultFontInfo.SPACE) return this.getLength();
            return this.length + 1;
        }

        public static DefaultFontInfo getDefaultFontInfo(char c) {
            for (DefaultFontInfo dFI : DefaultFontInfo.values()) {
                if (dFI.getCharacter() == c) return dFI;
            }
            return DefaultFontInfo.DEFAULT;
        }
    }
}
