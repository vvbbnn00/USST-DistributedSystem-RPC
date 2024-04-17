package tech.bzpl.ecard.server;

import cn.vvbbnn00.rpc.server.annotation.RpcExposed;
import cn.vvbbnn00.rpc.server.base.RpcHandler;
import cn.vvbbnn00.rpc.server.context.Context;
import tech.bzpl.ecard.entity.Card;
import java.util.logging.Logger;
import java.util.regex.Pattern;

@RpcExposed
public class CialloServer extends RpcHandler {
    private final Logger l = Logger.getLogger("CialloHandler");
    private CialloCardManager manager;
    private final Context session = getSessionContext();
    private String entrypoint = null;

    public CialloServer() {
    }

    private String getEntrypoint() {
        if (entrypoint == null) {
            entrypoint = (String) session.getAttribute("remoteAddress");
        }
        return entrypoint;
    }

    private CialloCardManager getManager() {
        Context serverContext = getServerContext();
        manager = (CialloCardManager) serverContext.getAttribute("manager");
        if (manager == null) {
            manager = new CialloCardManager();
            serverContext.setAttribute("manager", manager);
        }

        return manager;
    }

    public Card checkBalance(String cardNumber) {
        if (!Pattern.matches(Card.CARD_NUMBER_REGEX, cardNumber)) {
            throw new IllegalArgumentException("Invalid Card Number.");
        }

        Card card = getManager().getCard(cardNumber);
        if (card == null) {
            throw new IllegalArgumentException("Card Not Found.");
        }

        l.info("[" + getEntrypoint() + "] Checked balance of card " + cardNumber + ": " + card.getBalance());
        return card;
    }

    public Card topUp(String cardNumber, long amount) {
        if (!Pattern.matches(Card.CARD_NUMBER_REGEX, cardNumber)) {
            throw new IllegalArgumentException("Invalid Card Number.");
        }

        Card card = getManager().getCard(cardNumber);
        if (card == null) {
            card = new Card(cardNumber);
            card.setBalance(amount);
            getManager().addCard(card);
        } else {
            card.setBalance(card.getBalance() + amount);
        }

        l.info("[" + getEntrypoint() + "] Topped up card " + cardNumber + " with " + amount + ", new balance: " + card.getBalance());
        return card;
    }

    public Card withdraw(String cardNumber, long amount) {
        if (!Pattern.matches(Card.CARD_NUMBER_REGEX, cardNumber)) {
            throw new IllegalArgumentException("Invalid Card Number.");
        }

        Card card = getManager().getCard(cardNumber);
        if (card == null) {
            throw new IllegalArgumentException("Card Not Found.");
        }
        if (card.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient Balance.");
        }
        card.setBalance(card.getBalance() - amount);

        l.info("[" + getEntrypoint() + "] Withdrawn " + amount + " from card " + cardNumber + ", new balance: " + card.getBalance());
        return card;
    }
}
