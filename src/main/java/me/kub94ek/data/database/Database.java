package me.kub94ek.data.database;

import java.sql.Connection;
import java.sql.DriverManager;
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
                    stats TEXT NOT NULL)
                    """);
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        
    }
    
}
