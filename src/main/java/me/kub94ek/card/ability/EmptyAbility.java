package me.kub94ek.card.ability;

public final class EmptyAbility implements Ability {
    @Override
    public AbilityType getType() {
        return AbilityType.EMPTY;
    }
    
    @Override
    public boolean canBeUsed() {
        return false;
    }
    
    @Override
    public int cost() {
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
