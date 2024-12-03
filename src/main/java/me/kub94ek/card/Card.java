package me.kub94ek.card;

public final class Card {
    private final String id;
    private final String owner;
    private final CardType type;
    private final int atkBonus;
    private final int hpBonus;
    
    private int health;
    
    public Card(String id, String owner, CardType type, int atkBonus, int hpBonus) {
        this.id = id;
        this.owner = owner;
        this.type = type;
        this.atkBonus = atkBonus;
        this.hpBonus = hpBonus;
        health = -1;
    }
    public Card(Card card) {
        this.id = card.id;
        this.owner = card.owner;
        this.type = card.type;
        this.atkBonus = card.atkBonus;
        this.hpBonus = card.hpBonus;
        health = -1;
    }
    
    public Card withHealth(int health) {
        Card card = new Card(this);
        card.health = health;
        return card;
    }
    
    public int getAttack() {
        return type.atk + (int) (type.atk/100d * atkBonus);
    }
    
    public int getHealth() {
        if (health < 0) {
            return type.hp + (int) (type.hp/100d * hpBonus);
        }
        return health;
    }
    
    /***
     * @return string for use in string selections
     * @see net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu
     */
    public String getListString() {
        return type.name + " (#" + id + ", " + (atkBonus >= 0 ? "+" + atkBonus : atkBonus) + "%" +
                "/" + (hpBonus >= 0 ? "+" + hpBonus : hpBonus) + "%)";
    }
    
    
    @Override
    public String toString() {
        return "Card Type: " + type.name +
                " `(#" + id + ", " + (atkBonus >= 0 ? "+" + atkBonus : atkBonus) + "%" +
                "/" + (hpBonus >= 0 ? "+" + hpBonus : hpBonus) + "%)`" +
                '\n' +
                "ATK: " + getAttack() + ", HP: " + getHealth();
    }
    
    
}
