import java.sql.Connection;
import java.time.LocalDate;

public class Main {
    public static void main (String[] args){
        CPHFitness cphFitness = new CPHFitness();
        Connection conn = DatabaseHandler.connect();

        if (conn != null) {
            DatabaseHandler.createUserTable(conn);
            DatabaseHandler.createRunningLogTable(conn);
            DatabaseHandler.createGoalTable(conn);
            DatabaseHandler.createChallengeTable(conn);
            cphFitness.startMenu();
        }
    }
}
