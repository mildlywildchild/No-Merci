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

    public void setPlayerAsPC() {
        isPC = true;
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

        System.out.println("\nPlayer " + id + " has not enough chips to pass the card.\n");
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
        if (openNum > cards.get(0).getNumber() && openNum < cards.get(cards.size() - 1).getNumber()) {
            System.out.println("Within sequence.");
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

    public char decide(int count, Card card, PlayerManager pm) {
        int cardNum = card.getNumber();
        int cardChips = card.getChips();
        char output = 'N';

        if (sureWin) {
            return 'Y';
        }

        if (count == 1) {
            if (id == 4 || id == 5) {
                sureWin = true;
            }
            output = 'Y';
        } else if (pm.numPlayersWithCards() == 1 && cardChips >= 5) {
            if (cards.size() > 0) {
                sureWin = true;
            }
            output = 'Y';
        } else {
            output = useStrategy(card, pm);
        }

        return output;
    }

    public char useStrategy(Card card, PlayerManager pm) {
        System.out.println("Using strategy");
        if (cards.size() == 0 && pm.cardHasRange(card, 4)) {
            System.out.println("A");
            return 'Y';
        } else if (cards.size() == 1 && isInRange(card, 5)) {
            System.out.println("B");
            return 'Y';
        } else if (cards.size() > 1) {
            if (isWithinSequence(card)) {
                if(!pm.canMilk(card, false)) {
                    System.out.println("C");
                    return 'Y';
                } else if (card.getNumber() == 35) {
                    System.out.println("D");
                    // not complete
                    return 'N';
                } else if (numOfChips <= 6 && card.getNumber() < 14){
                    return 'Y';
                } else if (numOfChips < 6 && card.getNumber() >= 14) {
                    return 'N';
                } else if (pm.canMilk(card, true)) {
                    return 'N';
                } else {
                    return 'Y';
                }
            } else {
                if (isInRange(card, 2) || (isInRange(card, 5) && pm.cardHasRange(card, 3))) {
                    System.out.println("E");
                    return 'Y';
                } else if (card.getChips() == card.getNumber()) {
                    System.out.println("F");
                    return 'Y';
                }
            }
        }
        System.out.println("Didn't meet any conditions");
        return 'N';
    }
}
