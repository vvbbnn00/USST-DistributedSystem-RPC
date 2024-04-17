package tech.bzpl.ecard.entity;

import java.io.Serializable;

public class Card implements Serializable {
    public static final String CARD_NUMBER_REGEX = "^[0-9A-Z]{5,}$";

    private final String cardNumber;
    private long balance;

    public Card(String cardNumber) {
        this.cardNumber = cardNumber;
        this.balance = 0;
    }

    public Card(String cardNumber, long balance) {
        this.cardNumber = cardNumber;
        this.balance = balance;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }
}