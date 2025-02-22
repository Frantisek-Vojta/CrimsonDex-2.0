package me.kub94ek.command.impl.commands;

import me.kub94ek.command.Command;
import me.kub94ek.command.CommandExecutor;
import me.kub94ek.command.impl.executors.BattleCommandExecutor;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.Map;

public class BattleCommand implements Command {
    
    BattleCommandExecutor executor = new BattleCommandExecutor();
    
    @Override
    public CommandExecutor getExecutor() {
        return executor;
    }
    
    
    @Override
    public SlashCommandData getCommand() {
        SlashCommandData command = Commands.slash("battle", "Command for managing battles");
        command.addSubcommands(
                new SubcommandData("request", "Send battle request")
                        .addOptions(
                                new OptionData(
                                        OptionType.USER,
                                        "user", "The user to send the request to",
                                        true
                                ).setDescriptionLocalizations(
                                        Map.of(
                                                DiscordLocale.CZECH, "Komu žádost poslat"
                                        )
                                )
                        )
                        .setDescriptionLocalizations(
                                Map.of(
                                        DiscordLocale.CZECH, "Poslat žádost o souboj"
                                )
                        ),
                new SubcommandData("accept", "Accept battle request")
                        .addOptions(
                                new OptionData(
                                        OptionType.USER,
                                        "user", "The request sender",
                                        true
                                ).setDescriptionLocalizations(
                                        Map.of(
                                                DiscordLocale.CZECH, "Od koho žádost přijmout"
                                        )
                                )
                        )
                        .setDescriptionLocalizations(
                                Map.of(
                                        DiscordLocale.CZECH, "Přijmout žádost o souboj"
                                )
                        ),
                new SubcommandData("deny", "Deny battle request")
                        .addOptions(
                                new OptionData(
                                        OptionType.USER,
                                        "user", "The request sender",
                                        true
                                ).setDescriptionLocalizations(
                                        Map.of(
                                                DiscordLocale.CZECH, "Od koho žádost odmítnout"
                                        )
                                )
                        )
                        .setDescriptionLocalizations(
                                Map.of(
                                        DiscordLocale.CZECH, "Odmítnout žádost o souboj"
                                )
                        ),
                new SubcommandData("add", "Add card to battle")
                        .addOptions(
                                new OptionData(
                                        OptionType.STRING,
                                        "id", "ID of the card to add (without the #)",
                                        true
                                ).setDescriptionLocalizations(
                                        Map.of(
                                                DiscordLocale.CZECH, "ID karty na přidání"
                                        )
                                )
                        )
                        .setDescriptionLocalizations(
                                Map.of(
                                        DiscordLocale.CZECH, "Přidat kartu do souboje"
                                )
                        ),
                new SubcommandData("remove", "Remove card from battle")
                        .addOptions(
                                new OptionData(
                                        OptionType.STRING,
                                        "id", "ID of the card to remove (without the #)",
                                        true
                                ).setDescriptionLocalizations(
                                        Map.of(
                                                DiscordLocale.CZECH, "ID karty na odstranění"
                                        )
                                )
                        )
                        .setDescriptionLocalizations(
                                Map.of(
                                        DiscordLocale.CZECH, "Odstranit kartu ze souboje"
                                )
                        ),
                new SubcommandData("list", "List all cards in the battle")
                        .setDescriptionLocalizations(
                                Map.of(
                                        DiscordLocale.CZECH, "Vypsat seznam karet v souboji"
                                )
                        ),
                new SubcommandData("stats", "Shows your stats")
                        .setDescriptionLocalizations(
                                Map.of(
                                        DiscordLocale.CZECH, "Ukáže vaše statistiky"
                                )
                        ),
                new SubcommandData("cancel",
                        "Cancels requested battle (only works if the battle wasn't started yet)")
                        .setDescriptionLocalizations(
                                Map.of(
                                        DiscordLocale.CZECH, "Zruší požadovaný souboj (funguje jen před započnutím souboje)"
                                )
                        ),
                new SubcommandData("start", "Starts the battle")
                        .setDescriptionLocalizations(
                                Map.of(
                                        DiscordLocale.CZECH, "Započne souboj"
                                )
                        )
        );
        
        
        return command;
    }
    
}
