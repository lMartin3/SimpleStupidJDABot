package me.lmartin3.ejebot.events;

import me.lmartin3.ejebot.EjeBot;
import me.lmartin3.ejebot.moderation.PunishUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.regex.Pattern;

public class Listeners extends ListenerAdapter {
    private EjeBot bot;
    private Pattern invitationPattern1 = Pattern.compile("discord.gg\\/[A-z0-9]{1,6}");
    private Pattern invitationPattern2 = Pattern.compile("discord.com\\/invite\\/[A-z0-9]{1,6}");
    public Listeners(EjeBot bot) {
        this.bot = bot;
        bot.getJda().addEventListener(this);
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if(e.getChannel().getId().equalsIgnoreCase(bot.getBotConfiguration().getSuggestionsChannel())) {
            Emote approveEmote = e.getChannel().getGuild().getEmoteById("749955884389498892");
            Emote denyEmote = e.getChannel().getGuild().getEmoteById("749955884796608582");
            if (approveEmote != null && denyEmote != null) {
                e.getMessage().addReaction(approveEmote).queue(z -> {
                    e.getMessage().addReaction(denyEmote).queue();
                });
            }
        }



        //antispam
        Member member = e.getMessage().getMember();
        if(member==null) return;
        if(member.hasPermission(Permission.ADMINISTRATOR)) return;
        if(invitationPattern1.matcher(e.getMessage().getContentRaw()).find()
        || invitationPattern2.matcher(e.getMessage().getContentRaw()).find()) {
            e.getMessage().delete().queue();
            e.getChannel().sendMessage(e.getMessage().getAuthor().getAsMention() + " ¡No envíes invitaciones!").queue();
            EmbedBuilder builder = new EmbedBuilder();
            builder
                    .setTitle("⚠️» Advertencia")
                    .setDescription(e.getMessage().getAuthor().getAsTag() + " ha sido advertido.")
                    .addField("Motivo:", "Violación de la regla 4", false)
                    .addField("Advertido por:","Eje AutoMod", false)
                    .addField("ID: ", e.getMessage().getAuthor().getId(), false)
            ;
            e.getMessage().getChannel().sendMessage(builder.build()).queue();
            PunishUtils.getPunishmentChannel(bot).sendMessage(builder.build()).queue();
        }
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
