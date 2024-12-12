import java.util.Scanner;

public class TextUI {
    private Scanner scanner = new Scanner(System.in);


    public void displayMsg (String msg){
        System.out.println(msg);
    }

    public String promptText (String msg){
        System.out.println(msg);            //Asks the user a question
        String input = scanner.nextLine(); //Scans the users input
        return input;                      //and returns it as a String
    }


    public int promptNumeric (String msg){
        System.out.println(msg);  //asks the user a question
        String input = scanner.nextLine(); //Scans the users input
        int number;

        try {
            number = Integer.parseInt(input);
        }
        catch (NumberFormatException e){
            displayMsg("Please only enter numbers");
            number = promptNumeric (msg);  //call on the method again until they return a number
        }
        return number;

    }

}
