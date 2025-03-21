import java.util.ArrayList;

public class Parser {
    private ArrayList<Token> tokens;
    private int current = 0;

    public Parser(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }

    public void parse() {
        if (!isAtStart(Tokenizer.Token_Enum.PROGRAM_START)) {
            //Maybe static var para ma change change ra ang word na sugod and katapusan and stuff
            error("Expected SUGOD at the beginning");
        }
        current++;

        // Processing sa tunga tunga
        //Check sa tanan for now
        while (!isAtEnd() && !check(Tokenizer.Token_Enum.PROGRAM_END)) {
            processStatement();
        }

        if (!match(Tokenizer.Token_Enum.PROGRAM_END)) {
            error("Expected KATAPUSAN at the end");
        }

        System.out.println("Parsing successful!");
    }

    private boolean match(Tokenizer.Token_Enum expected) {
        if (current < tokens.size() && tokens.get(current).token == expected) {
            current++;
            return true;
        }
        return false;
    }

    private boolean isAtStart(Tokenizer.Token_Enum expected) {
        return !tokens.isEmpty() && tokens.get(0).token == expected;
    }

    private boolean isAtEnd() {
        return current >= tokens.size();
    }

    private void processStatement() {
        System.out.println("Processing: " + tokens.get(current).keyword);
        current++;
    }

    private boolean check(Tokenizer.Token_Enum expected) {
        return !isAtEnd() && tokens.get(current).token == expected;
    }

    private void error(String message) {
        System.err.println("Parsing error: " + message);
        System.exit(1);
    }
}