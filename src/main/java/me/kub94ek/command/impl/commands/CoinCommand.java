package me.kub94ek.command.impl.commands;

import me.kub94ek.command.Command;
import me.kub94ek.command.CommandExecutor;
import me.kub94ek.command.impl.executors.CoinCommandExecutor;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.Map;

public class CoinCommand implements Command {
    private static final CoinCommandExecutor executor = new CoinCommandExecutor();
    
    @Override
    public CommandExecutor getExecutor() {
        return executor;
    }
    
    @Override
    public SlashCommandData getCommand() {
        SlashCommandData command = Commands.slash("coin", "Command for managing coins");
        command.addSubcommands(
                new SubcommandData("bal", "Shows you your coin balance")
                        .setDescriptionLocalizations(
                                Map.of(
                                        DiscordLocale.CZECH, "Ukáže vám váš zůstatek mincí",
                                        DiscordLocale.SPANISH, "Muestra el saldo de monedas"
                                )
                        ),
                new SubcommandData("give", "Gives coins to somebody")
                        .addOptions(
                                new OptionData(
                                        OptionType.USER,
                                        "user", "The user to give the coins to",
                                        true
                                ).setDescriptionLocalizations(
                                        Map.of(
                                                DiscordLocale.CZECH, "Komu mince darovat",
                                                DiscordLocale.SPANISH, "A quién regalar las monedas"
                                        )
                                ),
                                new OptionData(
                                     OptionType.INTEGER,
                                        "coins", "How many coins to give",
                                        true
                                ).setDescriptionLocalizations(
                                        Map.of(
                                                DiscordLocale.CZECH, "Kolik mincí chcete darovat",
                                                DiscordLocale.SPANISH, "Cuántas monedas desea regalar"
                                        )
                                )
                        )
                        .setDescriptionLocalizations(
                                Map.of(
                                        DiscordLocale.CZECH, "Darujte někomu vaše mince",
                                        DiscordLocale.SPANISH, "Regala sus monedas a alguien"
                                )
                        )
        );
        
        
        return command;
    }
    
}
