import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        File file = new File("input.txt");
        try {
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()){
                String line = sc.nextLine();
                System.out.println(Tokenizer.getToken(line).name());
            }
        } catch (FileNotFoundException e) {
            System.out.println("Scanner Error on File");
            e.printStackTrace(); // not good daw
            throw new RuntimeException(e);
        }
    }
}