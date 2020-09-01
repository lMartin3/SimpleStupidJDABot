package me.lmartin3.ejebot.moderation;

import me.lmartin3.ejebot.EjeBot;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class PunishUtils {
    public static String getReason(int removeBefore, String[] arguments) {
        StringBuilder reasonBuilder = new StringBuilder();
        for(int i=removeBefore;i<arguments.length;i++) {
            reasonBuilder.append(arguments[i]).append(" ");
        }
        return reasonBuilder.toString();
    }

    public static TextChannel getPunishmentChannel(EjeBot bot) {
        return bot.getJda().getTextChannelById(bot.getBotConfiguration().getPunishmentsChannel());
    }

    public static User getMentionedUser(Message message) {
        if(message.getMentionedUsers().size()!=0) return message.getMentionedUsers().get(0);
        return null;
    }
}
