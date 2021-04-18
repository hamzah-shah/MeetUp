package edu.brown.cs.student.stars;

 import org.json.JSONArray;

 import java.sql.*;
 import java.util.ArrayList;
 import java.util.List;

// NOTE: CHANGED TO SINGLETON CLASS
public final class Events {

    private static Events events = null;
    private Connection conn;

    Events() {
      conn = MyDatabase.conn;
    }


    public static Events getInstance() {
      if (events == null) {
        events = new Events();
      }
      return events;
    }

  //might convert location to longitude and latitude

  /**
   * Schema: (looker username - eventId matched to - response)
   * @param username
   * @param starter
   */
  public void addMatch(String username, String starter) {
    try {
      PreparedStatement prep;
      prep = conn.prepareStatement("CREATE TABLE IF NOT EXISTS RSVP("
              + "number INTEGER,"
              + "username TEXT," // CHANGED: added UNIQUE
              + "starter TEXT,"
              + "response TEXT,"
              + "FOREIGN KEY (username) REFERENCES lookers(username)"
              + "ON DELETE CASCADE ON UPDATE CASCADE,"
              + "FOREIGN KEY (starter) REFERENCES events(starter)"
              + "ON DELETE CASCADE ON UPDATE CASCADE,"
              + "PRIMARY KEY (number));");
      prep.executeUpdate();
      prep = conn.prepareStatement("INSERT INTO RSVP VALUES(NULL, ?, ?, ?);");
      prep.setString(1, username);
      prep.setString(2, starter);
      prep.setString(3, "No response");
      prep.executeUpdate();
    } catch(SQLException e) {
      System.out.println(e);
    }
  }
  public List<String> getMatches(String starter) {
    List<String> names= new ArrayList<>();
    try {
      PreparedStatement prep;
      prep = conn.prepareStatement("SELECT username from RSVP where starter = ?;");
      prep.setString(1, starter);
      ResultSet rs = prep.executeQuery();
      while(rs.next()) {
        names.add(rs.getString(1));
      }
    } catch(SQLException e) {
      System.out.println(e);
    }
    return names;
  }
  /**
   * Schema: (event type - activity type - startTime - endTime - location - username)
   */
  public void addLooker(String username, String eventType, String startTime,
                        String endTime, double latitude, double longitude) {

    try {
      PreparedStatement prep;
      prep = conn.prepareStatement("CREATE TABLE IF NOT EXISTS lookers("
              + "number INTEGER,"
              + "username TEXT UNIQUE,"
              + "eventT TEXT,"
              + "startT TEXT,"
              + "endT TEXT,"
              + "latitude DOUBLE,"
              + "longitude DOUBLE,"
              + "FOREIGN KEY (username) REFERENCES users(username)"
              + "ON DELETE CASCADE ON UPDATE CASCADE,"
              + "PRIMARY KEY (number));");
      prep.executeUpdate();
      prep = conn.prepareStatement("INSERT INTO lookers VALUES(NULL, ?, ?, ?, ?, ?, ?);");
      prep.setString(1, username);
      prep.setString(2, eventType);
      prep.setString(3, startTime);
      prep.setString(4, endTime);
      prep.setDouble(5, latitude);
      prep.setDouble(6, longitude);
      prep.executeUpdate();
    } catch(SQLException e) {
      System.out.println(e);
    }
  }
  public List<LookerNode> getAllLookers() {
    List<LookerNode> lookers = new ArrayList<>();
    try {
      PreparedStatement prep;
      prep = conn.prepareStatement("SELECT * from lookers");
      ResultSet rs = prep.executeQuery();
      while(rs.next()) {
        int id = rs.getInt(1);
        String username = rs.getString(2);
        String event = rs.getString(3);
        String startTime = rs.getString(4);
        String endTime = rs.getString(5);
        double latitude = rs.getDouble(6);
        double longitude = rs.getDouble(7);

        LookerNode looker = new LookerNode(id, username, event, startTime, endTime, latitude, longitude);
        lookers.add(looker);

      }
    } catch (SQLException e) {
      System.out.println(e);
    }
    return lookers;
  }

  /**
   * Schema: (event type - activity type - startTime - endTime - location - starter's username - MAXnumPeople)
   */
  public void createEvent(String username, String activity, String startTime, String endTime,
                          double latitude, double longitude, int capacity) {
    try {
      PreparedStatement prep;
      prep = conn.prepareStatement("CREATE TABLE IF NOT EXISTS events("
              + "number INTEGER,"
              + "username TEXT UNIQUE," // TODO: make starter username a foreign key (that's how it is in lookers table)
              + "activityType TEXT,"
              + "startT TEXT,"
              + "endT TEXT,"
              + "latitude DOUBLE,"
              + "longitude DOUBLE,"
              + "capacity INTEGER,"
              + "PRIMARY KEY (number));");
      prep.executeUpdate();
      prep = conn.prepareStatement("INSERT INTO events VALUES(NULL, ?, ?, ?, ?, ?, ?, ?);");
      prep.setString(1, username);
      prep.setString(2, activity);
      prep.setString(3, startTime);
      prep.setString(4, endTime);
      prep.setDouble(5, latitude);
      prep.setDouble(6, longitude);
      prep.setInt(7, capacity);
      prep.executeUpdate();
    } catch(SQLException e) {
      System.out.println(e);
  }
  }


  public List<StarterNode> getAllEvents() {
    List<StarterNode> Events = new ArrayList<>();
    try {
      PreparedStatement prep;
      prep = conn.prepareStatement("SELECT * from events");
      ResultSet rs = prep.executeQuery();
      while(rs.next()) {

        //TODO: add usernames after sessions are ready
        int id = rs.getInt(1);
        String username = rs.getString(2);
        String activity = rs.getString(3);
        String startTime = rs.getString(4);
        String endTime = rs.getString(5);
        double latitude = rs.getDouble(6);
        double longitude = rs.getDouble(7);
        int capacity = rs.getInt(8);

        StarterNode starter = new StarterNode(id, username, activity, startTime,
                endTime, longitude, latitude, capacity);
        Events.add(starter);
      }
    } catch (SQLException e) {
      System.out.println(e);
    }
    return Events;
  }

  // TODO: get rid of redundancies in genNumEvents and getNumLookers
  public int getNumEvents() {
    int numRows = 0;
    try {
      PreparedStatement prep;
      prep = conn.prepareStatement("SELECT COUNT(*) from events");
      ResultSet rs = prep.executeQuery();
      while(rs.next()) {
        numRows = rs.getInt(1);
      }
    } catch (SQLException e) {
      System.out.println(e);
    }
    return numRows;
  }

  public int getNumLookers() {
    int numRows = 0;
    try {
      PreparedStatement prep;
      prep = conn.prepareStatement("SELECT COUNT(*) from lookers");
      ResultSet rs = prep.executeQuery();
      while(rs.next()) {
        numRows = rs.getInt(1);
      }
    } catch (SQLException e) {
      System.out.println(e);
    }
    return numRows;
  }

  public void clearTables() {
    try {
      PreparedStatement prep = conn.prepareStatement("DROP TABLE events");
      prep.executeUpdate();
      PreparedStatement prep1 = conn.prepareStatement("DROP TABLE lookers");
      prep1.executeUpdate();
      PreparedStatement prep2 = conn.prepareStatement("DROP TABLE RSVP");
      prep2.executeUpdate();

    } catch (SQLException e) {
      System.out.println(e);
    }


  }
}
