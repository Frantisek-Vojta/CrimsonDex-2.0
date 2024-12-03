package me.kub94ek.card.ability;

public interface Ability {
    AbilityType getType();
    boolean canBeUsed();
    int cost();
    boolean needsVictim();
    boolean needsOpponentVictim();
    
}
