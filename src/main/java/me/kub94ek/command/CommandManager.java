package me.kub94ek.command;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommandManager extends ListenerAdapter {
    
    private final JDA jda;
    private final HashMap<String, CommandExecutor> commandExecutors = new HashMap<>();
    private final List<String> commands = new ArrayList<>();
    
    public CommandManager(JDA jda) {
        this.jda = jda;
    }
    
    public void registerCommands(List<Command> commands) {
        List<SlashCommandData> slashCommandData = new ArrayList<>();
        commands.forEach(command -> {
            slashCommandData.add(command.getCommand());
            commandExecutors.put(command.getCommand().getName(), command.getExecutor());
        });
        
        jda.getGuilds().forEach(guild -> jda.updateCommands().addCommands(slashCommandData).queue());
    }
    
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        CommandExecutor commandExecutor = commandExecutors.get(e.getName());
        if (commandExecutor == null) {
            return;
        }
        
        commandExecutor.executeCommand(e);
        
    }
    
    public HashMap<String, CommandExecutor> getCommandExecutors() {
        return commandExecutors;
    }
    
}
