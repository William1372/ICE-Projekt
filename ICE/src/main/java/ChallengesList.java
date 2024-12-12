import java.util.ArrayList;

public class ChallengesList {
    private static ArrayList<Challenge> challengesList;


    public ChallengesList() {
        this.challengesList = new ArrayList<>();
    }

    public static void viewAllChallenges() {
        for (Challenge c : challengesList) {
            System.out.println(c.toString());
        }
    }

    public  ArrayList<Challenge> getChallengesList(){
        return challengesList;
    }
}

