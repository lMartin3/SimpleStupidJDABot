package me.lmartin3.ejebot.events;

import me.lmartin3.ejebot.EjeBot;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Listeners extends ListenerAdapter {
    private EjeBot bot;
    public Listeners(EjeBot bot) {
        this.bot = bot;
        bot.getJda().addEventListener(this);
    }

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {
        manageReaction(e.getMember(), e.getMessageId(), e.getReactionEmote().getName(), e.getChannel(), true);
    }

    @Override
    public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent e) {
        manageReaction(e.getMember(), e.getMessageId(), e.getReactionEmote().getName(), e.getChannel(), false);
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent e) {
        bot.updateStatsChannel();
    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent e) {
        bot.updateStatsChannel();
    }


    private void manageReaction(Member member, String messageId, String reaction, TextChannel channel, boolean add) {
        if (member==null) return;
        Role verificationRole = member.getGuild().getRoleById(bot.getBotConfiguration().getVerificationRole());
        if (verificationRole == null) return;
        System.out.println("Member: " + member.getUser().getAsMention() + " mid: "+ messageId
        + " reaction: " + reaction + " add: " + add);
        if(messageId.equalsIgnoreCase(bot.getBotConfiguration().getVerificationMessage())) {
            if (add) {
                member.getGuild().addRoleToMember(member.getId(), verificationRole).queue();
                channel.getHistoryFromBeginning(100)
                        .queue(mh -> mh.getRetrievedHistory().stream()
                                .filter(msg -> msg.getId().equalsIgnoreCase(messageId))
                                .forEach(msg -> msg.removeReaction(reaction, member.getUser()).queue())
                        );
            } else {
                //member.getGuild().removeRoleFromMember(member.getId(), role).queue();
            }
            return;
        }
        if(!bot.getBotConfiguration().getReactionMessages().containsKey(messageId)) return;
        Map<String, String> reactionRoles = bot.getBotConfiguration().getReactionMessages().get(messageId);
        if(!reactionRoles.containsKey(reaction)) return;
        if(!member.getRoles().contains(verificationRole)) {
            channel.getHistoryFromBeginning(100)
                    .queue(mh -> mh.getRetrievedHistory().stream()
                            .filter(msg -> msg.getId().equalsIgnoreCase(messageId))
                            .forEach(msg -> msg.removeReaction(reaction, member.getUser()).queue())
                    );
        }
        Role role = member.getGuild().getRoleById(reactionRoles.get(reaction));
        if (role == null) return;
        if(add) {
            member.getGuild().addRoleToMember(member.getId(), role).queue();
        } else {
            member.getGuild().removeRoleFromMember(member.getId(), role).queue();
        }
    }


    private TextChannel getLogChannel() {
        return bot.getJda().getTextChannelById(bot.getBotConfiguration().getLogChannel());
    }


}
