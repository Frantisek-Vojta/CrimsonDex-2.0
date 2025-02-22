package me.kub94ek.command.impl.executors;

import me.kub94ek.Main;
import me.kub94ek.command.CommandExecutor;
import me.kub94ek.data.database.Database;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.sql.SQLException;
import java.util.Objects;

public class BattleCommandExecutor implements CommandExecutor {
    
    @Override
    public void executeCommand(SlashCommandInteractionEvent e) {
        String memberId = e.getMember().getId();
        Database database = Main.getDatabase();
        
        switch (Objects.requireNonNull(e.getSubcommandName())) {
            case "request" -> {
                if (database.getBattleRequests().containsKey(memberId) || database.getAcceptedBattles().containsKey(memberId)) {
                    e.reply("You have already sent a battle request!").setEphemeral(true).queue();
                    return;
                }
                
                if (database.getBattleRequests().containsValue(memberId)) {
                    e.reply("You have a pending incoming request. " +
                            "You have to resolve it before sending a request.").setEphemeral(true).queue();
                    return;
                }
                
                if (database.getAcceptedBattles().containsValue(memberId)) {
                    e.reply("You have accepted a battle request. " +
                            "You can't send requests before finishing the accepted battle.").setEphemeral(true).queue();
                    return;
                }
                
                if (Main.getStartedBattleIds().containsKey(memberId)) {
                    e.reply("You can't send battle requests during a battle.").setEphemeral(true).queue();
                    return;
                }
                
                User user = Objects.requireNonNull(e.getOption("user")).getAsUser();
                String userId = user.getId();
                
                if (user.isBot() || user.isSystem()) {
                    e.reply("You can't send battle requests to non-human users").setEphemeral(true).queue();
                    return;
                }
                
                if (userId.equals(memberId)) {
                    e.reply("You can't request a battle against yourself!").setEphemeral(true).queue();
                    return;
                }
                
                if (Main.getStartedBattleIds().containsKey(userId)) {
                    e.reply("This person is currently in a battle. Try again later.").setEphemeral(true).queue();
                    return;
                }
                
                if (Main.getDatabase().getUserCards(userId).isEmpty()) {
                    e.reply("You can't request a battle against somebody without cards.").setEphemeral(true).queue();
                    return;
                }
                
                
                database.getBattleRequests().put(memberId, userId);
                try {
                    database.requestBattle(memberId, userId);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                
                e.reply("You have sent battle request to " + user.getAsMention()).setEphemeral(true).queue();
                
                user.openPrivateChannel().queue((dms) ->
                        dms.sendMessage(e.getMember().getAsMention() + " sent you a battle request from server " + e.getGuild().getName() + ".\n" +
                            "You can use `/battle accept <user>` or `/battle deny <user>` to react to the request.").queue()
                );
                
            }
        }
        
    }
    
}
