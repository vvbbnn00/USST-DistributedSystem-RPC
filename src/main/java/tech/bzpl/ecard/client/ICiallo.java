package tech.bzpl.ecard.client;

import tech.bzpl.ecard.entity.Card;

public interface ICiallo {
    Card checkBalance(String cardNumber);
    Card topUp(String cardNumber, long amount);
    Card withdraw(String cardNumber, long amount);
}
