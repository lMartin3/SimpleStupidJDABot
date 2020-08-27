package me.lmartin3.ejebot;

import com.google.gson.Gson;
import me.lmartin3.ejebot.configuration.BotConfiguration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Launcher {
    private static EjeBot bot;
    private static BotConfiguration botConfiguration;
    public static void main(String[] args) {
        loadConfiguration();
        bot = new EjeBot(botConfiguration);
        bot.init();
    }



    private static void loadConfiguration() {
        try {
            Gson gson = new Gson();
            File file = new File("config.json");
            if(!file.exists()) {
                botConfiguration = new BotConfiguration();
                FileWriter writer = new FileWriter(file);
                writer.write(gson.toJson(botConfiguration));
                writer.flush();
                writer.close();
            } else {
                StringBuilder json = new StringBuilder();
                Scanner scanner = new Scanner(file);
                while(scanner.hasNext()) {
                    json.append(scanner.next());
                }
                scanner.close();
                botConfiguration = gson.fromJson(json.toString(), BotConfiguration.class);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
