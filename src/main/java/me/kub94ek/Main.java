package me.kub94ek;

import me.kub94ek.data.database.Database;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
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
        server.updateCommands().addCommands(
                Commands.slash("card", "Command for controlling cards")
                        .addSubcommands(
                                new SubcommandData("list", "List your cards"),
                                new SubcommandData("give", "Give your card to somebody")
                                        .addOption(OptionType.USER, "user", "The user to give the card to", true)
                                        .addOption(OptionType.STRING, "id", "ID of the card to give (without the #)", true),
                                new SubcommandData("last", "Shows you the last caught card")
                        ),
                Commands.slash("battle", "Command for controlling battles")
                        .addSubcommands(
                                new SubcommandData("request", "Send battle request")
                                        .addOption(OptionType.USER, "user", "The user to send the request to", true),
                                new SubcommandData("accept", "Accept battle request")
                                        .addOption(OptionType.USER, "user", "The request sender", true),
                                new SubcommandData("deny", "Deny battle request")
                                        .addOption(OptionType.USER, "user", "The request sender", true),
                                new SubcommandData("add", "Add card to battle")
                                        .addOption(OptionType.STRING, "id", "ID of the card to add (without the #)", true),
                                new SubcommandData("remove", "Remove card from battle")
                                        .addOption(OptionType.STRING, "id", "ID of the card to remove (without the #)", true),
                                new SubcommandData("list", "List all cards in the battle"),
                                new SubcommandData("stats", "Show your stats"),
                                new SubcommandData("start", "Starts the battle")
                        ),
                Commands.slash("shop", "Command for controlling shop")
                        .addSubcommands(
                                new SubcommandData("open", "Opens shop menu"),
                                new SubcommandData("buy", "Allows you to buy cards from shop")
                                        .addOption(OptionType.STRING, "id", "ID of the card to buy", true)
                                        .addOption(OptionType.BOOLEAN, "gamble",
                                                "Whether or not to add random stat bonus to bought card"
                                        )
                        ),
                Commands.slash("coin", "Command for controlling coins")
                        .addSubcommands(
                                new SubcommandData("bal", "Shows you your coin balance"),
                                new SubcommandData("give", "Gives coins to somebody")
                                        .addOption(OptionType.USER, "user", "The user to give the coins to")
                                        .addOption(OptionType.INTEGER, "coins", "How many coins to give")
                        )
        ).queue();
    }
    
    
    public static JDA getJda() {
        return jda;
    }
    public static Database getDatabase() {
        return database;
    }
    
}