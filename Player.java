// Author: Jane Seah
import java.util.ArrayList;

public class Player {
    // Instance variables
    private int id;
    private ArrayList<Card> cards;
    private int numOfChips;
    private Player next;
    private boolean isPC = false;
    private boolean sureWin = false;
    private ArrayList<Character> decisions = new ArrayList<>();

    // Constructor
    public Player(int id) {
        this.id = id;
        cards = new ArrayList<>();
        numOfChips = 11;
        next = null;
    }

    // Getters
    public int getID() {
        return id;
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public int getNumChips() {
        return numOfChips;
    }

    public int getScore() {
        int score = -numOfChips;
        arrangeCards();
        int numCards = cards.size();
        if (numCards > 0) {
            for (int i = 0; i < numCards - 1; i++) {
                int cardPoints = cards.get(i).getNumber();
                score += cardPoints;
                int nextCardPoints = cards.get(i + 1).getNumber();
                // If next card is 1 more than previous card, don't add.
                if (nextCardPoints - cardPoints == 1) {
                    score -= nextCardPoints;
                }
            }
            score += cards.get(numCards - 1).getNumber();
        }

        return score;
    }

    public Player getNextPlayer() {
        return next;
    }

    public boolean isComputer() {
        return isPC;
    }

    // Setter
    public void setNextPlayer(Player otherPlayer) {
        next = otherPlayer;
    }

    public void setPlayerAsPC(boolean set) {
        isPC = set;
    }

    // Other methods
    public void take(Card c) {
        cards.add(c);
        numOfChips += c.getChips();
        System.out.println("Player " + id + " took card " + c.getNumber() + ".");
    }

    public boolean pass(Card c) {
        if (numOfChips > 0) {
            numOfChips--;
            System.out.println("Player " + id + " paid one chip to pass the card.");
            return true;
        }

        System.out.println("\nPlayer " + id + " doesn't have enough chips to pass the card.\n");
        take(c);
        return false;
    }

    public ArrayList<Card> arrangeCards() {
        if (cards.size() > 0) {
            ArrayList<Card> ordered = new ArrayList<>();
            for (int i = 3; i <= 35; i++) {
                for (Card c : cards) {
                    if (c.getNumber() == i) {
                        ordered.add(c);
                    }
                }
            }
            cards = ordered;
        }

        return cards;
    }

    public String displayCards() {
        String cardString = "[";
        for (Card c : cards) {
            cardString += c.getNumber() + " ";
        }
        cardString = cardString.trim() + "]";
        return cardString;
    }

    public boolean isInRange(Card openCard, int range) {
        int openNum = openCard.getNumber();
        arrangeCards();
        int lowerLimit = cards.get(0).getNumber();
        int upperLimit = cards.get(cards.size() - 1).getNumber();
        if (openNum < lowerLimit && lowerLimit - openNum <= range) {
            return true;
        } else if (openNum > upperLimit && openNum - upperLimit <= range) {
            return true;
        }

        return false;
    }

    public boolean isWithinSequence(Card openCard) {
        int openNum = openCard.getNumber();
        if (openNum > cards.get(0).getNumber() && openNum < cards.get(cards.size() - 1).getNumber() || isInRange(openCard, 1)) {
            return true;
        }
        return false;
    }

    public boolean isLastCard(Card last) {
        int holes = 0;
        Card toCompare = null;
        for (int i = 0; i < cards.size() - 1; i++) {
            if (cards.get(i+1).getNumber() - cards.get(i).getNumber() == 2) {
                holes++;
                toCompare = cards.get(i);
            }
        }

        if (holes == 1 && toCompare.getNumber() + 1 == last.getNumber()) {
            return true;
        }

        return false;
    }

    public int missingCards() {
        int holes = 0;
        for (int i = 0; i < cards.size() - 1; i++) {
            if (cards.get(i+1).getNumber() - cards.get(i).getNumber() == 2) {
                holes++;
            }
        }
        return holes;
    }

    public char decide(int count, Card card, PlayerManager pm, Player me) {
        char output = 'N';

        // If sure-win, keep taking cards
        if (sureWin) {
            decisions.add('Y');
            return 'Y';
        }

        // If no more chips, can only take
        if (numOfChips == 0) {
            decisions.add('Y');
            return 'Y';
        }

        // If net points of card is zero, take
        if (card.getNumber() - card.getChips() <= 0) {
            decisions.add('Y');
            return 'Y';
        }

        // First card played
        if (count == 1) {
            // If I am player 4 or 5, it's a sure-win.
            if (id == 4 || id == 5) {
                sureWin = true;
            }
            output = 'Y';
        } else if (pm.numPlayersWithCards() == 1 && card.getChips() >= 5) { // If only one player has taken cards and I have >= 5 chips, take.
            // If I'm the only player who has cards, it's a sure-win.
            if (cards.size() > 0) {
                sureWin = true;
            }
            output = 'Y';
        } else { // More than 1 player has taken cards: Go to method useStrategy().
            output = useStrategy(card, pm, me);
        }

        decisions.add(output);
        return output;
    }

    public char useStrategy(Card card, PlayerManager pm, Player me) {
        char output = 'N';
        System.out.println("Using strategy.");
        if (cards.size() == 0) { // If I haven't taken any cards
            // And the card has potential to expand 5 above or below. (No card within this range has been taken by another player.)
            if (pm.cardHasRange(card, 5)) {
                System.out.println("First card with range");
                output = 'Y';
            }
        } else if (cards.size() == 1) { // If I have taken a card
            // And the open card is within my range, take it.
            if (isInRange(card, 5)) {
                System.out.println("Second card and within range");
                output = 'Y';
            }
        } else if (cards.size() > 1) { // If I have taken more than one card
            // And the card is within my sequence.
            if (isWithinSequence(card)) {
                System.out.println("Within sequence");
                // And I can't milk the card
                if (!pm.canMilk(card, me, isLastCard(card))) {
                    System.out.println("Cannot milk.");
                    output = 'Y';
                } else if (numOfChips < 6 && card.getNumber() == 35) { // I can milk the card
                    System.out.println("A");
                    output = 'N';
                } else if (numOfChips > 6 && card.getNumber() == 35) { // Can't afford to milk so soon(?)
                    if (card.getNumber() - card.getChips() <= 9) {
                        System.out.println("B");
                        output = 'Y';
                    }
                } else if (numOfChips <= 6 && card.getNumber() < 14) {
                    System.out.println("C");
                    output = 'Y';
                } else if (numOfChips < 6 && card.getNumber() >= 14) {
                    if (card.getChips() >= 5) {
                        System.out.println("D");
                        output = 'Y';
                    }
                } else if (numOfChips > 5) {
                    // If I'm missing more than 3 cards, I can't milk it.
                    if (missingCards() > 3) {
                        System.out.println("E");
                        output = 'Y';
                    } else if (missingCards() == 2 || missingCards() == 3) { // If I'm missing 2 or 3 cards, milk one round.
                        if (pm.canMilk(card, me, false)) {
                            System.out.println("F");
                            output = 'N';
                        }

                        if (decisions.get(decisions.size() - 1) == 'N') {
                            System.out.println("G");
                            output = 'Y';
                        }
                    } else if (missingCards() == 1) { // If I'm only missing one card, milk until one player has no more chips.
                        if (pm.canMilk(card, me, true) && card.getNumber() - card.getChips() >= 5) {
                            System.out.println("H");
                            output = 'N';
                        }
                    }
                }
            } else { // Card not within my sequence
                if (pm.cardHasRange(card, 3) && missingCards() < 3) {
                    // incorrect
                    System.out.println("I");
                    output = 'Y';
                } else if (isInRange(card, 2) && missingCards() < 2) {
                    System.out.println("J");
                    output = 'Y';
                }
            }
        } else {
            // If my sequence has been completed, start a new range if it has potential.
            if (missingCards() == 0 && pm.cardHasRange(card, 4)) {
                System.out.println("Range completed and new card has range");
                output = 'Y';
            }
        }

        decisions.add('Y');
        return output;
    }
}
