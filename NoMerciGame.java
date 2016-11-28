// Author: Jane Seah
import java.util.*;

public class NoMerciGame {
    public static void main(String[] args) {
        // Instructions
        System.out.println("=========== NO MERCI! ===========");
        System.out.println("Designer:\tThorsten Gimmler");
        // System.out.println("Programmer:\tJane Seah");
        System.out.println("---------------------------------\n");
        System.out.println("========== DESCRIPTION ==========");
        System.out.println("No Thanks! is a card game designed to be as simple as it is engaging.\n");
        System.out.println("The rules are simple. Each turn, players have two options:");
        System.out.println("- play one of their chips to avoid picking up the current face-up card");
        System.out.println("- pick up the face-up card (along with any chips that have already been played on that card) and turn over the next card\n");
        System.out.print("However, the choices aren't so easy as players compete to have the lowest score at the end of the game. ");
        System.out.print("The deck of cards is numbered from 3 to 35, with each card counting for a number of points equal to its face value. ");
        System.out.print("Runs of two or more cards only count as the lowest value in the run - but nine cards are removed from the deck before starting, so be careful looking for connectors. ");
        System.out.println("Each chip is worth -1 point, but they can be even more valuable by allowing you to avoid drawing that unwanted card.");

        System.out.println("\nREFERENCE: https://boardgamegeek.com/boardgame/12942/no-thanks/credits");

        // GAME STARTS
        Scanner sc = new Scanner(System.in);
        boolean havePC = false;

        // Prompt for number of players
        System.out.print("\nHow many players? >");
        int n = sc.nextInt();

        System.out.print("\nPlay against a computer? >");
        char haveComputer = sc.next().toUpperCase().charAt(0);
        if (haveComputer == 'Y') {
            havePC = true;
        }

        PlayerManager playerMgr = new PlayerManager(n, havePC);
        CardDeck cd = new CardDeck();

        System.out.println("\n<< LET'S BEGIN! >>\n");
        playerMgr.display();

        ArrayList<Card> deck = cd.getDeck();
        ArrayList<Player> players = playerMgr.getPlayers();
        Player currentPlayer = players.get(0);

        // Game keeps going until the card deck runs out
        int count = 1;
        while (!deck.isEmpty()) {
            if (currentPlayer.isComputer()) {
                System.out.println("\nComputer's turn.");
                System.out.println("Computer has " + currentPlayer.getNumChips() + " chip(s) and the following card(s): " + currentPlayer.displayCards() + "\n");
            } else {
                System.out.println("\nPlayer " + currentPlayer.getID() + "'s turn.");
                System.out.println("Player " + currentPlayer.getID() + " has " + currentPlayer.getNumChips() + " chip(s) and the following card(s): " + currentPlayer.displayCards() + "\n");
            }

            // Opens card at the top of the deck
            Card card = deck.get(0);
            System.out.println("Card " + count + ": " + card.getNumber());
            System.out.println("Chips on the card: " + card.getChips());

            // Player's decision point
            char input;
            do {
                System.out.print("Take card? [Y/N] >");
                if (currentPlayer.isComputer()) {
                    // in progress
                    input = currentPlayer.decide(count, card, playerMgr, currentPlayer);
                } else {
                    input = sc.next().toUpperCase().charAt(0);
                    sc.nextLine();
                }

                if (input == 'Y') {
                    currentPlayer.take(card);
                    deck.remove(0);
                    count++;
                } else if (input == 'N') {
                    boolean passed = currentPlayer.pass(card);
                    if (passed) {
                        card.increaseChips();
                        currentPlayer = currentPlayer.getNextPlayer();
                    } else {
                        deck.remove(0);
                        count++;
                    }
                } else {
                    System.out.println("Invalid response.");
                }
            } while (input != 'Y' && input != 'N');

            playerMgr.display();
        }

        // Announces winner
        System.out.println("Player " + playerMgr.getWinner().getID() + " won!");
        if (cd.cardsRemoved()) {
            cd.printRemovedCards();
        }
    }
}
