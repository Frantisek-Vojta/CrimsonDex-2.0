package me.kub94ek.image;

import me.kub94ek.card.Card;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class CardCreator {
    
    private static InputStream getFileInputStream(String fileName) {
        ClassLoader classLoader = CardCreator.class.getClassLoader();

        return classLoader.getResourceAsStream(fileName);
    }
    
    public static void createCardImage(Card card) throws IOException, FontFormatException {
        BufferedImage bufferedImage = new BufferedImage(1428, 2000, BufferedImage.TYPE_INT_RGB);
        Graphics2D imageGraphics = bufferedImage.createGraphics();
        imageGraphics.setColor(new Color(0x2b52ff));
        imageGraphics.fillRect(0, 0, 1428, 2000);
        
        BufferedImage cardImage = ImageIO.read(getFileInputStream("image/card/" + card.getType().imageName + "-card.png"));
        imageGraphics.drawImage(cardImage, 34, 261, null);
        imageGraphics.setColor(Color.WHITE);
        Font nameFont = Font.createFont(
                Font.TRUETYPE_FONT,
                getFileInputStream("fonts/ArsenicaTrial-Extrabold.ttf")
        );
        nameFont = nameFont.deriveFont(120f);
        imageGraphics.setFont(nameFont);
        imageGraphics.drawString(card.getType().name, 60, 182);
        Font abilityFont = Font.createFont(
                Font.TRUETYPE_FONT,
                getFileInputStream("fonts/BobbyJonesSoft.otf")
        );
        abilityFont = abilityFont.deriveFont(82f);
        imageGraphics.setColor(Color.BLACK);
        imageGraphics.setFont(abilityFont);
        imageGraphics.drawString(card.getType().abilityName, 58, 1138);
        imageGraphics.setColor(Color.WHITE);
        imageGraphics.drawString(card.getType().abilityName, 60, 1140);
        
        Font openSans = Font.createFont(
                Font.TRUETYPE_FONT,
                getFileInputStream("fonts/OpenSans-Semibold.ttf")
        ).deriveFont(52f);
        
        imageGraphics.setFont(openSans);
        
        String ability = card.getType().abilityDescription;
        int idx = 0;
        
        while (getTextWidth(ability, imageGraphics) > 1250) {
            String[] words = ability.split(" ");
            StringBuilder stringBuilder;
            for (int i = words.length - 1; i > -1; i--) {
                stringBuilder = new StringBuilder();
                
                for (int j = 0; j <= i; j++) {
                    stringBuilder.append(words[j]);
                    stringBuilder.append(' ');
                }
                
                if (getTextWidth(stringBuilder.toString(), imageGraphics) <= 1250) {
                    imageGraphics.drawString(stringBuilder.toString(), 60, 1240 + idx*60);
                    ability = ability.substring(stringBuilder.length());
                    break;
                }
                
            }
            
            idx++;
            
        }
        imageGraphics.drawString(ability, 60, 1240 + idx*60);
        
        imageGraphics.setFont(openSans.deriveFont(22f));
        imageGraphics.drawString("Bot made by Kub94ek (kub94ek.1234)", 20, 1970);
        
        int x = 1400 - getTextWidth("Card artwork: " + card.getType().cardAuthor, imageGraphics);
        imageGraphics.drawString("Card artwork: " + card.getType().cardAuthor, x, 1970);
        x = 1400 - getTextWidth("Spawn artwork: " + card.getType().spawnAuthor, imageGraphics);
        imageGraphics.drawString("Spawn artwork: " + card.getType().spawnAuthor, x, 1940);
        imageGraphics.drawString("Card design: " + card.getType().statsAuthor, 20, 1940);
        
        imageGraphics.setFont(abilityFont.deriveFont(180f));
        
        imageGraphics.drawImage(ImageIO.read(
                getFileInputStream("image/heart.png")
        ), 160, 1685, null);
        imageGraphics.setColor(Color.RED);
        imageGraphics.drawString("" + card.getHealth(), 320, 1802);
        
        imageGraphics.drawImage(ImageIO.read(
                getFileInputStream("image/attack.png")
        ), 775, 1641, null);
        
        imageGraphics.setColor(new Color(255, 202, 0));
        imageGraphics.drawString("" + card.getAttack(), 975, 1802);
        
        imageGraphics.dispose();
 
        File file = new File("image.jpg");
        ImageIO.write(bufferedImage, "jpg", file);
    }
    
    private static int getTextWidth(String text, Graphics2D g2) {
        return  (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
    }
    
}
