package me.kub94ek.card.ability;

import me.kub94ek.data.battle.StartedBattle;

public interface Ability {
    AbilityType getType();
    boolean canBeUsed(StartedBattle battle);
    int cost(StartedBattle battle);
    boolean needsVictim();
    boolean needsOpponentVictim();
    
}
