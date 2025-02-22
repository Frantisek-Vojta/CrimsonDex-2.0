package me.kub94ek.card.ability;

import me.kub94ek.data.battle.StartedBattle;

public final class EmptyAbility implements Ability {
    @Override
    public AbilityType getType() {
        return AbilityType.EMPTY;
    }
    
    @Override
    public boolean canBeUsed(StartedBattle battle) {
        return false;
    }
    
    @Override
    public int cost(StartedBattle battle) {
        return 0;
    }
    
    @Override
    public boolean needsVictim() {
        return false;
    }
    
    @Override
    public boolean needsOpponentVictim() {
        return false;
    }
    
}
