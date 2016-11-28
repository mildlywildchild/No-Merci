// Author: Jane Seah
import java.util.*;

public class PlayerManager {
    // Instance variables
    private ArrayList<Player> players;
    private Random rdm = new Random();

    // Constructor
    public PlayerManager(int numPlayers, boolean havePC) {
        players = new ArrayList<Player>();

        for (int i = 0; i < numPlayers; i++) {
            Player p = new Player(i + 1);
            players.add(p);
        }

        for (int i = 0; i < numPlayers; i++) {
            Player p = players.get(i);
            p.setPlayerAsPC(true);
            // if (i % 2 == 1) {
            //     p.setPlayerAsPC();
            // }

            if (i == numPlayers - 1) {
                p.setNextPlayer(players.get(0));
            } else {
                p.setNextPlayer(players.get(i + 1));
            }
        }

        // int rdmID = rdm.nextInt(numPlayers);
        // players.get(rdmID).setPlayerAsPC(false);
    }

    // Getters
    public ArrayList<Player> getPlayers() {
        return players;
    }

    public Player getWinner() {
        int maxScore = 616;
        Player winner = null;
        for (Player p : players) {
            if (p.getScore() < maxScore) {
                winner = p;
                maxScore = p.getScore();
            }
        }
        return winner;
    }

    public ArrayList<Card> getSpecificPlayersCards(int id) {
        return players.get(id - 1).getCards();
    }

    // Other methods
    public void display() {
        System.out.println("\n======= PLAYER STATS =======");
        System.out.println("Player\tScore\tChips\tCards");
        for (Player p : players) {
            p.arrangeCards();
            String playerName = "" + p.getID();
            if (p.isComputer()) {
                playerName += "(PC)";
            }
            System.out.println(playerName + "\t" + p.getScore() + "\t" + p.getNumChips() + "\t" + p.displayCards());
        }
    }

    public boolean enoughPC(int numPC) {
        int count = 0;
        for (Player p : players) {
            if (p.isComputer()) {
                count++;
            }
        }
        if (count == numPC) {
            return true;
        }
        return false;
    }

    public int numPlayersWithCards() {
        int count = 0;
        for (Player p : players) {
            if (!p.getCards().isEmpty()) {
                count++;
            }
        }
        return count;
    }

    public boolean cardHasRange(Card openCard, int range) {
        int openNum = openCard.getNumber();
        for (Player p : players) {
            for (Card c : p.getCards()) {
                int n = c.getNumber();
                if ((n < openNum && openNum - n <= range) || (n > openNum && n - openNum <= range)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean canMilk(Card openCard, Player exception, boolean finalCard) {
        if (openCard.getNumber() > 10) {
            int limit = 6;
            if (finalCard) {
                limit = 1;
            }
            for (Player p : players) {
                if (p != exception && p.getNumChips() < limit) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
