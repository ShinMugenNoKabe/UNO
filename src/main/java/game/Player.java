package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Player {

	private final int id;
	private final List<Card> playerDeck;
	private final boolean isBotPlayer;

	public Player(int id, boolean isBotPlayer) {
		this.id = id;
		this.playerDeck = new ArrayList<>();
		this.isBotPlayer = isBotPlayer;
	}

	public int getId() {
		return id;
	}

	public List<Card> getPlayerDeck() {
		return playerDeck;
	}
	
	public boolean isBotPlayer() {
		return isBotPlayer;
	}
	
	public boolean isHuman() {
		return !isBotPlayer;
	}

	public void pickCard(Card card, List<Card> listPlayedCards) {
		if (card.getPlayer().isPresent()) {
			return;
		}
		
		listPlayedCards.remove(card);
		addCardToDeck(card);

		sortDeck();
	}
	
	private void addCardToDeck(Card pickedCard) {
		pickedCard.setPlayer(this);
		playerDeck.addFirst(pickedCard);
	}
	
	public void playCard(Card card, List<Card> listPlayedCards) {
		if (card == null) {
			return;
		}
		
		if (!this.equals(card.getPlayer().orElseThrow())) {
			return;
		}
		
		System.out.println("Player " + getId() + " played card " + card);
		
		listPlayedCards.addFirst(card);
		removeCardFromPlayerDeck(card);
		sortDeck();
		
		if (hasUNO()) {
			System.out.println("Player " + this.getId() + " has a UNO!");
		}
	}

	private void sortDeck() {
		Collections.sort(playerDeck);
	}
	
	private void removeCardFromPlayerDeck(Card playedCard) {
		playedCard.setPlayerToEmpty();
		playerDeck.remove(playedCard);
	}

	private boolean hasUNO() {
		return playerDeck.size() == 1;
	}

	public boolean hasWon() {
		return playerDeck.isEmpty();
	}

}
