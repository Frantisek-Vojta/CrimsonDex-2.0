package me.kub94ek.card;

import me.kub94ek.card.ability.Ability;
import me.kub94ek.card.ability.EmptyAbility;
import net.dv8tion.jda.internal.entities.emoji.CustomEmojiImpl;

import java.util.List;

import static me.kub94ek.card.CardTags.*;

public enum CardType {
    RAYAQA_DA_TURAT("Rayaqa da Turat", List.of("rayaqa da turat", "turat"),
            760, 3124,
            "Takora!", "Turat deals 2x damage to countries connected to the highway.",
            true, EmptyAbility.class, List.of(CONNECTED_TO_HIGHWAY, JUNIAN_UNION, HAS_SEA),
            "turat",
            "doge", "doge", "doge",
            new CustomEmojiImpl("turat", 1276106022724833380L, false),
            false, 0),
    
    LIBERLAND("Liberland", List.of("liberland"),
            1024, 2140,
            "Colonizer of the South", "Liberland deals +15% damage to the countries of the Southern Hemisphere.",
            true, EmptyAbility.class, List.of(),
            "liberland",
            "Sw1Pe_", "Sw1Pe_", "doge",
            new CustomEmojiImpl("liberland", 1276107969788117013L, false),
            false, 0),
    
    LAVION("Lavion", List.of("lavion"),
            100, 100,
            "Lavion", "Lavion",
            false, EmptyAbility.class, List.of(JUNIAN_UNION, CONNECTED_TO_HIGHWAY),
            "lavion",
            "", "memesssssicek", "doge",
            new CustomEmojiImpl("lavion", 1276105712224702527L, false),
            false, 0),
    
    PROVINCE_VALDOR("Province Valdor", List.of("province valdor", "valdor"),
            100, 100,
            "Chicken Empire", "When you attack Province Valdor, " +
            "there is a 28% chance it'll take 5% of your max hp.",
            true, EmptyAbility.class, List.of(JUNIAN_UNION, CONNECTED_TO_HIGHWAY),
            "valdor",
            "doge", "doge", "doge; Ability design: xCel, Kub94ek",
            new CustomEmojiImpl("valdor", 1276096255784910880L, false),
            false, 0),
    
    TAIKOK_DYNASTY("Taikok Dynasty", List.of("taikok dynasty", "taikok"),
            550, 1945,
            "Best Naval in the world", "Taikok Dynasty can deal 40% of country's hp if it is " +
            "connected to a sea. (Usable twice)",
            true, EmptyAbility.class, List.of(HAS_SEA),
            "taikok",
            "Kub94ek", "doge", "doge; Ability design: Jonaaak",
            new CustomEmojiImpl("taikok", 1280126988698517625L, false),
            false, 0),
    
    TRIBERG("Triberg", List.of("triberg"),
            100, 100,
            "Triberg Ability Name", "Triberg ability desc",
            true, EmptyAbility.class, List.of(),
            "triberg",
            "Kubisek", "doge, Kubisek", "doge",
            new CustomEmojiImpl("triberg", 1313214736569073784L, false),
            false, 0);
    
    public final String name;
    public final List<String> validAnswers;
    public final int atk;
    public final int hp;
    public final String abilityName;
    public final String abilityDescription;
    public final boolean obtainable;
    public final Class<? extends Ability> ability;
    public final List<String> tags;
    public final String imageName;
    public final String cardAuthor;
    public final String spawnAuthor;
    public final String statsAuthor;
    public final CustomEmojiImpl emoji;
    public final boolean buyable;
    public final int cost;
    
    CardType(String name, List<String> validAnswers, int atk, int hp,
             String abilityName, String abilityDescription,
             boolean obtainable,
             Class<? extends Ability> ability,
             List<String> tags,
             String imageName,
             String cardAuthor, String spawnAuthor, String statsAuthor,
             CustomEmojiImpl emoji,
             boolean buyable, int cost) {
        this.name = name;
        this.validAnswers = validAnswers;
        this.atk = atk;
        this.hp = hp;
        this.abilityName = abilityName;
        this.abilityDescription = abilityDescription;
        this.obtainable = obtainable;
        this.ability = ability;
        this.tags = tags;
        this.imageName = imageName;
        this.cardAuthor = cardAuthor;
        this.spawnAuthor = spawnAuthor;
        this.statsAuthor = statsAuthor;
        this.emoji = emoji;
        this.buyable = buyable;
        this.cost = cost;
    }
}
