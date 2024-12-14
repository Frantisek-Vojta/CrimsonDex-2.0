package me.kub94ek.command;

import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public interface Command {
    CommandExecutor getExecutor();
    SlashCommandData getCommand();
    
}
