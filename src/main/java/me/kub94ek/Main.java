package me.kub94ek;

import me.kub94ek.data.database.Database;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main extends ListenerAdapter {
    private static JDA jda;
    private static Database database;
    
    public static void main(String[] args) {
        if (args.length < 1) {
            throw new RuntimeException("Missing arguments");
        }
        
        database = new Database();
        
        new Main().startBot(args[0]);
        
    }
    
    public void startBot(String botToken) {
        JDABuilder builder = JDABuilder.createLight(
                botToken,
                GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS
        );
        
        builder.setActivity(Activity.customStatus("Throwing CrimsonBalls on the table"));
        
        try {
            jda = builder.build().awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        
        jda.addEventListener(this);
        
        jda.getGuilds().forEach(Main::registerGuildCommands);
    }
    
    private static void registerGuildCommands(Guild server) {
        // TODO: Command registering
    }
    
    
    public static JDA getJda() {
        return jda;
    }
    
}