package me.lmartin3.ejebot.commands;

import me.lmartin3.ejebot.EjeBot;
import me.lmartin3.ejebot.Launcher;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CommandLoader extends ListenerAdapter {

    private EjeBot bot;
    public HashMap<String, RegisteredCommand> commands = new HashMap<>();
    public CommandLoader(EjeBot bot) {
        this.bot = bot;
        bot.getJda().addEventListener(this);
    }

    public void registerCommands(Object object) {
        for (Method method : object.getClass().getMethods()) {
            BotCommand annotation = method.getAnnotation(BotCommand.class);

            if (annotation != null) {
                commands.put(annotation.prefix() + annotation.name(), new RegisteredCommand(method, object, annotation));
            }
        }
    }

    final static class RegisteredCommand {
        private Object instance;
        private Method method;
        private BotCommand annotation;

        RegisteredCommand(Method method, Object instance, BotCommand annotation) {
            this.method = method;
            this.instance = instance;
            this.annotation = annotation;
        }

        public BotCommand getAnnotation() { return annotation; }
        public Method getMethod() { return method; }
        public Object getInstance() { return instance; }
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        callCommand(event.getMessage());
    }

    public boolean callCommand(Message message) {
        List<String> split = Arrays.asList(message.getContentRaw().split(" "));
        String cmd = message.getContentRaw().split(" ")[0];
        if(!commands.containsKey(cmd)) { return false; }
        ArrayList<String> arguments = new ArrayList<>();
        for(int i=1;i<split.size();i++) {
            arguments.add(split.get(i));
        }
        String[] args = arguments.toArray(new String[0]);
        System.out.println("User "+ message.getAuthor().getAsTag() + " (" + message.getAuthor().getId()+") -> cmd: " + cmd + " args: " + Arrays.toString(args));
        try {
            RegisteredCommand command = commands.get(cmd);
            if(command==null) return false;
            if(message.getMember() != null && !message.getMember().hasPermission(command.annotation.permission())) {
                message.getChannel().sendMessage(":x: No tenés permisos para hacer esto.").queue();
                return false;
            }
            if(command.annotation.onlyCreators() && message.getMember() !=null && !bot.getBotConfiguration().isOwner(message.getMember().getId())) {
                message.getChannel().sendMessage(":x: Solo los desarrolladores pueden ejecutar este comando.").queue();
                return false;
            }
            commands.get(cmd).method.invoke(commands.get(cmd).instance, message, args);
        } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
            System.out.println("ERROR OCCURRED!");
            message.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Ha ocurrido un error")
                    .setDescription("Por favor reporta esto con los administradores")
                    .setColor(Color.RED)
                    .addField("Error", e.toString(), true)
                    .build()
            ).queue();
            e.printStackTrace();
        }
        return true;
    }
}

