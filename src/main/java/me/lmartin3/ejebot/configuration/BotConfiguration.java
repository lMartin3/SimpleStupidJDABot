package me.lmartin3.ejebot.configuration;

import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
public class BotConfiguration {
    private String[] owners = new String[]{
    }; //list of owners' ids (in string format)
    private String guild = ""; //guild id
    private String logChannel = ""; //text channel used for logs
    private String statsChannel = ""; //stats channel (for user count and other stuff)
    private String punishmentsChannel = ""; //the id of the channel where punishment announcements should be sent
    private String suggestionsChannel = ""; //a channel made for user suggestions

    private String verificationRole = ""; //role given to verified users
    private String muteRole = ""; //role given to muted users (permissions must be set up manually)
    private String vcMuteRole = ""; //role given to users muted in voice channels (same as above with permissions)

    private String verificationMessage = ""; //the id of the channel where verifications will be sent to (for admins)
    private String verificationEmoji = "☑️";

    private Map<String, Map<String, String>> reactionMessages;
    private String token = "place your own";

    public BotConfiguration() {
        Map<String, String> eje = new HashMap<>()
        //In this map you can place emojis for optative roles, the emoji goes first, then the id of the role
        //eje.put("⛏️", "role_id_here"); //MINECRAFT

        Map<String, String> others = new HashMap<>();
        //same as above, but for another category
        //others.put("📰", "role_id_here");
    }

    public boolean isOwner(String id) {
        return Arrays.asList(owners).contains(id);
    }
}
