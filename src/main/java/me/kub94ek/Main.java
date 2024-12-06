package me.kub94ek;

import me.kub94ek.card.Card;
import me.kub94ek.card.CardType;
import me.kub94ek.data.database.Database;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main extends ListenerAdapter {
    private static JDA jda;
    private static Database database;
    private static List<String> availableChannels = new ArrayList<>();
    
    private static final List<String> messageIds = new ArrayList<>();
    
    private static final HashMap<String, Button> buttons = new HashMap<>();
    private static final HashMap<String, CardType> cardTypes = new HashMap<>();
    private static final HashMap<String, List<String>> rightAnswers = new HashMap<>();
    
    private static final List<String> whitelistedChannels = List.of("1274350116379164793");
    private static final ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
    
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
        
        
        availableChannels.addAll(whitelistedChannels);
    }
    
    private static FileUpload createFileUpload(String fileName) {
        ClassLoader classLoader = Main.class.getClassLoader();

        InputStream is = classLoader.getResourceAsStream(fileName);
        if (is == null) {
            throw new IllegalArgumentException("File not found: " + fileName);
        }
        return FileUpload.fromData(is, "image.png");
    }
    
    
    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if (whitelistedChannels.contains(e.getGuildChannel().getId()) && !e.getAuthor().isBot() && !e.getAuthor().isSystem()) {
            if (availableChannels.contains(e.getGuildChannel().getId())) {
                availableChannels.remove(e.getGuildChannel().getId());
                
                spawnCard(e.getGuildChannel().getId());
                
                service.schedule(() -> {
                    availableChannels.add(e.getGuildChannel().getId());
                }, 5, TimeUnit.MINUTES);
            }
        }
    }
    
    private static void spawnCard(String channelId) {
        TextChannel channel = jda.getTextChannelById(channelId);
        Random random = new Random();
        CardType type = CardType.values()[random.nextInt(CardType.values().length)];
        while (!type.obtainable) {
            type = CardType.values()[random.nextInt(CardType.values().length)];
        }
        Button button = Button.primary("catch", "Catch CrimsonBall!");
        
        final CardType finalType = type;
        channel.sendMessage(
                        "New CrimsonBall has spawned \n\n"
                )
                .addFiles(
                        createFileUpload("image/spawn/" + type.imageName + ".png")
                )
                .addActionRow(
                        button
                ).queue(message -> {
                    new File("image.png").delete();
                    var messageId = message.getId();
                    
                    buttons.put(messageId, button);
                    messageIds.add(messageId);
                    rightAnswers.put(channelId, finalType.validAnswers);
                    cardTypes.put(messageId, finalType);
                    
                    service.schedule(() -> {
                        if (buttons.containsKey(messageId)) {
                            message.editMessageComponents(ActionRow.of(button.asDisabled())).queue();
                            buttons.remove(messageId);
                        }
                        messageIds.remove(messageId);
                    }, 3, TimeUnit.MINUTES);
                });
    }
    
    
    @Override
    public void onButtonInteraction(ButtonInteractionEvent e) {
        //String memberId = dms.get(e.getChannelId());
        
        if (e.getComponentId().equals("catch")) {
            if (!messageIds.contains(e.getMessage().getId())) {
                e.reply("This button can't be used").setEphemeral(true).queue();
                return;
            }
            
            TextInput answer = TextInput.create("answer", "Answer", TextInputStyle.SHORT)
                    .setPlaceholder("Type the answer here")
                    .setMinLength(1)
                    .setMaxLength(100)
                    .build();
            
            Modal modal = Modal.create("catch", "Catch the card")
                    .addComponents(ActionRow.of(answer))
                    .build();
            
            e.replyModal(modal).queue();
        }
    }
    
    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (event.getModalId().equals("catch")) {
            String answer = event.getValue("answer").getAsString();
            if (!messageIds.contains(event.getMessage().getId())) {
                event.reply("This card was already caught.").setEphemeral(true).queue();
                return;
            }
            
            if (rightAnswers.get(event.getChannelId()).contains(answer.toLowerCase())) {
                if (!messageIds.contains(event.getMessage().getId())) {
                    event.reply("This card was already caught.").setEphemeral(true).queue();
                    return;
                }
                
                messageIds.remove(event.getMessage().getId());
                Random random = new Random();
                CardType type = cardTypes.get(event.getMessage().getId());
                
                Card card = new Card(Generator.createUniqueCardId(), event.getMember().getId(), type,
                        random.nextInt(-20, 21),
                        random.nextInt(-20, 21));
                
                /*CardCatchEvent cardCatchEvent = new CardCatchEvent(card, event.getMember().getId());
                
                if (EventManager.dispatchEvent(cardCatchEvent)) {
                    event.reply(cardCatchEvent.getMessage()).setEphemeral(true).queue();
                    return;
                }*/
                
                event.reply("Correct!").setEphemeral(true).queue();
                event.getMessageChannel().sendMessage(event.getMember().getAsMention() +
                        " Has caught a CrimsonBall.\n" + card).queue();
                cardTypes.remove(event.getMessage().getId());
                event.getMessage().editMessageComponents(
                        ActionRow.of(buttons.get(event.getMessage().getId()).asDisabled())
                ).queue();
                buttons.remove(event.getMessage().getId());
                
                
                try {
                    database.addCard(card);
                    /*if (!database.hasStats(event.getMember().getId())) {
                        database.registerStats(event.getMember().getId());
                        database.setStat(
                                event.getMember().getId(),
                                "cards_caught",
                                database.getUserCards(event.getMember().getId()).size()
                        );
                    } else {
                        database.setStat(event.getMember().getId(), "cards_caught",
                                database.getStat(event.getMember().getId(), "cards_caught") + 1);
                    }*/
                    
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                
            } else {
                event.reply("Wrong Answer!").setEphemeral(true).queue();
            }
        }
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
                                new SubcommandData("open", "Shows shop menu"),
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
    
    @Override
    public void onGuildJoin(GuildJoinEvent e) {
        registerGuildCommands(e.getGuild());
    }
    
    public static JDA getJda() {
        return jda;
    }
    public static Database getDatabase() {
        return database;
    }
    
}