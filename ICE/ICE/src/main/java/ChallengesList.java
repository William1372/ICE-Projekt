import java.util.ArrayList;
import java.util.List;

public class ChallengesList {
    private static ArrayList<Challenge> AllChallengesList;
    private TextUI ui;
    


    public ChallengesList() {
        this.AllChallengesList = new ArrayList<>();
    }

    // TODO UI skal ud af challenge classen - ind i CPHFitness klassen
    public static void viewAllChallenges() {
        for (Challenge c : AllChallengesList) {
            System.out.println(c.toString());
        }
    }

    public List<Challenge> getAllChallengesList(){
        return AllChallengesList;
    }
}

