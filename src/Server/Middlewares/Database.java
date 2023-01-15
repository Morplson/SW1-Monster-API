package Server.Middlewares;

import Server.Models.Cards.Card;
import Server.Models.Cards.Element;
import Server.Models.Cards.Monster;
import Server.Models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class Database implements Middleware {
    private static final String URL = "jdbc:postgresql://localhost:5432/monster_trading_cards";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "admin";

    private final ReentrantLock lock = new ReentrantLock();

    Connection connection;

    public Database() {
        try {
            this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        };
    }



    public void open(){
        lock.lock();
    }

    public void close()  {
        lock.unlock();
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

        System.out.println(insertQuery.toString());

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
            ArrayList<Card> stack = getCardsByUsername(username, false);
            ArrayList<Card> deck = getCardsByUsername(username, true);
            user = new User(username, password, coins, name, bio, image, elo_value, stack, deck);
        }

        rs.close();
        stmt.close();
        return user;
    }

    public ArrayList<Card> getCardsByUsername(String username, boolean deck) throws SQLException {

        // ----- BUILDING QUERY ----- //
        String sql = "SELECT c.* FROM users_cards uc INNER JOIN cards c ON uc.card_id = c.id WHERE uc.user_id = ?";
        if (deck) {
            sql += " AND uc.deck = true";
        }

        // ----- SENDING QUERY ----- //
        PreparedStatement stmt = this.connection.prepareStatement(sql);
        stmt.setString(1, username);

        // ----- EXECUTE QUERY ----- //
        ResultSet rs = stmt.executeQuery();

        // ----- PROCESSING RESULT ----- //
        ArrayList<Card> cards = new ArrayList<Card>();
        while (rs.next()) {
            String id = rs.getString("id");
            String name = rs.getString("name");
            float damage = rs.getFloat("damage");
            float health = rs.getFloat("health");
            float critical_chance = rs.getFloat("critical_chance");
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

    //---------- Card -----------------------------------------------------------------

    public void insertCard(Card card) throws SQLException {

        // ----- BUILDING QUERY ----- //
        StringBuilder insertQuery = new StringBuilder("INSERT INTO cards (id, name, ");
        int counter = 2;
        if (card.getDamage() != null) {
            insertQuery.append("damage, ");
            counter++;
        }
        if (card.getHealth() != null) {
            insertQuery.append("health, ");
            counter++;
        }
        if (card.getCriticalChance() != null) {
            insertQuery.append("critical_chance, ");
            counter++;
        }
        if (card.getElement() != null) {
            insertQuery.append("element_type, ");
            counter++;
        }
        if (card.getMonster() != null) {
            insertQuery.append("monster_type, ");
            counter++;
        }
        insertQuery.deleteCharAt(insertQuery.length() - 2); // remove the last comma and space
        insertQuery.append(") VALUES (");
        for (int i = 0; i < counter; i++) {
            insertQuery.append("?, ");
        }
        insertQuery.deleteCharAt(insertQuery.length() - 2); // remove the last comma and space
        insertQuery.append(")");

        System.out.println(insertQuery.toString());

        // ----- SENDING QUERY -----
        PreparedStatement stmt = this.connection.prepareStatement(insertQuery.toString());
        int index = 1;
        stmt.setString(index++, card.getId());
        stmt.setString(index++, card.getName());
        if (card.getDamage() != null) {
            stmt.setFloat(index++, card.getDamage());
        }
        if (card.getHealth() != null) {
            stmt.setFloat(index++, card.getHealth());
        }
        if (card.getCriticalChance() != null) {
            stmt.setFloat(index++, card.getCriticalChance());
        }
        if (card.getElement() != null) {
            stmt.setString(index++, card.getElement().name());
        }
        if (card.getMonster() != null) {
            stmt.setString(index++, card.getMonster().name());
        }

        // ----- EXECUTE QUERY ----- //
        stmt.executeUpdate();
    }

    public int insertPack(String name, int price) throws SQLException {

        // ----- BUILDING QUERY ----- //
        String sql = "INSERT INTO packs (name, price) VALUES (?, ?)";

        // ----- SENDING QUERY ----- //
        PreparedStatement stmt = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, name);
        stmt.setInt(2, price);

        // ----- EXECUTE QUERY ----- //
        stmt.executeUpdate();

        // ----- PROCESSING RESULT ----- //
        ResultSet keys = stmt.getGeneratedKeys();
        System.out.println(keys.toString());
        keys.next();
        return keys.getInt("id");
    }

    public void addCardToPack(int packId, String cardId) throws SQLException {

        // ----- BUILDING QUERY ----- //
        String sql = "INSERT INTO packs_cards (pack_id, card_id) VALUES (?, ?)";

        // ----- SENDING QUERY ----- //
        PreparedStatement stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, packId);
        stmt.setString(2, cardId);

        // ----- EXECUTE QUERY ----- //
        stmt.executeUpdate();
    }

    public HashMap<String, Object> getRandomPack() throws SQLException{

        // ----- BUILDING QUERY ----- //
        String sql = "SELECT * FROM packs ORDER BY random() LIMIT 1";

        // ----- SENDING QUERY ----- //
        PreparedStatement stmt = this.connection.prepareStatement(sql);

        // ----- EXECUTE QUERY ----- //
        ResultSet keys = stmt.executeQuery();

        // ----- PROCESSING RESULT ----- //
        keys.next();

        HashMap<String, Object> pack = new HashMap<>();
        pack.put("id", keys.getInt("id"));
        pack.put("name", keys.getString("name"));
        pack.put("price", keys.getInt("price"));

        return pack;
    }

    public ArrayList<String> getCardIdsByPackIdFromPackCards(int packId) throws SQLException{

        // ----- BUILDING QUERY ----- //
        String sql = "SELECT card_id FROM packs_cards WHERE pack_id = ?";

        // ----- SENDING QUERY ----- //
        PreparedStatement stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, packId);

        // ----- EXECUTE QUERY ----- //
        ResultSet keys = stmt.executeQuery();

        // ----- PROCESSING RESULT ----- //

        ArrayList<String> cardIDs = new ArrayList<>();
        String cardID = "";
        while (keys.next()) {
            cardID = keys.getString("card_id");

            System.out.println(cardID);
            cardIDs.add(cardID);
        }

        return cardIDs;
    }

    public void removePackByPackIdCascade(int packId) throws SQLException{
        // ----- BUILDING QUERY ----- //
        String sql = "DELETE FROM packs WHERE id = ?";

        // ----- SENDING QUERY ----- //
        PreparedStatement stmt = this.connection.prepareStatement(sql);
        stmt.setInt(1, packId);

        // ----- EXECUTE QUERY ----- //
        stmt.executeUpdate();
    }

    public void addCardToUser(String userId, String cardId, boolean deck) throws SQLException {
        // ----- BUILDING QUERY ----- //
        String sql = "INSERT INTO users_cards (user_id, card_id, deck) VALUES (?,?,?)";

        // ----- SENDING QUERY ----- //
        PreparedStatement stmt = this.connection.prepareStatement(sql);
        stmt.setString(1, userId);
        stmt.setString(2, cardId);
        stmt.setBoolean(3, deck);

        // ----- EXECUTE QUERY ----- //
        stmt.executeUpdate();
    }

    /**
     * This method is soley used to "edit the deck"
     * @param userId
     * @param cardId
     * @param deck
     * @throws SQLException
     */
    public void updateCardInUser(String userId, String cardId, boolean deck) throws SQLException {
        // ----- BUILDING QUERY ----- //
        String sql = "UPDATE users_cards SET deck = ? WHERE user_id = ? AND card_id = ?";

        // ----- SENDING QUERY ----- //
        PreparedStatement stmt = this.connection.prepareStatement(sql);
        stmt.setBoolean(1, deck);
        stmt.setString(2, userId);
        stmt.setString(3, cardId);

        // ----- EXECUTE QUERY ----- //
        stmt.executeUpdate();
    }

    public void transferCard(String fromUserId, String toUserId, String cardId) throws SQLException {
        // First, check if the card exists for the user
        String sql = "SELECT * FROM users_cards WHERE user_id = ? AND card_id = ?";
        PreparedStatement stmt = this.connection.prepareStatement(sql);
        stmt.setString(1, fromUserId);
        stmt.setString(2, cardId);
        ResultSet rs = stmt.executeQuery();
        if(!rs.next()){
            throw new SQLException("This card doesn't belong to this user");
        }

        // Then delete the card from the first user
        sql = "DELETE FROM users_cards WHERE user_id = ? AND card_id = ?";
        stmt = this.connection.prepareStatement(sql);
        stmt.setString(1, fromUserId);
        stmt.setString(2, cardId);
        stmt.executeUpdate();

        // Finally insert the card to the second user
        sql = "INSERT INTO users_cards (user_id, card_id, deck) VALUES (?,?,?)";
        stmt = this.connection.prepareStatement(sql);
        stmt.setString(1, toUserId);
        stmt.setString(2, cardId);
        stmt.executeUpdate();
    }


}
