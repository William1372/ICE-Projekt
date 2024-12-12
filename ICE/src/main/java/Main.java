import java.sql.Connection;

public class Main {
    public static void main (String[] args){
        CPHFitness cphFitness = new CPHFitness();
        Connection conn = DatabaseHandler.connect();

        if (conn != null) {
            DatabaseHandler.createUserTable(conn);
            DatabaseHandler.createRunningLogTable(conn);
            cphFitness.startMenu();
        }
    }
}
