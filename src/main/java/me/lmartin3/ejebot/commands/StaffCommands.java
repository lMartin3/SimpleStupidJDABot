package me.lmartin3.ejebot.commands;

import me.lmartin3.ejebot.EjeBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.util.Arrays;

public class StaffCommands {
    private EjeBot bot;

    public StaffCommands(EjeBot bot) {
        this.bot = bot;
    }

    @BotCommand(name = "warn", description = "Advertir a un usuario", permission = Permission.KICK_MEMBERS)
    public void warnCommand(Message message, String[] arguments) {
        User mentioned = getMentionedUser(message);
        if(mentioned==null) {
            message.getChannel().sendMessage(":x: ¡Tenés que mencionar a un usuario!").queue();
            return;
        }
        //TODO check de permisos para que no se pueda warnear a un staff
        String reason = getReason(1, arguments);
        message.getChannel().sendMessage(mentioned.getAsMention() + " ha sido advertido.").queue();
        EmbedBuilder builder = new EmbedBuilder();
        builder
                .setTitle("⚠️» Advertencia")
                .setDescription(mentioned.getAsTag() + " ha sido advertido.")
                .addField("Motivo:", reason, false)
                .addField("Advertido por:",message.getAuthor().getAsTag(), false)
                .addField("ID: ", mentioned.getId(), false)
        ;
        message.getChannel().sendMessage(builder.build()).queue();
        getPunishmentChannel().sendMessage(builder.build()).queue();
    }

    @BotCommand(name = "mute", description = "Mutear a un usuario", permission = Permission.KICK_MEMBERS)
    public void muteCommand(Message message, String[] arguments) {
        User mentioned = getMentionedUser(message);
        if(mentioned==null) {
            message.getChannel().sendMessage(":x: ¡Tenés que mencionar a un usuario!").queue();
            return;
        }
        String reason = getReason(1, arguments);
        Role muteRole = message.getGuild().getRoleById(bot.getBotConfiguration().getMuteRole());
        if(muteRole==null) {
            message.getChannel().sendMessage(":x: Error: El rol de muteado no existe.").queue();
            return;
        }
        message.getGuild().addRoleToMember(mentioned.getId(), muteRole).queue();
        message.getChannel().sendMessage(mentioned.getAsMention() + " ha sido muteado.").queue();
        EmbedBuilder builder = new EmbedBuilder();
        builder
                .setTitle("🔕 » Mute")
                .setDescription(mentioned.getAsTag() + " ha sido muteado.")
                .addField("Motivo:", reason, false)
                .addField("Muteado por:",message.getAuthor().getAsTag(), false)
                .addField("ID: ", mentioned.getId(), false)
        ;
        message.getChannel().sendMessage(builder.build()).queue();
        getPunishmentChannel().sendMessage(builder.build()).queue();
    }

    @BotCommand(name = "kick", description = "Kickear a un usuario", permission = Permission.KICK_MEMBERS)
    public void kickCommand(Message message, String[] arguments) {
        User mentioned = getMentionedUser(message);
        if(mentioned==null) {
            message.getChannel().sendMessage(":x: ¡Tenés que mencionar a un usuario!").queue();
            return;
        }
        String reason = getReason(1, arguments);
        EmbedBuilder builder = new EmbedBuilder();
        builder
                .setTitle("⛔ » Kick")
                .setDescription(mentioned.getAsTag() + " ha sido kickeado.")
                .addField("Motivo:", reason, false)
                .addField("Kickeado por:",message.getAuthor().getAsTag(), false)
                .addField("ID: ", mentioned.getId(), false)
        ;
        Runnable continueKick = ()->{
            message.getChannel().sendMessage(mentioned.getAsMention() + " ha sido kickeado.").queue();
            message.getChannel().sendMessage(builder.build()).queue();
            getPunishmentChannel().sendMessage(builder.build()).queue();
            message.getGuild().kick(mentioned.getId(), reason).queue();
        };
        mentioned.openPrivateChannel().queue((pc)->{
            pc.sendMessage("¡Has sido expulsado de " + message.getGuild().getName() + "!").queue((m)->{
                pc.sendMessage(builder.build()).queue(); });
            continueKick.run();
        }, (x)-> {
            continueKick.run();
        });

    }

    @BotCommand(name = "unmute", description = "Desmutear a un usuario", permission = Permission.KICK_MEMBERS)
    public void unmuteCommand(Message message, String[] arguments) {
        User mentioned = getMentionedUser(message);
        if(mentioned==null) {
            message.getChannel().sendMessage(":x: ¡Tenés que mencionar a un usuario!").queue();
            return;
        }
        Role muteRole = message.getGuild().getRoleById(bot.getBotConfiguration().getMuteRole());
        if(muteRole==null) {
            message.getChannel().sendMessage(":x: Error: El rol de muteado no existe.").queue();
            return;
        }
        Member member = message.getGuild().getMemberById(mentioned.getId());
        if(member!=null&&!member.getRoles().contains(muteRole)) {
            message.getChannel().sendMessage(":x: Ese usuario no está muteado.").queue();
        }
        message.getGuild().removeRoleFromMember(mentioned.getId(), muteRole).queue();
        message.getChannel().sendMessage(mentioned.getAsMention() + " ha sido desmuteado.").queue();
        EmbedBuilder builder = new EmbedBuilder();
        builder
                .setTitle("🔔 » Unmute")
                .setDescription(mentioned.getAsTag() + " ha sido desmuteado.")
                .addField("Desmuteado por:",message.getAuthor().getAsTag(), false)
                .addField("ID: ", mentioned.getId(), false)
        ;
        message.getChannel().sendMessage(builder.build()).queue();
        getPunishmentChannel().sendMessage(builder.build()).queue();
    }


    @BotCommand(name = "vcmute", description = "Mutear a un usuario en VC", permission = Permission.KICK_MEMBERS)
    public void vcMuteCommand(Message message, String[] arguments) {
        User mentioned = getMentionedUser(message);
        if(mentioned==null) {
            message.getChannel().sendMessage(":x: ¡Tenés que mencionar a un usuario!").queue();
            return;
        }
        String reason = getReason(1, arguments);
        Role muteRole = message.getGuild().getRoleById(bot.getBotConfiguration().getVcMuteRole());
        if(muteRole==null) {
            message.getChannel().sendMessage(":x: Error: El rol de muteado VC no existe.").queue();
            return;
        }
        Member member = message.getGuild().getMemberById(mentioned.getId());
        if(member!=null&&member.getRoles().contains(muteRole)) {
            message.getChannel().sendMessage(":x: Ese usuario ya está muteado en VC.").queue();
        }
        message.getGuild().addRoleToMember(mentioned.getId(), muteRole).queue();
        message.getChannel().sendMessage(mentioned.getAsMention() + " ha sido muteado en VC.").queue();
        EmbedBuilder builder = new EmbedBuilder();
        builder
                .setTitle("🔇 » VC Mute")
                .setDescription(mentioned.getAsTag() + " ha sido muteado en VC.")
                .addField("Motivo:", reason, false)
                .addField("Muteado por:",message.getAuthor().getAsTag(), false)
                .addField("ID: ", mentioned.getId(), false)
        ;
        message.getChannel().sendMessage(builder.build()).queue();
        getPunishmentChannel().sendMessage(builder.build()).queue();
    }

    @BotCommand(name = "vcunmute", description = "Desmutear a un usuario de VC", permission = Permission.KICK_MEMBERS)
    public void vcUnmuteCommand(Message message, String[] arguments) {
        User mentioned = getMentionedUser(message);
        if(mentioned==null) {
            message.getChannel().sendMessage(":x: ¡Tenés que mencionar a un usuario!").queue();
            return;
        }
        Role muteRole = message.getGuild().getRoleById(bot.getBotConfiguration().getVcMuteRole());
        if(muteRole==null) {
            message.getChannel().sendMessage(":x: Error: El rol de muteado VC no existe.").queue();
            return;
        }
        Member member = message.getGuild().getMemberById(mentioned.getId());
        if(member!=null&&!member.getRoles().contains(muteRole)) {
            message.getChannel().sendMessage(":x: Ese usuario no está muteado en VC.").queue();
        }
        message.getGuild().removeRoleFromMember(mentioned.getId(), muteRole).queue();
        message.getChannel().sendMessage(mentioned.getAsMention() + " ha sido desmuteado en VC.").queue();
        EmbedBuilder builder = new EmbedBuilder();
        builder
                .setTitle("🔈 » VC Unmute")
                .setDescription(mentioned.getAsTag() + " ha sido desmuteado en VC.")
                .addField("Desmuteado por:",message.getAuthor().getAsTag(), false)
                .addField("ID: ", mentioned.getId(), false)
        ;
        message.getChannel().sendMessage(builder.build()).queue();
        getPunishmentChannel().sendMessage(builder.build()).queue();
    }

    @BotCommand(name = "react", description = "React to a message", onlyCreators = true)
    public void reactCommand(Message message, String[] arguments) {
        if(arguments.length < 2) {
            message.getChannel().sendMessage(":x: Debes especificar un mensaje al cual reaccionar y una reacción").queue();
        } else {
            message.getChannel().getHistoryFromBeginning(100).queue(mh->{
                for(Message m : mh.getRetrievedHistory()) {
                    if(m.getId().equalsIgnoreCase(arguments[0])) {
                        m.addReaction(arguments[1]).queue((v)->{
                            message.getChannel().sendMessage(":white_check_mark: Hecho.").queue();
                        });
                        return;
                    }
                }
                message.getChannel().sendMessage(":x: Mensaje no encontrado.").queue();
            });

        }
    }



    private String getReason(int removeBefore, String[] arguments) {
        StringBuilder reasonBuilder = new StringBuilder();
        for(int i=removeBefore;i<arguments.length;i++) {
            reasonBuilder.append(arguments[i]).append(" ");
        }
        return reasonBuilder.toString();
    }

    private TextChannel getPunishmentChannel() {
        return bot.getJda().getTextChannelById(bot.getBotConfiguration().getPunishmentsChannel());
    }

    private User getMentionedUser(Message message) {
        if(message.getMentionedUsers().size()!=0) return message.getMentionedUsers().get(0);
        return null;
    }
}
