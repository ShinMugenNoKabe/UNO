package game;

public enum CardColor {
	RED("\u001B[31m"),
	GREEN("\u001B[32m"),
	YELLOW("\u001B[33m"),
	BLUE("\u001B[34m");
	
	public final String ansiColor;
	public static final String ANSI_RESET = "\u001B[0m";
	
	CardColor(String ansiColor) {
		this.ansiColor = ansiColor;
	}

	public String getAnsiColor() {
		return ansiColor;
	}
}
