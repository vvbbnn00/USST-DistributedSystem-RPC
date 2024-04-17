package tech.bzpl.ecard.server;

import tech.bzpl.ecard.entity.Card;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

public class CialloCardManager {
    private final static String CARD_DATA_PATH = "card_data.dat";
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Map<String, Card> cardMap = new HashMap<>();
    private final Logger l = Logger.getLogger("CialloCardManager");

    public CialloCardManager() {
        loadCardData();
    }

    public synchronized void loadCardData() {
        lock.writeLock().lock();
        FileInputStream fis = null;
        File file = new File(CARD_DATA_PATH);
        if (!file.exists()) {
            l.warning("Card data file not found, no data loaded");
            lock.writeLock().unlock();
            return;
        }
        // Load the card data from the file
        try {
            fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            cardMap.clear();
            cardMap.putAll((Map<String, Card>) ois.readObject());
            l.info("Card data loaded");
        } catch (Exception e) {
            l.severe("Failed to load card data: " + e.getMessage());
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception ignored) {
            }
            lock.writeLock().unlock();
        }
    }

    public synchronized void saveCardData() {
        // Commit the changes to the card data
        try {
            lock.writeLock().lock();
            File file = new File(CARD_DATA_PATH);
            if (!file.exists()) {
                boolean ignored = file.createNewFile();
            }
            // Save the card data to the file
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(cardMap);
                l.info("Card data saved");
            } catch (IOException e) {
                l.severe("Failed to save card data: " + e.getMessage());
            }
        } catch (IOException e) {
            l.severe("Failed to create card data file: " + e.getMessage());
        } finally {
            lock.writeLock().unlock();
        }
    }

    public synchronized void addCard(Card card) {
        lock.writeLock().lock();
        cardMap.put(card.getCardNumber(), card);
        lock.writeLock().unlock();
    }

    public Card getCard(String cardNumber) {
        lock.readLock().lock();
        Card card = cardMap.get(cardNumber);
        lock.readLock().unlock();
        return card;
    }

    public synchronized void updateCard(Card card) {
        lock.writeLock().lock();
        cardMap.put(card.getCardNumber(), card);
        lock.writeLock().unlock();
    }

    public synchronized void removeCard(String cardNumber) {
        lock.writeLock().lock();
        cardMap.remove(cardNumber);
        lock.writeLock().unlock();
    }

}