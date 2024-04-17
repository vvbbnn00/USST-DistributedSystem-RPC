package tech.bzpl.ecard.client;

import tech.bzpl.ecard.client.util.CialloScanner;
import tech.bzpl.ecard.entity.Card;
import java.util.regex.Pattern;

public class CialloClientTerminal implements Runnable {
    private final ICiallo rpc;

    public CialloClientTerminal(ICiallo rpc) {
        this.rpc = rpc;
    }

    private void doCheckBalance() {
        String cardNumber = CialloScanner.getLine("Please enter the card number: ");
        if (!Pattern.matches(Card.CARD_NUMBER_REGEX, cardNumber)) {
            System.out.println("Invalid card number!");
            return;
        }
        try {
            Card card = rpc.checkBalance(cardNumber);
            System.out.println("Balance: CNY " + card.getBalance() / 100.0);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void doTopUp() {
        String cardNumber = CialloScanner.getLine("Please enter the card number: ");
        if (!Pattern.matches(Card.CARD_NUMBER_REGEX, cardNumber)) {
            System.out.println("Invalid card number!");
            return;
        }
        int amount = CialloScanner.getMoney("Please enter the amount to deposit: ");
        if (amount <= 0) {
            System.out.println("Invalid amount!");
            return;
        }
        try {
            Card card = rpc.topUp(cardNumber, amount);
            System.out.println("Top Up Success! New balance: CNY " + card.getBalance() / 100.0);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void doWithdraw() {
        String cardNumber = CialloScanner.getLine("Please enter the card number: ");
        if (!Pattern.matches(Card.CARD_NUMBER_REGEX, cardNumber)) {
            System.out.println("Invalid card number!");
            return;
        }
        int amount = CialloScanner.getMoney("Please enter the amount to withdraw: ");
        if (amount <= 0) {
            System.out.println("Invalid amount!");
            return;
        }

        try {
            Card card = rpc.withdraw(cardNumber, amount);
            System.out.println("Withdraw Success! New balance: CNY " + card.getBalance() / 100.0);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void menu() {
        System.out.println("======================");
        System.out.println("1. Check Balance");
        System.out.println("2. Top Up");
        System.out.println("3. Withdraw");
        System.out.println("0. Exit");
        System.out.println("======================");
    }

    @Override
    public void run() {
        while (true) {
            menu();
            int choice = CialloScanner.getInt("Enter your choice: ");
            switch (choice) {
                case 1 -> doCheckBalance();
                case 2 -> doTopUp();
                case 3 -> doWithdraw();
                case 0 -> {
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice");
            }
        }
    }
}