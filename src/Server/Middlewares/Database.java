package Server.Middlewares;

import Server.Models.Card;
import Server.Models.Cards.Element;
import Server.Models.Cards.Monster;
import Server.Models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Database implements Middleware {
    private static final String URL = "jdbc:postgresql://localhost:5432/monster_trading_cards";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "admin";

    Connection connection;


    public void open() throws SQLException  {
        this.connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/monster_trading_cards", USERNAME, PASSWORD);
    }

    public void close() throws SQLException  {
        this.connection.close();
    }

    public HashMap<String, Integer> getScoreboard() throws SQLException {
        HashMap<String, Integer> results = new HashMap<String, Integer>();

        Statement stmt = this.connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT username, elo_value FROM users ORDER BY elo_value DESC");

        while (rs.next()) {
            String username = rs.getString("username");
            int eloValue = rs.getInt("elo_value");

            results.put(username, eloValue);
        }

        rs.close();
        stmt.close();

        return results;
    }

    //---------- User -----------------------------------------------------------------

    public int insertUser(User user, String token, String domain) throws SQLException {

        // ----- BUILDING QUERY -----
        StringBuilder insertQuery = new StringBuilder("INSERT INTO users (username, password, token, access_domain, ");
        int counter = 4;
        if (user.getCoins() != null) {
            insertQuery.append("coins, ");
            counter++;
        }
        if (user.getName() != null) {
            insertQuery.append("name, ");
            counter++;
        }
        if (user.getBio() != null) {
            insertQuery.append("bio, ");
            counter++;
        }
        if (user.getImage() != null) {
            insertQuery.append("image, ");
            counter++;
        }
        if (user.getEloValue() != null) {
            insertQuery.append("elo_value, ");
            counter++;
        }
        insertQuery.deleteCharAt(insertQuery.length() - 2); // remove the last comma and space
        insertQuery.append(") VALUES (");
        for (int i = 0; i < counter; i++) {
            insertQuery.append("?, ");
        }
        insertQuery.deleteCharAt(insertQuery.length() - 2); // remove the last comma and space
        insertQuery.append(")");


        // ----- SENDING QUERY -----
        PreparedStatement stmt = this.connection.prepareStatement(insertQuery.toString());
        int index = 1;
        stmt.setString(index++, user.getUsername());
        stmt.setString(index++, user.getPassword());
        stmt.setString(index++, token);
        stmt.setString(index++, domain);
        if (user.getCoins() != null) {
            stmt.setInt(index++, user.getCoins());
        }
        if (user.getName() != null) {
            stmt.setString(index++, user.getName());
        }
        if (user.getBio() != null) {
            stmt.setString(index++, user.getBio());
        }
        if (user.getImage() != null) {
            stmt.setString(index++, user.getImage());
        }
        if (user.getEloValue() != null) {
            stmt.setInt(index++, user.getEloValue());
        }

        return stmt.executeUpdate();
    }


    public String getUserToken(String username, String password) throws SQLException {
        String sql = "SELECT token FROM users WHERE username = ? AND password = ?";
        PreparedStatement stmt = this.connection.prepareStatement(sql);
        stmt.setString(1, username);
        stmt.setString(2, password);
        ResultSet rs = stmt.executeQuery();

        String token = null;
        if (rs.next()) {
            token = rs.getString("token");
        }

        rs.close();
        stmt.close();
        return token;
    }

    public String getUserAccessDomain(String username, String password) throws SQLException {
        String sql = "SELECT access_domain FROM users WHERE username = ? AND password = ?";
        PreparedStatement stmt = this.connection.prepareStatement(sql);
        stmt.setString(1, username);
        stmt.setString(2, password);
        ResultSet rs = stmt.executeQuery();

        String access_domain = username;
        if (rs.next()) {
            access_domain = rs.getString("access_domain");
        }

        rs.close();
        stmt.close();
        return access_domain;
    }

    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        PreparedStatement stmt = this.connection.prepareStatement(sql);
        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery();

        User user = null;
        if (rs.next()) {
            String password = rs.getString("password");
            int coins = rs.getInt("coins");
            String name = rs.getString("name");
            String bio = rs.getString("bio");
            String image = rs.getString("image");
            int elo_value = rs.getInt("elo_value");
            ArrayList<Card> stack = getCardsByUsername(username, "stacks");
            ArrayList<Card> deck = getCardsByUsername(username, "decks");
            user = new User(username, password, coins, name, bio, image, elo_value, stack, deck);
        }

        rs.close();
        stmt.close();
        return user;
    }

    public ArrayList<Card> getCardsByUsername(String username, String table) throws SQLException {
        String sql = "SELECT c.* FROM "+table+" s INNER JOIN cards c ON s.card_id = c.id WHERE s.user_id = ?";
        PreparedStatement stmt = this.connection.prepareStatement(sql);
        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery();

        ArrayList<Card> cards = new ArrayList<Card>();
        while (rs.next()) {
            String id = rs.getString("id");
            String name = rs.getString("name");
            float damage = rs.getFloat("damage");
            float health = rs.getFloat("health");
            double critical_chance = rs.getDouble("critical_chance");
            String element_type = rs.getString("element_type");
            String monster_type = rs.getString("monster_type");
            Element element = element_type != null ? Element.valueOf(element_type) : null;
            Monster monster = monster_type!= null? Monster.valueOf(monster_type): null;
            Card card = new Card(id, name, damage, health, element, monster, critical_chance);
            cards.add(card);
        }

        return cards;
    }

    public int updateUserByUsername(String username, User user) throws SQLException {

        // ----- BUILDING QUERY -----
        StringBuilder updateQuery = new StringBuilder("UPDATE users SET ");
        if (user.getUsername() != null) {
            updateQuery.append("username = ?, ");
        }
        if (user.getPassword() != null) {
            updateQuery.append("password = ?, ");
        }
        if (user.getCoins() != null) {
            updateQuery.append("coins = ?, ");
        }
        if (user.getName() != null) {
            updateQuery.append("name = ?, ");
        }
        if (user.getBio() != null) {
            updateQuery.append("bio = ?, ");
        }
        if (user.getImage() != null) {
            updateQuery.append("image = ?, ");
        }
        if (user.getEloValue() != null) {
            updateQuery.append("elo_value = ?, ");
        }
        updateQuery.deleteCharAt(updateQuery.length() - 2); // remove the last comma and space
        updateQuery.append(" WHERE username = ?");

        // ----- SENDING QUERY -----
        PreparedStatement stmt = this.connection.prepareStatement(updateQuery.toString());
        int index = 1;
        if (user.getUsername() != null) {
            stmt.setString(index++, user.getUsername());
        }
        if (user.getPassword() != null) {
            stmt.setString(index++, user.getPassword());
        }
        if (user.getCoins() != null) {
            stmt.setInt(index++, user.getCoins());
        }
        if (user.getName() != null) {
            stmt.setString(index++, user.getName());
        }
        if (user.getBio() != null) {
            stmt.setString(index++, user.getBio());
        }
        if (user.getImage() != null) {
            stmt.setString(index++, user.getImage());
        }
        if (user.getEloValue() != null) {
            stmt.setInt(index++, user.getEloValue());
        }
        stmt.setString(index, username);

        return stmt.executeUpdate();
    }

}
