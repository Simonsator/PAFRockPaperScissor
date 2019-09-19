package de.simonsator.partyandfriends.extensions.rockpaperscissor;

import de.simonsator.partyandfriends.api.PAFExtension;
import de.simonsator.partyandfriends.extensions.rockpaperscissor.commands.RPSCommand;
import de.simonsator.partyandfriends.extensions.rockpaperscissor.configuration.RPSConfiguration;
import de.simonsator.partyandfriends.friends.commands.Friends;
import de.simonsator.partyandfriends.utilities.ConfigurationCreator;
import net.md_5.bungee.api.ProxyServer;

import java.io.File;
import java.io.IOException;

public class RPSPlugin extends PAFExtension {
	@Override
	public void onEnable() {
		try {
			ConfigurationCreator configuration = new RPSConfiguration(new File(getConfigFolder(), "config.yml"), this);
			RPSCommand command = new RPSCommand(configuration.getStringList("Commands.RPSCommand.Names"), configuration.getInt("Commands.RPSCommand.Priority"),
					configuration.getString("Commands.RPSCommand.HelpMessage"), configuration.getString("Commands.RPSCommand.Permission"), configuration);
			Friends.getInstance().addCommand(command);
			ProxyServer.getInstance().getPluginManager().registerListener(this, command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
