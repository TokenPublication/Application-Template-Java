package com.example.application_template_jmvvm.data.model.card;

/**
 * This interface to connect MSR and ICC cards onCardDataReceived method after reading Card
 * However this app was designed only ICC card, therefore to implement MSR read needs extra effort
 * Therefore it's TODO for now
 */
public interface ICard {
    String getCardNumber();
    String getOwnerName();
}
