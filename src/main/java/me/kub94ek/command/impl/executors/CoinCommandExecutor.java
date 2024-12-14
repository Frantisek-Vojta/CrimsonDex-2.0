package me.kub94ek.command.impl.executors;

import me.kub94ek.Main;
import me.kub94ek.command.CommandExecutor;
import me.kub94ek.data.database.Database;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class CoinCommandExecutor implements CommandExecutor {
    @Override
    public void executeCommand(SlashCommandInteractionEvent event) {
        String memberId = event.getMember().getId();
        Database database = Main.getDatabase();
        
        switch (event.getSubcommandName()) {
            case "bal" -> event.reply(
                    "Your balance: " + database.getCoins(memberId) + " coins"
            ).setEphemeral(true).queue();
            case "give" -> {
                OptionMapping userOption = event.getOption("user");
                OptionMapping coinsOption = event.getOption("coins");
                User user = userOption.getAsUser();
                int coins = coinsOption.getAsInt();
                
                if (coins > database.getCoins(memberId)) {
                    event.reply("You don't have enough coins!").setEphemeral(true).queue();
                    return;
                }
                
                if (user.isBot() || user.isSystem() || user.getId().equals(memberId)) {
                    event.reply("Invalid user!").setEphemeral(true).queue();
                    return;
                }
                
                database.giveCoins(memberId, user.getId(), coins);
                event.reply("Gave " + coins + " coins to <@" + user.getId() + ">").setEphemeral(true).queue();
                
            }
        }
    }
    
}
