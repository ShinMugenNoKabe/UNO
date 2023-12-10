package game;

import java.util.Optional;

public class Card implements Comparable<Card> {

    private final CardColor color;
    private final int number;
    private Player player;

    protected static final int MAX_CARD_NUMBER = 9;

    public Card(CardColor color, int number) {
        this.color = color;
        this.number = number;
    }

    public CardColor getColor() {
        return color;
    }

    public int getNumber() {
        return number;
    }

    public Optional<Player> getPlayer() {
        return Optional.ofNullable(this.player);
    }

    public boolean isInAPlayerDeck() {
        return getPlayer().isPresent();
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setPlayerToEmpty() {
        setPlayer(null);
    }

    public boolean isAvailableToBePickedUp() {
        return getPlayer().isEmpty();
    }

    @Override
    public String toString() {
        return color.getAnsiColor() + "[" + color + " - " + number + "]" + CardColor.ANSI_RESET;
    }

    @Override
    public int compareTo(Card otherCard) {
        if (this.getColor().equals(otherCard.getColor())) {
            if (this.getNumber() >= otherCard.getNumber()) {
                return 1;
            } else {
                return -1;
            }
        }

        return 0;
    }

}