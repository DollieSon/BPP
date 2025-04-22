import Tokenizer.Tokenizer;
import Tokenizer.Token;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        File file = new File("test_files/input3.txt");
        try {
            Scanner input = new Scanner(file);
            Tokenizer tok = new Tokenizer();
            ArrayList<Token> res = tok.tokenize(input);
            Parser parser = new Parser(res);
            N_ProgramNode program = parser.parse();
            System.out.println(program);
            System.out.println("Done");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}