package game;

import java.util.Optional;

public class Card implements Comparable<Card> {
	
	private CardColor color;
	private int number;
	private Player player;
	
	public Card() {
		
	}
	
	public Card(CardColor color, int number) {
		this.color = color;
		this.number = number;
	}

	public CardColor getColor() {
		return color;
	}

	public void setColor(CardColor color) {
		this.color = color;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public Optional<Player> getPlayer() {
		return Optional.ofNullable(this.player);
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	@Override
	public String toString() {
		return color.getAnsiColor() + "[" + color + " - " + number + "]" + CardColor.ANSI_RESET;
	}

	@Override
	public int compareTo(Card card) {
		if (this.getColor().equals(card.getColor())) {
			if (this.getNumber() >= card.getNumber()) {
				return 1;
			} else {
				return -1;
			}
		}
		
		return 0;
	}
	
}