package me.kub94ek;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class Generator {
    private static final String HEX = "0123456789ABCDEF";
    
    
    /*public static String createUniqueBattleId() {
        String id = generateId();
        while (Main.battles.containsValue(id)) {
            id = generateId();
        }
        return id;
    }*/
    
    public static String createUniqueCardId() {
        String id = generateId();
        while (Main.getDatabase().cardExists(id)) {
            id = generateId();
        }
        return id;
    }
    
    private static String generateId() {
        int length = 8;
        StringBuilder id = new StringBuilder(length);
        Random random = new Random(System.nanoTime());
        
        List<String> charCategories = new ArrayList<>(4);
        charCategories.add(HEX);
        
        for (int i = 0; i < length; i++) {
            String charCategory = charCategories.get(random.nextInt(charCategories.size()));
            int position = random.nextInt(charCategory.length());
            id.append(charCategory.charAt(position));
        }
        return new String(id);
    }
    
}
