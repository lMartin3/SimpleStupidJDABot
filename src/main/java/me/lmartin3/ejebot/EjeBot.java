package me.lmartin3.ejebot;

import lombok.Getter;
import me.lmartin3.ejebot.commands.CommandLoader;
import me.lmartin3.ejebot.commands.Commands;
import me.lmartin3.ejebot.commands.StaffCommands;
import me.lmartin3.ejebot.configuration.BotConfiguration;
import me.lmartin3.ejebot.events.Listeners;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;

import javax.security.auth.login.LoginException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

@Getter
public class EjeBot {
    public static final String version = "1.1";
    private final BotConfiguration botConfiguration;
    private JDA jda;
    private CommandLoader commandLoader;

    public EjeBot(BotConfiguration botConfiguration) {
        this.botConfiguration = botConfiguration;
    }

    public void init() {
        System.out.println("Starting EjeBot instance");
        try {
            //jda = JDABuilder.createDefault(botConfiguration.getToken()).build();
            jda = new JDABuilder(AccountType.BOT).setToken(botConfiguration.getToken()).build();
            System.out.println("Logged in");
        } catch (LoginException | NullPointerException e) {
            System.out.println("Error logging in with JDA");
            e.printStackTrace();
            return;
        }
        System.out.println("Registering listeners");
        new Listeners(this);
        System.out.println("Starting command loader");
        commandLoader = new CommandLoader(this);
        System.out.println("Registering commands");
        commandLoader.registerCommands(new Commands(this));
        commandLoader.registerCommands(new StaffCommands(this));
        System.out.println("Setting status");
        jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(Activity.ActivityType.DEFAULT, "g.help | EjeBot v"+version));
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        updateStatsChannel();
                    }
                },
                1000*10
        );
        /*
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.out.println("hello");
            }
        };
        timer.schedule(task,0,3000);
         */
    }

    public void updateStatsChannel() {
        System.out.println("Updating stats channel... channel: " + botConfiguration.getStatsChannel());
        VoiceChannel channel = jda.getVoiceChannelById("747250241845067847");
        if(channel==null) { System.out.println("Channel is null"); return; }
        channel.getManager().setName("┆\uD83D\uDC65┆ Usuarios: " +
                (int) channel.getGuild().getMembers().stream().filter(m -> !m.getUser().isBot()).count()
        ).queue();
    }
}
