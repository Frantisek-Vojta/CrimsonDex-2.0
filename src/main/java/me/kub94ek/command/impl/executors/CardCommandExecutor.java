package me.kub94ek.command.impl.executors;

import me.kub94ek.Main;
import me.kub94ek.card.Card;
import me.kub94ek.command.CommandExecutor;
import me.kub94ek.data.database.Database;
import me.kub94ek.image.CardCreator;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.utils.FileUpload;

import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class CardCommandExecutor implements CommandExecutor {
    
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
                    
                    StringSelectMenu.Builder builder = StringSelectMenu.create("card-list");
                    var message = event.reply("Listing all your cards:");
                    List<Card> cards = database.getUserCards(memberId);
                    int idx = 0;
                    if (cards.size() >= 25) {
                        while (cards.size() > 25) {
                            for (int i = 0; i < 25; i++) {
                                builder.addOption(cards.getFirst().getListString(), cards.getFirst().getId(),
                                        Emoji.fromCustom(cards.getFirst().getType().emoji));
                                cards.removeFirst();
                            }
                            message.addActionRow(builder.build());
                            idx++;
                            builder = StringSelectMenu.create("card-list" + idx);
                        }
                    }
                    
                    StringSelectMenu.Builder finalBuilder = builder;
                    cards.forEach((card) -> {
                        finalBuilder.addOption(card.getListString(), card.getId(),
                                        Emoji.fromCustom(card.getType().emoji));
                    });
                    message.addActionRow(builder.build());
                    message.setEphemeral(true).queue();
                }
                case "give" -> {
                    User user = Objects.requireNonNull(event.getOption("user")).getAsUser();
                    String cardId = Objects.requireNonNull(event.getOption("id")).getAsString();
                    
                    if (!database.cardExists(cardId)) {
                        event.reply("Unknown card").setEphemeral(true).queue();
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
                    event.getChannel().sendMessage(event.getMember().getAsMention() + " gave the card "
                            + database.getCard(cardId).getListString() + " to " + user.getAsMention()).queue();
                    
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
    
}
