package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Player {

	private int id;
	private List<Card> playerDeck;
	private boolean isBotPlayer;

	public Player(int id, boolean isBotPlayer) {
		this.id = id;
		this.playerDeck = new ArrayList<>();
		this.isBotPlayer = isBotPlayer;
	}

	public Player(List<Card> playerDeck) {
		this.playerDeck = playerDeck;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<Card> getPlayerDeck() {
		return playerDeck;
	}

	public void setPlayerDeck(List<Card> playerDeck) {
		this.playerDeck = playerDeck;
	}
	
	public boolean isBotPlayer() {
		return isBotPlayer;
	}

	public void setBotPlayer(boolean isBotPlayer) {
		this.isBotPlayer = isBotPlayer;
	}

	public void pickCard(Card card, List<Card> listPlayedCards) {
		if (card.getPlayer().isPresent()) {
			return;
		}
		
		listPlayedCards.remove(card);
		card.setPlayer(this);
		this.playerDeck.add(0, card);
		Collections.sort(this.playerDeck);
	}
	
	public void playCard(Card card, List<Card> listPlayedCards) {
		if (!this.equals(card.getPlayer().orElseThrow())) {
			return;
		}

		listPlayedCards.add(0, card);
		card.setPlayer(null);
		this.playerDeck.remove(card);
		Collections.sort(this.playerDeck);
		
		if (this.playerDeck.size() == 1) {
			System.out.println("Player " + this.getId() + " has a UNO!");
		}
	}

	public boolean hasWon() {
		return this.getPlayerDeck().isEmpty();
	}

	@Override
	public String toString() {
		return "Player [id=" + id + ", isBotPlayer=" + isBotPlayer + "]";
	}
	
}
