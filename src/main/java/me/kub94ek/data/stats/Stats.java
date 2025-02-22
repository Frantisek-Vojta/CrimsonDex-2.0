package me.kub94ek.data.stats;

public enum Stats {
    WINS("Wins", "win"),
    LOSES("Loses", "lose"),
    BATTLES_PLAYED("Battles Played", "battles"),
    CARDS_CAUGHT("Cards Caught", "c_catch"),
    CARDS_BOUGHT("Cards Bought", "c_buy"),
    CARDS_TRADED("Cards Traded", "c_trade"),
    KILLS("Cards Killed", "kill"),
    CARDS_LOST("Cards Lost", "death"),
    COINS("Coins Total", "coins");
    
    public final String name;
    public final String id;
    
    Stats(String name, String id) {
        this.name = name;
        this.id = id;
    }
}
