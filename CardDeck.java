// Author: Jane Seah
import java.util.*;

public class CardDeck {
    // Instance variable
    private ArrayList<Card> deck;
    private boolean nineRemoved;
    private ArrayList<Card> removedCards;

    // Constructor
    public CardDeck() {
        deck = new ArrayList<>();
        nineRemoved = false;
        removedCards = new ArrayList<>();

        ArrayList<Integer> numberList = new ArrayList<>();
        for (int i = 3; i <= 35; i++) {
            numberList.add(i);
        }

        while (!numberList.isEmpty()) {
            Random rdm = new Random();
            int index = rdm.nextInt(numberList.size());
            Card c = new Card(numberList.get(index));
            deck.add(c);
            numberList.remove(index);
        }

        Scanner sc = new Scanner(System.in);
        System.out.print("\nRemove nine cards? [Y/N] >");
        char input = sc.next().toUpperCase().charAt(0);

        if (input == 'Y') {
            nineRemoved = true;
            Random rdm = new Random();
            for (int i = 0; i < 9; i++) {
                int index = rdm.nextInt(deck.size());
                removedCards.add(deck.get(index));
                deck.remove(index);
            }
            System.out.println("Nine cards have been removed.");
        } else {
            System.out.println("The game will proceed without removing nine cards.");
        }
    }

    // Getter
    public ArrayList<Card> getDeck() {
        return deck;
    }

    public boolean cardsRemoved() {
        return nineRemoved;
    }

    public void printRemovedCards() {
        System.out.print("The following cards were removed: ");
        for (Card c : removedCards) {
            System.out.print(c.getNumber() + " ");
        }
        System.out.println();
    }
}
