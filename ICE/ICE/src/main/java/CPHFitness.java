import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CPHFitness {
    private static Connection connection;
    private User currentUser;
    private Leaderboard leaderboard;
    private TextUI ui;
    private ChallengesList challengesList;

    public CPHFitness(){
        connection = DatabaseHandler.connect();
        this.leaderboard = new Leaderboard();
        this.ui = new TextUI();
        this.challengesList = new ChallengesList();
    }

    public void startMenu(){
        String bold = "\u001B[1m";
        int choice = ui.promptNumeric(bold+"Welcome to CPHFitness!\n"+"1) Log in\n"+"2) Sign up\n"+"3) Exit app");
        switch(choice){
            case 1:
                userLogin();
                mainMenu();
                break;
            case 2:
                currentUser = registerUser();
                mainMenu();
                break;
            case 3:
                exitProgram();
                break;
            default:
                startMenu();
        }
    }

    public void mainMenu() {
        int choice = ui.promptNumeric("You have the following options: \n " +
                "1) Add a run \n " +
                "2) Add a goal \n " +
                "3) View previous runs \n " +
                "4) View your current training plan or choose a new one \n " +
                "5) View your current challenge or take a new one \n " +
                "6) View your goals \n " +
                "7) View leaderboard \n " +
                "8) Edit profile \n " +
                "9) Exit program");

        switch (choice) {
            case 1:
                int hours = ui.promptNumeric("Enter hours: ");
                int minutes = ui.promptNumeric("Enter minutes ");
                int seconds = ui.promptNumeric("Enter seconds: ");
                String date = ui.promptText("Enter the date of the run: dd/mm/yy: ");
                if (date == null || date.isEmpty()) {  //Hvis man ikke indtaster en dato, så tages dagens dato automatisk
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
                    date = LocalDate.now().format(formatter);
                    ui.displayMsg(date);
                }
                float distance = ui.promptNumeric("Enter the distance in meters:");
                int totalMin = hours*60 + minutes;
                DatabaseHandler.updateDistanceGoals(currentUser,distance/1000);
                DatabaseHandler.updateTimeGoals(currentUser, totalMin);
                DatabaseHandler.updateDualGoals(currentUser, totalMin, distance);
                Run run = new Run(hours, minutes, seconds, distance, date);
                DatabaseHandler.saveRun(currentUser, run);
                ui.displayMsg("You just added the run " + run + " to your running log. Good work!");
                mainMenu();
                break;
            case 2:
                createGoal();
                mainMenu();
                break;
            case 3:
                ui.displayMsg("List of previous runs: ");
                currentUser.viewRunningLog();
                mainMenu();
                break;
            case 4:
                //currentUser.viewTrainingPlanList;   // Metode findes ikke endnu
                mainMenu();
            case 5:
                if(!currentUser.getCurrentChallenges().isEmpty()) {
                    ui.displayMsg("Current challenges: ");
                    ChallengesList.viewAllChallenges();
                    mainMenu();
                } else {
                    ui.displayMsg("You currently have no active challenges.");
                    mainMenu();
                    break;
                }
            case 6:
                if (currentUser.getGoalsFromDatabase().isEmpty()){
                    ui.displayMsg("You have no current goals");
                    mainMenu();
                } else {
                    ui.displayMsg("Current goals: ");
                    for (Goal goal : currentUser.getGoalsFromDatabase())
                        System.out.println(goal);
                }
                mainMenu();
            case 7:
                leaderboard.viewLeaderboard();
                mainMenu();
                break;
            case 8:
                currentUser.updateProfile();
                mainMenu();
                break;
            case 9:
                ui.displayMsg("Exiting the program. Goodbye!");
                exitProgram();
            default:
                ui.displayMsg("Invalid choice. Please try again.");
                mainMenu();
        }
    }

    public void createGoal() {
        int choice = ui.promptNumeric("You have the following options: \n " +
                "1) Add untimed distance-based goal (e.g 50 km)\n " +
                "2) Add timed distance-based goal (e.g 10 km under 60 minutes) \n " +
                "3) Add timed non-distance based goal (e.g 30 minutes) \n " +
                "4) Return to Main Menu");

        switch(choice){
            case 1:
                float goal1 = ui.promptNumeric("Enter distance in kilometers:");
                currentUser.addGoal(new Goal(goal1, 0));
                System.out.println("You just added : " + goal1 + " km to your goals. Good luck!");
                Goal goalObj1 = new Goal(goal1, 0);
                DatabaseHandler.saveGoal(currentUser, goalObj1);
                mainMenu();
                break;
            case 2:
                float goal2Meters = ui.promptNumeric("Enter distance in meters:");
                int goal2Time = ui.promptNumeric("Enter time in minutes: ");
                currentUser.addGoal(new Goal(goal2Meters, goal2Time, 0));
                System.out.println("You just added: " + goal2Meters + " meters in " + goal2Time + " minutes to your goals. Good luck!");
                Goal goalObj2 = new Goal(goal2Meters, goal2Time, 0);
                DatabaseHandler.saveGoal(currentUser, goalObj2);
                break;
            case 3:
                int goal3 = ui.promptNumeric("Enter time in minutes: ");
                currentUser.addGoal(new Goal(goal3, 0));
                System.out.println("You just added : " + goal3 + " min to your goals. Good luck!");
                Goal goalObj3 = new Goal(goal3, 0);
                DatabaseHandler.saveGoal(currentUser, goalObj3);
                break;
            case 4:
                mainMenu();
            default:
                ui.displayMsg("Invalid number. Please try again.");
                createGoal();
        }
    }

    public void userLogin() {
        String bold = "\u001B[1m";
        String inputUsername = ui.promptText(bold + "Enter your username: ");
        String inputPassword = ui.promptText(bold + "Enter your password: ");

        String sql = "SELECT * FROM users WHERE username = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, inputUsername);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                if (inputPassword.equals(storedPassword)) {
                    ui.displayMsg(bold + "Login succeeded!");
                    ui.displayMsg(bold + "Welcome, " + inputUsername + "!");

                    String gender = rs.getString("gender");
                    int age = rs.getInt("age");
                    int height = rs.getInt("height");
                    double weight = rs.getDouble("weight");
                    int userId = rs.getInt("id");

                    currentUser = new User(inputUsername, storedPassword, age, gender, height, weight);
                    currentUser.setId(userId);

                    return;
                } else {
                    ui.displayMsg(bold + "Incorrect password. Please try again.");
                }
            } else {
                ui.displayMsg(bold + "Username not found. Please try again or sign up!");
            }
            startMenu();
        } catch (SQLException e) {
            ui.displayMsg(bold + "An error occurred during login: " + e.getMessage());
        }
    }

    public User registerUser() {
        String username = ui.promptText("Enter your username:");
        String password = ui.promptText("Enter your password:");
        int age = ui.promptNumeric("Enter your age:");
        String gender = ui.promptText("Enter your gender (Male/Female): ");
        int height = ui.promptNumeric("Enter your height in cm:");
        double weight = ui.promptNumeric("Enter your weight in kg:");

        User user = new User(username, password, age, gender, height, weight);

        DatabaseHandler.saveUser(user);

        String sql = "SELECT id FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("id");
                user.setId(userId);
            }
        } catch (SQLException e) {
            ui.displayMsg("Error retrieving user ID after registration: " + e.getMessage());
        }

        ui.displayMsg("User registered successfully!");
        return user;
    }

    public void viewCurrentChallenges() {

        ArrayList<Challenge> challenges = currentUser.getCurrentChallenges();

        if (challenges.isEmpty()) {
            ui.displayMsg("You have no active challenges.");
        } else {
            ui.displayMsg("Your active challenges:");
            for (Challenge challenge : challenges) {
                ui.displayMsg(challenge.toString());
            }
        }
    }

    public void exitProgram(){
        System.exit(0);
    }
}