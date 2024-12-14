package me.kub94ek.command.impl.executors;

import me.kub94ek.Main;
import me.kub94ek.card.Card;
import me.kub94ek.command.CommandExecutor;
import me.kub94ek.data.database.Database;
import me.kub94ek.image.CardCreator;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.FileUpload;

import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CardCommandExecutor extends ListenerAdapter implements CommandExecutor {
    
    public static final HashMap<String, Integer> pages = new HashMap<>();
    
    
    @Override
    public void executeCommand(SlashCommandInteractionEvent event) {
        String memberId = event.getMember().getId();
        Database database = Main.getDatabase();
        
        switch (Objects.requireNonNull(event.getSubcommandName())) {
                case "list" -> {
                    if (database.getUserCards(memberId).isEmpty()) {
                        event.reply("You don't have any card yet.").setEphemeral(true).queue();
                        return;
                    }
                    
                    
                    var message = createListMessage(
                            event.reply("Listing all your cards:"),
                            database.getUserCards(memberId),
                            0
                    );
                    message.setEphemeral(true).queue(sentMessage -> pages.put(memberId, 0));
                }
                case "give" -> {
                    User user = Objects.requireNonNull(event.getOption("user")).getAsUser();
                    String cardId = Objects.requireNonNull(event.getOption("id")).getAsString();
                    
                    if (!database.cardExists(cardId)) {
                        event.reply("Unknown card").setEphemeral(true).queue();
                        return;
                    }
                    
                    if (user.isBot() || user.isSystem()) {
                        event.reply("Invalid user").setEphemeral(true).queue();
                        return;
                    }
                    
                    /*if ((battleData.containsKey(memberId) && battleData.get(memberId).containsCard(cardId))
                            || (battles.containsKey(memberId)
                            && startedBattleData.get(battles.get(memberId)).cardHealth.containsKey(cardId))) {
                        event.reply("This card is currently not available").setEphemeral(true).queue();
                        return;
                    }*/
                    
                    boolean[] owns = {false};
                    
                    database.getUserCards(memberId).forEach(card -> {
                        if (card.getId().equals(cardId)) {
                            owns[0] = true;
                        }
                    });
                    
                    if (!owns[0]) {
                        event.reply("You don't own this card").setEphemeral(true).queue();
                        return;
                    }
                    
                    try {
                        database.moveCard(cardId, user.getId());
                    } catch (SQLException e) {
                        e.printStackTrace();
                        event.reply("""
                                Unexpected database error occurred.\
                                
                                Contact <@1064940406560788540> if this continues happening. \
                                Don't forget to provide the error ID.\
                                
                                Error ID: jurhOd4zf9gUaAUP5X\s"""
                        ).setEphemeral(true).queue();
                    }
                    
                    event.reply("You gave the card " + database.getCard(cardId).getListString() +
                            " to " + user.getAsMention()).setEphemeral(true).queue();
                    event.getChannel().sendMessage(event.getMember().getAsMention() + " gave the card `"
                            + database.getCard(cardId).getListString() + "` to " + user.getAsMention()).queue();
                    
                }
                case "last" -> {
                    if (database.getUserCards(memberId).isEmpty()) {
                        event.reply("You don't have any card yet.").setEphemeral(true).queue();
                        return;
                    }
                    
                    Card card = database.getUserCards(memberId).getLast();
                    event.deferReply(true).queue();
                    
                    try {
                        CardCreator.createCardImage(new Card("id", "owner", card.getType(),
                                card.getAtkBonus(), card.getHpBonus()));
                    } catch (IOException | FontFormatException e) {
                        e.printStackTrace();
                        event.reply("""
                                Unexpected database error occurred.\
                                
                                Contact <@1064940406560788540> if this continues happening. \
                                Don't forget to provide the error ID.\
                                
                                Error ID: papsq6Z8VTFCZKjmst\s"""
                        ).setEphemeral(true).queue();
                    }
                    
                    event.getHook().sendMessage(card.toString())
                            .addFiles(FileUpload.fromData(
                                new File("image.jpg")
                            ))
                            .queue();
                    
                }
            }
    }
    
    public static ReplyCallbackAction createListMessage(ReplyCallbackAction message, List<Card> cards, int page) {
        
        StringSelectMenu.Builder builder = StringSelectMenu.create("card-list");
        
        for (int i = page*25; i < (Math.min(cards.size(), 25*(page+1))); i++) {
            builder.addOption(
                    cards.get(i).getListString(),
                    cards.get(i).getId(),
                    Emoji.fromCustom(cards.get(i).getType().emoji)
            );
        }
        
        message.addActionRow(builder.build());
        
        List<Button> buttons = new ArrayList<>();
        if (page > 0) {
            buttons.add(Button.secondary("previous-page", "⮜"));
        }
        if (25*(page+1) < cards.size()) {
            buttons.add(Button.secondary("next-page", "⮞"));
        }
        
        if (!buttons.isEmpty()) {
            message.addActionRow(buttons);
        }
        
        return message;
    }
    
}
