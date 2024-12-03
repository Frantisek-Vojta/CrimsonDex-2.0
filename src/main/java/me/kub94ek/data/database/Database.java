package me.kub94ek.data.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

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
    
}
