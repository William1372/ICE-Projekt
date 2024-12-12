import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CPHFitness {
    private static Connection connection; // Databaseforbindelse
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
                "8) Exit program");

        switch (choice) {
            case 1:
                // Læg til løb
                int hours = ui.promptNumeric("Enter hours: ");
                int minutes = ui.promptNumeric("Enter minutes ");
                int seconds = ui.promptNumeric("Enter seconds: ");
                String date = ui.promptText("Enter the date of the run: dd/mm/yy: ");
                float distance = ui.promptNumeric("Enter the distance in meters:");

                Run run = new Run(hours, minutes, seconds, distance, date);
                DatabaseHandler.saveRun(currentUser, run);
                System.out.println("You just added the run " + run + " to your running log. Good work!");
                mainMenu();
                break;
            case 2:
                createGoal();
                break;
            case 3:
                System.out.println("List of previous runs: ");
                currentUser.viewRunningLog();  // Kun løb for den aktuelle bruger
                mainMenu();
                break;
            case 4:
                //currentUser.viewTrainingPlanList;   // Metode findes ikke endnu
                mainMenu();
            case 5:
                System.out.println("Current challenges: ");
                ChallengesList.viewAllChallenges();
                mainMenu();
            case 6:
                System.out.println("Current goals: ");
                currentUser.viewGoalLog();
                mainMenu();
            case 7:
                leaderboard.viewLeaderboard();
                mainMenu();
                break;
            case 8:
                System.out.println("Exiting the program. Goodbye!");
                exitProgram();
            default:
                System.out.println("Invalid choice. Please try again.");
                mainMenu();
        }
    }

    public void createGoal() {
        int choice = ui.promptNumeric("You have the following options: \n " +
                "1) Add untimed distance-based goal (e.g 50 km)\n " +
                "2) Add timed distance-based goal (e.g 10 km under 60 minutes) \n " +
                "3) Add timed non-distance based goal (e.g 30 minutes) \n " +
                "4) Exit program");

        switch(choice){
            case 1:
                float goal1 = ui.promptNumeric("Enter distance in kilometers:");
                currentUser.addGoal(new Goal(goal1, 0));
                System.out.println("You just added : " + goal1 + " km to your goals. Good luck!");
                mainMenu();
                break;
            case 2:
                float goal2Meters = ui.promptNumeric("Enter distance in meters:");
                float goal2Time = ui.promptNumeric("Enter time in minutes: ");
                currentUser.addGoal(new Goal(goal2Meters, goal2Time));
                System.out.println("You just added: " + goal2Meters + goal2Time + " to your goals. Good luck!");
                break;
            case 3:
                int goal3 = ui.promptNumeric("Enter time in minutes: ");
                currentUser.addGoal(new Goal(goal3, 0));
                System.out.println("You just added : " + goal3 + " to your goals. Good luck!");
                break;
            default:
                System.out.println("Invalid number. Please try again.");
                createGoal();
        }
    }

    public void userLogin() {
        String bold = "\u001B[1m";
        String inputUsername = ui.promptText(bold + "Enter your username: ");
        String inputPassword = ui.promptText(bold + "Enter your password: ");

        // SQL forespørgsel for at hente brugerdata
        String sql = "SELECT * FROM users WHERE username = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, inputUsername);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                if (inputPassword.equals(storedPassword)) {
                    ui.displayMsg(bold + "Login succeeded!");
                    ui.displayMsg(bold + "Welcome, " + inputUsername + "!");

                    // Opret en User-instans baseret på databasedata
                    String gender = rs.getString("gender");
                    int age = rs.getInt("age");
                    int height = rs.getInt("height");
                    double weight = rs.getDouble("weight");
                    int userId = rs.getInt("id");  // Hent ID'et fra databasen

                    // Sæt currentUser og id
                    currentUser = new User(inputUsername, storedPassword, age, gender, height, weight);
                    currentUser.setId(userId); // Sæt brugerens ID korrekt

                    return;
                } else {
                    ui.displayMsg(bold + "Incorrect password. Please try again.");
                }
            } else {
                ui.displayMsg(bold + "Username not found. Please try again or sign up!");
            }
            startMenu(); // Genkald metoden for at prøve igen
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

        // Opret en User-instans uden ID først
        User user = new User(username, password, age, gender, height, weight);

        // Gem brugeren i databasen
        DatabaseHandler.saveUser(user);

        // Hent det nyoprettede ID fra databasen og sæt det på User objektet
        String sql = "SELECT id FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("id");
                user.setId(userId); // Sæt ID på User objektet
            }
        } catch (SQLException e) {
            ui.displayMsg("Error retrieving user ID after registration: " + e.getMessage());
        }

        ui.displayMsg("User registered successfully!");
        return user;
    }

    //Shows the users current challenges
    public void viewCurrentChallenges() {

      //for(Challenge c : getChallengeslist){

        //}

    }


    public void exitProgram(){
        System.exit(0);
    }
}
