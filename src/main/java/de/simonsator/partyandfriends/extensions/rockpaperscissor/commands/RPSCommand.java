package de.simonsator.partyandfriends.extensions.rockpaperscissor.commands;

import de.simonsator.partyandfriends.api.friends.abstractcommands.FriendSubCommand;
import de.simonsator.partyandfriends.api.pafplayers.OnlinePAFPlayer;
import de.simonsator.partyandfriends.api.pafplayers.PAFPlayer;
import de.simonsator.partyandfriends.api.pafplayers.PAFPlayerManager;
import de.simonsator.partyandfriends.extensions.rockpaperscissor.RPSChoice;
import de.simonsator.partyandfriends.main.Main;
import de.simonsator.partyandfriends.utilities.ConfigurationCreator;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RPSCommand extends FriendSubCommand implements Listener {
	private List<RPSCollection> rpsCollectionList = new ArrayList<>();
	private final ConfigurationCreator CONFIGURATION;

	public RPSCommand(List<String> pCommands, int pPriority, String pHelp, String pPermission, ConfigurationCreator pConfig) {
		super(pCommands, pPriority, pHelp, pPermission);
		CONFIGURATION = pConfig;
	}

	@EventHandler
	public void onLeave(PlayerDisconnectEvent pEvent) {
		removeFromList(PAFPlayerManager.getInstance().getPlayer(pEvent.getPlayer()), 0);
	}

	private void removeFromList(OnlinePAFPlayer pPlayer, int start) {
		for (int i = start; i < rpsCollectionList.size(); i++) {
			RPSCollection rpsCollection = rpsCollectionList.get(i);
			if (rpsCollection.SENDER.equals(pPlayer) || rpsCollection.ASKED_FOR.equals(pPlayer)) {
				rpsCollectionList.remove(rpsCollection);
				removeFromList(pPlayer, i);
				return;
			}
		}
	}

	@Override
	public void onCommand(OnlinePAFPlayer pPlayer, String[] args) {
		if (!isPlayerGiven(pPlayer, args))
			return;
		PAFPlayer playerQuery = PAFPlayerManager.getInstance().getPlayer(args[1]);
		if (!isPlayerOnline(pPlayer, playerQuery))
			return;
		OnlinePAFPlayer friend = (OnlinePAFPlayer) playerQuery;
		if (!isAFriendOf(pPlayer, friend, args))
			return;
		if (args.length < 3) {
			sendError(pPlayer, PREFIX + new TextComponent(CONFIGURATION.getString("Messages.NeedToChooseEither")));
			return;
		}
		RPSChoice rpsChoice = convertToEnum(args[2]);
		if (rpsChoice == null) {
			sendError(pPlayer, new TextComponent(CONFIGURATION.getString("Messages.NeedToChooseEither")));
			return;
		}
		for (int i = 0; i < rpsCollectionList.size(); i++) {
			RPSCollection rpsCollection = rpsCollectionList.get(i);
			if (rpsCollection.SENDER.equals(pPlayer) && rpsCollection.ASKED_FOR.equals(friend)) {
				pPlayer.sendMessage(new TextComponent(PREFIX + CONFIGURATION.getString("Messages.AlreadySendRequest")));
				return;
			}
			if (rpsCollection.ASKED_FOR.equals(pPlayer) && rpsCollection.SENDER.equals(friend)) {
				pPlayer.sendMessage(PREFIX + CONFIGURATION.getString("Message.YourFriendChoose").replace("[FRIENDS_CHOICE]", Objects.requireNonNull(translate(rpsCollection.CHOICE))));
				friend.sendMessage(PREFIX + CONFIGURATION.getString("Message.YourFriendChoose").replace("[FRIENDS_CHOICE]", Objects.requireNonNull(translate(rpsChoice))));
				if (rpsCollection.CHOICE == rpsChoice) {
					pPlayer.sendMessage(CONFIGURATION.getString(PREFIX
							+ "Message.Tie"));
					friend.sendMessage(CONFIGURATION.getString(PREFIX
							+ "Message.Tie"));
				} else {
					boolean player1Wins = player1Wins(pPlayer, rpsCollection.SENDER, rpsChoice, rpsCollection.CHOICE);
					OnlinePAFPlayer winner = rpsCollection.SENDER;
					OnlinePAFPlayer looser = pPlayer;
					if (player1Wins) {
						winner = pPlayer;
						looser = rpsCollection.SENDER;
					}
					winner.sendMessage(PREFIX + CONFIGURATION.getString("Message.YouWin"));
					looser.sendMessage(PREFIX + CONFIGURATION.getString("Message.YouLoose"));
					rpsCollectionList.remove(rpsCollection);
				}
				return;
			}
		}
		sendRPSRequest(pPlayer, friend, rpsChoice);
	}

	private void sendRPSRequest(OnlinePAFPlayer pPlayer, OnlinePAFPlayer friend, RPSChoice rpsChoice) {
		rpsCollectionList.add(new RPSCollection(pPlayer, friend, rpsChoice));
		pPlayer.sendMessage(PREFIX + CONFIGURATION.getString("Message.RequestWasSend"));
		sendError(friend, new TextComponent(PREFIX + CONFIGURATION.getString("Message.RequestReceived").replace("[REQUESTER]", pPlayer.getDisplayName())));
	}

	private String translate(RPSChoice choice) {
		switch (choice) {
			case ROCK:
				return CONFIGURATION.getStringList("Choice.Rock").get(0);
			case PAPER:
				return CONFIGURATION.getStringList("Choice.Paper").get(0);
			case SCISSOR:
				return CONFIGURATION.getStringList("Choice.Scissor").get(0);
			default:
				return null;
		}
	}

	private boolean player1Wins(OnlinePAFPlayer pPlayer1, OnlinePAFPlayer pPlayer2, RPSChoice choicePlayer1, RPSChoice choicePlayer2) {
		switch (choicePlayer1) {
			case PAPER:
				return choicePlayer2 == RPSChoice.ROCK;
			case ROCK:
				return choicePlayer2 == RPSChoice.SCISSOR;
			case SCISSOR:
				return choicePlayer2 == RPSChoice.PAPER;
			default:
				return false;
		}
	}

	private RPSChoice convertToEnum(String chosen) {
		if (CONFIGURATION.getStringList("Choice.Rock").contains(chosen)) {
			return RPSChoice.ROCK;
		}
		if (CONFIGURATION.getStringList("Choice.Paper").contains(chosen)) {
			return RPSChoice.PAPER;
		}
		if (CONFIGURATION.getStringList("Choice.Scissor").contains(chosen)) {
			return RPSChoice.SCISSOR;
		}
		return null;
	}

	private boolean isPlayerOnline(OnlinePAFPlayer pSender, PAFPlayer pQueryPlayer) {
		if (!pQueryPlayer.isOnline()) {
			sendError(pSender, new TextComponent(PREFIX + Main.getInstance().getMessages().getString("Friends.General.PlayerIsOffline").
					replace("[PLAYER]", pQueryPlayer.getDisplayName())));
			return false;
		}
		return true;
	}

	private static class RPSCollection {
		private final OnlinePAFPlayer SENDER;
		private final OnlinePAFPlayer ASKED_FOR;
		private final RPSChoice CHOICE;

		private RPSCollection(OnlinePAFPlayer player1, OnlinePAFPlayer player2, RPSChoice choice) {
			SENDER = player1;
			ASKED_FOR = player2;
			CHOICE = choice;
		}
	}
}
