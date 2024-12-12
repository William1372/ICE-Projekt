import java.util.ArrayList;
import java.util.List;

public class ChallengesList {
    private static ArrayList<Challenge> AllChallengesList;


    public ChallengesList() {
        this.AllChallengesList = new ArrayList<>();
    }

    public static void viewAllChallenges() {
        for (Challenge c : AllChallengesList) {
            System.out.println(c.toString());
        }
    }

    public List<Challenge> getAllChallengesList(){
        return AllChallengesList;
    }
}

