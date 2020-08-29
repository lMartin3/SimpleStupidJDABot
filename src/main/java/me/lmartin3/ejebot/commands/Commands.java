package me.lmartin3.ejebot.commands;

import me.lmartin3.ejebot.EjeBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Commands {
    private EjeBot bot;

    public Commands(EjeBot bot) {
        this.bot = bot;
    }

    @BotCommand(name = "help", description = "Mostrar una lista de comandos")
    public void helpCommand(Message message, String[] arguments) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Menú de ayuda");
        builder.setFooter("Por cualquier duda, comuníquese con un administrador.");
        builder.setThumbnail(bot.getJda().getSelfUser().getAvatarUrl());
        bot.getCommandLoader().commands.values().stream().map(CommandLoader.RegisteredCommand::getAnnotation).forEach((bc) -> {
            builder.addField(bc.name(), bc.description(), true);
        });
        message.getChannel().sendMessage(builder.build()).queue();
    }

    @BotCommand(name = "about", description = "Acerca del bot")
    public void aboutCommand(Message message, String[] arguments) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Acerca del bot");
        builder.setDescription("EjeBot es un bot custom para el Eje Otaku-Gamer-Normie con múltiples funcionalidades de moderación y entretenimiento.");
        builder.setFooter("Creado por lMartin3#1975");
        builder.setThumbnail(bot.getJda().getSelfUser().getAvatarUrl());
        builder.setColor(Color.LIGHT_GRAY);
        message.getChannel().sendMessage(builder.build()).queue();
    }

    @BotCommand(name = "avatar", description = "Ver el avatar de alguien")
    public void avatarCommand(Message message, String[] arguments) {
        User mentioned = getMentionedUser(message);
        if(mentioned==null) {
            message.getChannel().sendMessage(":x: ¡Tenés que mencionar a un usuario!").queue();
            return;
        }
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Avatar de " + mentioned.getAsTag());
        builder.setImage(mentioned.getAvatarUrl());
        builder.setColor(Color.BLACK);
        message.getChannel().sendMessage(builder.build()).queue();
    }

    @BotCommand(name = "r34", description = "Ver el avatar de alguien")
    public void r34Command(Message message, String[] arguments) {
        if(arguments.length < 1) {
            message.getChannel().sendMessage(":x: ¡Tenés que ingresar un parámetro de búsqueda!");
            return;
        }
        if(!message.getTextChannel().isNSFW()) {
            message.getChannel().sendMessage(":warning: Este comando debe ser ejecutado en un canal NSFW.").queue();
            return;
        }
        String param = arguments[0];
        Document document = getHtmlDocument("https://rule34.xxx/index.php?page=post&s=list&tags=" + param);
        Elements elements = document.getElementsByTag("a");
        ArrayList<String> urls = new ArrayList<>();
        for(Element element : elements) {
            if(element.html().contains("<img")) {
                urls.add(element.attr("href"));
            }
        }
        if(urls.size()==0) {
            message.getChannel().sendMessage(":x: Sin resultados").queue();
            return;
        }
        int random = ThreadLocalRandom.current().nextInt(0, urls.size()); //number between 0 and urls.size() -1

        Document hdImage = getHtmlDocument("https://rule34.xxx/" + urls.get(random));
        String result = hdImage.getElementById("image").attr("src");
        System.out.println(urls.get(random));
        message.getChannel().sendMessage(new EmbedBuilder().setImage(result).build()).queue();
    }


    private User getMentionedUser(Message message) {
        if(message.getMentionedUsers().size()!=0) return message.getMentionedUsers().get(0);
        return null;
    }

    public static Document getHtmlDocument(String url) {

        Document doc = null;
        try {
            doc = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000).get();
        } catch (IOException ex) {
            System.out.println("Excepción al obtener el HTML de la página" + ex.getMessage());
        }
        return doc;
    }

}
