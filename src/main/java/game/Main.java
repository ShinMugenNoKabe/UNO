package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

	public static void main(String[] args) {
		List<Card> deckOfCards = initializeDeck();
		List<Card> listPlayedCards = new ArrayList<>();
		
		Scanner sc = new Scanner(System.in);
		List<Player> listPlayers = initializePlayers(sc, deckOfCards, listPlayedCards);
		
		boolean gameIsOver = false;
		
		while (!gameIsOver) {
			for (Player player : listPlayers) {
				System.out.println();
				System.out.println("Player " + player.getId() + " turn:");
				
				if (player.isBotPlayer()) {
					botPlayerTurn(player, deckOfCards, listPlayedCards);
				} else {
					player1Turn(player, deckOfCards, listPlayedCards, sc);
				}
				
				if (player.hasWon()) {
					System.out.println("Player " + player.getId() + " has won!");
					gameIsOver = true;
					break;
				}
			}
		}

		sc.close();
	}
	
	private static List<Card> initializeDeck() {
		List<Card> deckOfCards = new ArrayList<>();
		CardColor[] colors = CardColor.values();
		
		for (CardColor color : colors)  {
			for (int i = 0; i <= 9; i++) {
				Card c = new Card(color, i);
				
				deckOfCards.add(c);
			}
		}
		
		Collections.shuffle(deckOfCards);
		
		return deckOfCards;
	}
	
	private static List<Player> initializePlayers(Scanner sc, List<Card> deckOfCards, List<Card> listPlayedCards) {
		int numberOfPlayers = 0;
		
		while (true) {
			System.out.println("How many players do you want? (2 - 8): ");
			numberOfPlayers = sc.nextInt();
			
			if (numberOfPlayers < 2 || numberOfPlayers > 8) {
				System.out.println("Invalid number of players!");
			} else {
				break;
			}
		}
		
		List<Player> listPlayers = new ArrayList<>();
		Player player1 = null;
		
		sc.nextLine(); // Clear buffer
		
		while (player1 == null) {
			System.out.println("Would you like to play? Y/N:");
			String userWillPlay = sc.nextLine();
			
			if (userWillPlay.equals("Y")) {
				player1 = new Player(1, false);
			} else if (userWillPlay.equals("N")) {
				player1 = new Player(1, true);
			} else {
				System.out.println("Invalid option!");
			}
		}
		
		listPlayers.add(player1);
		
		for (int i = 1; i < numberOfPlayers; i++) {
			Player botPlayer = new Player((i + 1), true);
			listPlayers.add(botPlayer);
		}
		
		for (int i = 0; i < listPlayers.size(); i++) {
			Player player = listPlayers.get(i);
			setPlayerCards(player, deckOfCards, listPlayedCards);
		}
		
		return listPlayers;
	}
	
	private static void setPlayerCards(Player player, List<Card> deckOfCards, List<Card> listPlayedCards) {
		int startIndex = ((player.getId() - 1) * 5);
		
		for (int i = startIndex; i < startIndex + 5; i++) {
			Card card = deckOfCards.get(i);
			player.pickCard(card, listPlayedCards);
		}
	}
	
	private static void player1Turn(Player player, List<Card> deckOfCards, List<Card> listPlayedCards, Scanner sc) {
		Stream<Card> streamAvailableCards = player.getPlayerDeck().stream().filter((c) -> isValidMode(c, listPlayedCards));
		
		if (streamAvailableCards.findAny().isEmpty()) {
			System.out.println("No possible options!");
			pickRandomCard(player, deckOfCards, listPlayedCards);
		}

		List<Card> listAvailableCards = player.getPlayerDeck().stream().filter((c) -> isValidMode(c, listPlayedCards)).collect(Collectors.toList());
		streamAvailableCards.close();
		
		System.out.println("Your current cards are: ");
		
		for (int i = 0; i < player.getPlayerDeck().size(); i++) {
			System.out.print(player.getPlayerDeck().get(i) + ", ");
		}
		
		System.out.println("\nYour possible options are: ");
		
		List<Integer> listAvailableCardNumbers = new ArrayList<>();
		
		for (int i = 0; i < listAvailableCards.size(); i++) {
			listAvailableCardNumbers.add(i);
			System.out.print((i + 1) + ": " + listAvailableCards.get(i) + ", ");
		}
		
		System.out.print("Other: don't play card");
		
		System.out.println("\nChoose your next move: ");
		int cardIndex = sc.nextInt() - 1;
		
		if (listAvailableCardNumbers.contains(cardIndex)) {
			playCardIfAvailable(player, listAvailableCards.get(cardIndex), listPlayedCards);
		} else {
			playCardIfAvailable(player, null, listPlayedCards);
		}
	}
	
	private static void botPlayerTurn(Player player, List<Card> deckOfCards, List<Card> listPlayedCards) {
		boolean playedCard = false;
		
		for (Card card : player.getPlayerDeck()) {
			if (isValidMode(card, listPlayedCards)) {
				playCardIfAvailable(player, card, listPlayedCards);
				playedCard = true;
				break;
			}
		}
		
		if (!playedCard) {
			Card card = pickRandomCard(player, deckOfCards, listPlayedCards);
			playCardIfAvailable(player, card, listPlayedCards);
		}
	}
	
	private static boolean isValidMode(Card card, List<Card> listPlayedCards) {
		if (listPlayedCards.isEmpty()) {
			return true;
		}
		
		Card lastPlayedCard = listPlayedCards.get(0);
		
		return (card.getPlayer().isPresent() &&
				(card.getColor().equals(lastPlayedCard.getColor()) || card.getNumber() == lastPlayedCard.getNumber()));
	}
	
	private static Card pickRandomCard(Player player, List<Card> deckOfCards, List<Card> listPlayedCards) {
		//listPlayedCards.clear();
		Collections.shuffle(deckOfCards);
		List<Card> listAvailableCards = deckOfCards.stream().filter((c) -> c.getPlayer().isEmpty()).collect(Collectors.toList());
		
		for (Card card : listAvailableCards) {
			player.pickCard(card, listPlayedCards);
			
			System.out.println("Player " + player.getId() + " picked card " + card);
			
			if (isValidMode(card, listPlayedCards)) {
				return card;
			}
		}
		
		return null;
	}
	
	private static void playCardIfAvailable(Player player, Card card, List<Card> listPlayedCards) {
		if (card == null) {
			return;
		}
		
		System.out.println("Player " + player.getId() + " played card " + card);
		player.playCard(card, listPlayedCards);
	}

}
