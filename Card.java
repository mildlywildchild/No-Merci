// Author: Jane Seah
public class Card {
    // Instance variables
    private int number;
    private int chips;

    // Constructor
    public Card(int n) {
        number = n;
        chips = 0;
    }

    // Getters
    public int getNumber() {
        return number;
    }

    public int getChips() {
        return chips;
    }

    // Modifiers
    public void increaseChips() {
        chips++;
    }
}
