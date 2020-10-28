package me.lmartin3.ejebot.configuration;

import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
public class BotConfiguration {
    private String[] owners = new String[]{
            "405801436534931457",
            "294913372586115074",
            "345623314011521025"
    };
    private String guild = "541723569730748416";
    private String logChannel = "745654076552904814";
    private String statsChannel = "747250241845067847";
    private String punishmentsChannel = "748545800287551548";
    private String suggestionsChannel = "749783619630923846";

    private String verificationRole = "623640446677155861";
    private String muteRole = "748544829755228200";
    private String vcMuteRole = "748559979753242684";

    private String verificationMessage = "746577001632235620";
    private String verificationEmoji = "☑️";

    private Map<String, Map<String, String>> reactionMessages;
    private String token = "NTAxNTM1MzAwMjcxNzM0Nzg0.W8UhJQ.sQmI57Xmq82B-skPx_AHhMZDuWo";

    public BotConfiguration() {
        Map<String, String> eje = new HashMap<>();
        eje.put("⛏️", "745675038602166314"); //MINECRAFT
        eje.put("\uD83C\uDF4A", "745675041366081667"); //100% OJ
        eje.put("\uD83C\uDF32", "745675283562233897"); //TERRARIA
        eje.put("⚙️", "745675324469280830"); //FACTORIO
        eje.put("\uD83C\uDFB5", "745675327602294905"); //OSU
        eje.put("\uD83D\uDCA5", "745675035284340757"); //R6
        eje.put("\uD83C\uDD71️", "745675023896936568"); //CSGO
        eje.put("\uD83D\uDD35", "745715208147828747"); //GMOD
        eje.put("🔻", "745715211297882114"); //VALORANT 0xD83D 0xDD3D
        eje.put("🧙", "745715213298434170"); //LOL
        eje.put("🔎", "749130615504502784"); //AMONG US

        Map<String, String> others = new HashMap<>();
        others.put("🔞", "747247950031355996");
        others.put("📰", "749380306758270995");
        others.put("\uD83D\uDCB8", "764262796468551690");
        reactionMessages = new HashMap<>();
        reactionMessages.put("745716308267630602", eje);
        reactionMessages.put("749373199447228517", others);
    }

    public boolean isOwner(String id) {
        return Arrays.asList(owners).contains(id);
    }
}
