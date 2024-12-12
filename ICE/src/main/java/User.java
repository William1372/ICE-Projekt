import java.sql.*;
import java.util.ArrayList;

public class User {
    private int id;
    private String username;
    private String password;
    private int age;
    private String gender;
    private int height;
    private double weight;
    private ArrayList<Run> runningLog;
    private ArrayList<Goal> goalLog;
    private TextUI ui = new TextUI();
    private ArrayList<Challenge> currentChallenges;

    public User(String username, String password, int age, String gender, int height, double weight) {
        this.username = username;
        this.password = password;
        this.age = age;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.runningLog = new ArrayList<>();
        this.goalLog = new ArrayList<>();
        this.currentChallenges = new ArrayList<>();
    }

    public void editGoal(Goal goal) {

    }

    public void updateWeight() {
        String bold = "\u001B[1m";
        try {
            double newWeight = ui.promptNumeric(bold + "Type your updated weight in kg's: ");
            if (newWeight > 0) {
                ui.displayMsg(bold + "Weight has been updated from: " + weight + "kg., to: " + newWeight + "kg.");
                this.weight = newWeight;
            } else {
                ui.displayMsg(bold + "Invalid weight. Please type a positive number!");
                updateWeight();
            }
        } catch (Exception e) {
            System.out.println(bold + e);
        }
    }

    public void updateHeight() {
        String bold = "\u001B[1m";
        try {
            int newHeight = ui.promptNumeric(bold + "Type your updated height in cm's: ");
            if (newHeight > 0) {
                ui.displayMsg(bold + "Height has been updated from: " + height + "cm., to: " + newHeight + "cm.");
                height = newHeight;
            }
            else {
                ui.displayMsg(bold + "Invalid height. Please type a positive number!");
                updateHeight();
            }
        } catch (Exception e) {
            System.out.println(bold + e);
        }
    }

    public void viewRunningLog() {
        if (this.id <= 0) {
            System.out.println("Error: User ID not set.");
            return;
        }

        String sql = "SELECT hours, minutes, seconds, distance, date FROM running_log WHERE user_id = ?";

        try (PreparedStatement stmt = DatabaseHandler.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, this.id);  // Brug brugerens ID for at hente løb kun for den bruger
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                System.out.println("No runs found for this user.");
                return;
            }

            do {
                int hours = rs.getInt("hours");
                int minutes = rs.getInt("minutes");
                int seconds = rs.getInt("seconds");
                double distance = rs.getDouble("distance");
                String date = rs.getString("date");

                System.out.println("Run: " + hours + "h " + minutes + "m " + seconds + "s, Distance: " + distance + " meters, Date: " + date);
            } while (rs.next());
        } catch (SQLException e) {
            System.out.println("Error retrieving running log: " + e.getMessage());
        }
    }


        public void viewGoalLog() {
        try{
            for(Goal goal : goalLog){
                System.out.println(goal);
            }
        } catch(Exception e){
            System.out.println(e);
        }
    }

    public void addGoal(Goal goal){
        try {
            goalLog.add(goal);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void removeGoal(Goal goal){
        this.goalLog.remove(goal);

    }

    //Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void save() {
         DatabaseHandler.saveUser(this);
    }

}
