package me.kub94ek.data.database;

import me.kub94ek.card.Card;
import me.kub94ek.card.CardType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public final class Database {
    public final Connection databaseConnection;
    
    /***
     * Connects to a SQLite database in the database.db file and creates all needed tables if they don't exist yet.
     */
    public Database() {
        try {
            // Just use a simple SQLite database
            databaseConnection = DriverManager.getConnection("jdbc:sqlite:database.db");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        
        // Create all tables
        try (Statement statement = databaseConnection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS cards (
                    id TEXT PRIMARY KEY,
                    owner TEXT NOT NULL,
                    card_type TEXT NOT NULL,
                    atk_bonus INT DEFAULT 0,
                    hp_bonus INT DEFAULT 0)
                    """);
            
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS coins (
                    user_id TEXT PRIMARY KEY,
                    coins INT DEFAULT 0)
                    """);
            
            // Store stats in JSON
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS stats (
                    user_id TEXT PRIMARY KEY,
                    stats TEXT DEFAULT '{}')
                    """);
            
            // Store completed and current achievements as Lists and progress in JSON
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS achievements (
                    user_id TEXT PRIMARY KEY,
                    completed_achievements TEXT DEFAULT '[]',
                    current_achievements TEXT DEFAULT '[]',
                    achievement_progress TEXT DEFAULT '{}')
                    """);
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        
    }
    
    public void addCard(Card card) throws SQLException {
        addCard(card.getId(), card.getOwner(), card.getType().name(), card.getAtkBonus(), card.getHpBonus());
    }
    public void addCard(String id, String owner, String type, int atkBonus, int hpBonus) throws SQLException {
        PreparedStatement statement = databaseConnection.prepareStatement(
                "INSERT INTO cards VALUES (?, ?, ?, ?, ?)"
        );
        
        statement.setString(1, id);
        statement.setString(2, owner);
        statement.setString(3, type);
        statement.setInt(4, atkBonus);
        statement.setInt(5, hpBonus);
        
        statement.execute();
        statement.close();
        
    }
    public void removeCard(String id) throws SQLException {
        PreparedStatement statement = databaseConnection.prepareStatement("DELETE FROM cards WHERE id='" + id + "'");
        statement.execute();
        statement.close();
    }
    public boolean cardExists(String id) {
        Statement statement;
        boolean exists;
        try {
            statement = databaseConnection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM cards WHERE id='" + id + "'");
            exists = resultSet.next();
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return exists;
    }
    public Card getCard(String id) {
        if (!cardExists(id)) {
            throw new IllegalArgumentException("This card doesn't exist");
        }
        
        Card card = null;
        Statement statement;
        try {
            statement = databaseConnection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM cards WHERE id='" + id + "'");
            while (resultSet.next()) {
                card = new Card(id, resultSet.getString("owner"),
                        CardType.valueOf(resultSet.getString("card_type")),
                        resultSet.getInt("atk_bonus"), resultSet.getInt("hp_bonus"));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return card;
    }
    public List<Card> getUserCards(String userId) {
        List<Card> cards = new ArrayList<>();
        Statement statement;
        try {
            statement = databaseConnection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM cards WHERE owner='" + userId + "'");
            while (resultSet.next()) {
                cards.add(new Card(resultSet.getString("id"), userId,
                        CardType.valueOf(resultSet.getString("card_type")),
                        resultSet.getInt("atk_bonus"), resultSet.getInt("hp_bonus")));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return cards;
    }
    public void moveCard(String id, String newOwner) throws SQLException {
        if (!cardExists(id)) return;
        
        Card card = getCard(id);
        removeCard(id);
        addCard(id, newOwner, card.getType().name(), card.getAtkBonus(), card.getHpBonus());
        
    }
    
    public boolean hasCoins(String userId) {
        Statement statement;
        boolean exists;
        try {
            statement = databaseConnection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM coins WHERE user_id='" + userId + "'");
            exists = resultSet.next();
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return exists;
    }
    public void setCoins(String userId, int coins) {
        if (!hasCoins(userId)) {
            try (Statement statement = databaseConnection.createStatement()) {
                statement.execute("INSERT INTO coins (user_id, coins) VALUES ('" + userId + "', " + coins + ")");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return;
        }
        
        try (Statement statement = databaseConnection.createStatement()) {
            statement.execute("UPDATE coins SET coins=" + coins + " WHERE user_id='" + userId + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int getCoins(String userId) {
        if (!hasCoins(userId)) {
            return 0;
        }
        
        int returnValue = 0;
        Statement statement;
        try {
            statement = databaseConnection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM coins WHERE user_id='" + userId + "'");
            if (resultSet.next()) {
                returnValue = resultSet.getInt("coins");
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return returnValue;
        }
        return returnValue;
    }
    public void addCoins(String userId, int coins) {
        setCoins(userId, getCoins(userId) + coins);
    }
    public void removeCoins(String userId, int coins) {
        setCoins(userId, getCoins(userId) - coins);
    }
    public void giveCoins(String fromUser, String toUser, int coins) {
        if (!hasCoins(toUser)) {
            try (Statement statement = databaseConnection.createStatement()) {
                statement.execute("INSERT INTO coins (user_id, coins) VALUES ('" + toUser + "', " + coins + ")");
                statement.execute("UPDATE coins SET coins=" + (getCoins(fromUser) - coins) + " WHERE user_id='" + fromUser + "'");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return;
        }
        
        try (Statement statement = databaseConnection.createStatement()) {
            statement.execute("UPDATE coins SET coins=" + (getCoins(fromUser) - coins) + " WHERE user_id='" + fromUser + "'");
            statement.execute("UPDATE coins SET coins=" + (getCoins(toUser) + coins) + " WHERE user_id='" + toUser + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}
