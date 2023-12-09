package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Game {
	
	private final List<Card> deckOfCards;
	private final List<Card> listPlayedCards;
	private final Scanner sc;
	private final List<Player> listPlayers;
	
	private static final int MAX_CARD_NUMBER = 9;
	
	private static final int MINIMUM_NUMBER_OF_PLAYERS = 2;
	private static final int MAXIMUM_NUMBER_OF_PLAYERS = 8;
	
	private static final int MAXIMUM_OF_INITIAL_CARDS_FOR_A_PLAYER = 5;
	
	public Game() {
		this.sc = new Scanner(System.in);
		this.listPlayedCards = new ArrayList<>();
		
		this.deckOfCards = new ArrayList<>();
		initializeDeck();
		
		this.listPlayers = new ArrayList<>();
		initializePlayers();
	}
	
	private void initializeDeck() {
		CardColor[] colors = CardColor.values();
		
		for (CardColor color : colors)  {
			for (int i = 0; i <= MAX_CARD_NUMBER; i++) {
				Card c = new Card(color, i);
				deckOfCards.add(c);
			}
		}
		
		shuffleDeckOfCards();
	}
	
	private void initializePlayers() {
		int numberOfPlayers = askForNumberOfPlayers();
		
		Player player1 = askForPlayerOne();
		listPlayers.add(player1);
		
		for (int i = 1; i < numberOfPlayers; i++) {
			Player botPlayer = new Player((i + 1), true);
			listPlayers.add(botPlayer);
		}

        for (Player player : listPlayers) {
            setPlayerCards(player);
        }
	}
	
	private int askForNumberOfPlayers() {
		int numberOfPlayers;
		
		while (true) {
			System.out.println("How many players do you want? (" + MINIMUM_NUMBER_OF_PLAYERS + " - " + MAXIMUM_NUMBER_OF_PLAYERS + "): ");
			numberOfPlayers = sc.nextInt();
			
			boolean isValidNumberOfPlayers = (numberOfPlayers >= MINIMUM_NUMBER_OF_PLAYERS && numberOfPlayers <= MAXIMUM_NUMBER_OF_PLAYERS);
			
			if (!isValidNumberOfPlayers)  {
				System.out.println("Invalid number of players!");
			} else {
				break;
			}
		}
		
		sc.nextLine(); // Clear buffer
		
		return numberOfPlayers;
	}
	
	private Player askForPlayerOne() {
		Player player1 = null;
		
		while (player1 == null) {
			System.out.println("Would you like to play? Y/N:");
			String userWillPlayResponse = sc.nextLine();
			
			boolean player1WillPlay = userWillPlayResponse.equalsIgnoreCase("Y");
			boolean player2WillNotPlay = userWillPlayResponse.equalsIgnoreCase("N");
			
			boolean isValidResponse = (player1WillPlay || player2WillNotPlay);
			
			if (!isValidResponse) {
				System.out.println("Invalid option!");
				continue;
			}
			
			player1 = new Player(1, !player1WillPlay);
		}
		
		return player1;
	}
	
	private void setPlayerCards(Player player) {
		int startIndex = ((player.getId() - 1) * MAXIMUM_OF_INITIAL_CARDS_FOR_A_PLAYER);
		
		for (int i = startIndex; i < startIndex + MAXIMUM_OF_INITIAL_CARDS_FOR_A_PLAYER; i++) {
			Card card = deckOfCards.get(i);
			player.pickCard(card, listPlayedCards);
		}
	}
	
	public void start() {
		boolean gameIsOver = false;
		
		while (!gameIsOver) {
			for (Player player : listPlayers) {
				System.out.println();
				System.out.println("Player " + player.getId() + " turn:");
				
				if (player.isHuman()) {
					player1Turn(player);
				} else {
					botPlayerTurn(player);
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
	
	private void botPlayerTurn(Player player) {
		Optional<Card> availableCard = player.getPlayerDeck()
				.stream()
				.filter(card -> isValidMode(card))
				.findAny();
		
		if (!availableCard.isEmpty()) {
			player.playCard(availableCard.orElseThrow(), listPlayedCards);
			return;
		}
		
		player.playCard(pickRandomCard(player), listPlayedCards);
	}
	
	private void player1Turn(Player player) {
		pickPlayer1CardIfNoOptionsAvailable(player);
		showPlayer1CurrentCards(player);

		List<Card> listAvailableCardsToBePlayed = player.getPlayerDeck()
				.stream()
				.filter(card -> isValidMode(card))
				.toList();
		
		showPlayer1AvailableCardOptions(listAvailableCardsToBePlayed);
		
		System.out.println("\nChoose your next move: ");
		int selectedCardIndex = sc.nextInt() - 1;
		
		boolean cardIndexExists = (selectedCardIndex >= 0 && selectedCardIndex < listAvailableCardsToBePlayed.size());
		
		if (cardIndexExists) {
			Card selectedCard = listAvailableCardsToBePlayed.get(selectedCardIndex);
			player.playCard(selectedCard, listPlayedCards);
		}
	}
	
	private void pickPlayer1CardIfNoOptionsAvailable(Player player) {
		boolean playerCanPlayAnyCard = player.getPlayerDeck()
				.stream()
				.anyMatch(card -> isValidMode(card));
		
		if (!playerCanPlayAnyCard) {
			System.out.println("No possible options!");
			pickRandomCard(player);
		}
	}
	
	private void showPlayer1CurrentCards(Player player) {
		System.out.println("\nYour current cards are: ");
		
		player.getPlayerDeck().forEach(availableCard ->  {
			System.out.print(availableCard + ", ");
		});
	}
	
	private void showPlayer1AvailableCardOptions(List<Card> listAvailableCardsToBePlayed) {
		System.out.println("\nYour possible options are: ");
		
		for (int i = 0; i < listAvailableCardsToBePlayed.size(); i++) {
			Card availableCard = listAvailableCardsToBePlayed.get(i);
			System.out.print((i + 1) + ": " + availableCard + ", ");
		}
		
		System.out.print("Other: don't play card");
	}
	
	private boolean isValidMode(Card card) {
		if (listPlayedCards.isEmpty()) {
			return true;
		}
		
		Card lastPlayedCard = listPlayedCards.get(0);
		
		return (card.getPlayer().isPresent() &&
				(card.getColor().equals(lastPlayedCard.getColor()) || card.getNumber() == lastPlayedCard.getNumber()));
	}
	
	private Card pickRandomCard(Player player) {
		shuffleDeckOfCards();
		
		List<Card> listAvailableCards = deckOfCards
				.stream()
				.filter(card -> card.isAvailableToBePickedUp())
				.toList();
		
		// Keep picking up cards until one can be played
		for (Card card : listAvailableCards) {
			player.pickCard(card, listPlayedCards);
			
			System.out.println("Player " + player.getId() + " picked card " + card);
			
			if (isValidMode(card)) {
				return card;
			}
		}
		
		return null;
	}
	
	private void shuffleDeckOfCards() {
		Collections.shuffle(deckOfCards);
	}

}
