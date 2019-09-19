package de.simonsator.partyandfriends.extensions.rockpaperscissor.configuration;

import de.simonsator.partyandfriends.api.PAFExtension;
import de.simonsator.partyandfriends.utilities.ConfigurationCreator;

import java.io.File;
import java.io.IOException;

/**
 * @author 00pfl
 * @version 1.0 09.06.2017.
 */
public class RPSConfiguration extends ConfigurationCreator {
	public RPSConfiguration(File pFile, PAFExtension pafExtension) throws IOException {
		super(pFile, pafExtension);
		readFile();
		loadDefaults();
		saveFile();
		process();
	}

	private void loadDefaults() {
		set("Commands.RPSCommand.Names", "rps", "rpschallenge");
		set("Commands.RPSCommand.Priority", 10);
		set("Commands.RPSCommand.Permission", "");
		set("Commands.RPSCommand.HelpMessage", "&8/&5friend rps [Friend] [Rock/Paper/Scissor] &8- &7Play rock, paper, scissor against your friends");
		set("Choice.Rock", "rock", "stone");
		set("Choice.Paper", "paper", "sheet");
		set("Choice.Scissor", "scissor", "scissors");
		set("Messages.NeedToChooseEither", " &7You either need to choose &brock, &bpaper or &bscissor&7.");
		set("Messages.AlreadySendRequest", " &7You already send this friend a request to play rock, paper, scissor.");
		set("Message.YourFriendChoose", " &7Your friend did choose &b[FRIENDS_CHOICE]&7.");
		set("Message.YouWin", " &aYou have won.");
		set("Message.YouLoose", " &cYou have lost.");
		set("Message.RequestWasSend", " &7Your request was send.");
		set("Message.RequestReceived", " &7You received a rock paper scissor request from your friend &e[REQUESTER]&7.");
		set("Message.Tie", " &6It's a tie.");
	}
}
