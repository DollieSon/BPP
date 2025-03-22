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
        System.out.println("Program Start: SUGOD");
        current++;

        // Processing sa tunga tunga
        //Check sa tanan for now
        while (!isAtEnd() && !check(Tokenizer.Token_Enum.PROGRAM_END)) {
            if (match(Tokenizer.Token_Enum.VAR_DECLARE)) { // Found MUGNA
                parseVariableDeclaration();
            } else {
                processStatement(); // Placeholder for future statements
            }
        }

        if (!match(Tokenizer.Token_Enum.PROGRAM_END)) {
            error("Expected KATAPUSAN at the end");
        }
        System.out.println("Program End: KATAPUSAN");

        System.out.println("Parsing successful!");
    }

    private void parseVariableDeclaration() {
        // Step 1: Expect a valid data type
        Tokenizer.Token_Enum dataType;
        if (match(Tokenizer.Token_Enum.INT_TYPE)) {
            dataType = Tokenizer.Token_Enum.INT_TYPE;
        } else if (match(Tokenizer.Token_Enum.CHAR_TYPE)) {
            dataType = Tokenizer.Token_Enum.CHAR_TYPE;
        } else if (match(Tokenizer.Token_Enum.BOOL_TYPE)) {
            dataType = Tokenizer.Token_Enum.BOOL_TYPE;
        } else if (match(Tokenizer.Token_Enum.FLOAT_TYPE)) {
            dataType = Tokenizer.Token_Enum.FLOAT_TYPE;
        } else {
            error("Expected a data type (NUMERO, LETRA, TINUOD, TIPIK) after MUGNA");
            return;
        }

        // Step 2: Process each variable separately
        do {
            if (!match(Tokenizer.Token_Enum.VARIABLE_NAME)) {
                error("Expected a variable name after data type");
                return;
            }
            String varName = previous().keyword;

            // Check if this specific variable gets assigned
            if (match(Tokenizer.Token_Enum.EQUAL_ASSIGN)) {
                if (isAtEnd()) {
                    error("Expected a value after '='");
                    return;
                }
                Token valueToken = advance(); // Move to assigned value
                System.out.println("Variable Declaration: [" + varName + "] = " + valueToken.keyword);
                return; // Stop parsing after an assignment
            }

            // If no assignment, just declare the variable
            System.out.println("Variable Declaration: [" + varName + "]");
        } while (match(Tokenizer.Token_Enum.COMMA)); // Process multiple variables
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
//        System.out.println("Processing: " + tokens.get(current).keyword);
        current++;
    }

    private boolean check(Tokenizer.Token_Enum expected) {
        return !isAtEnd() && tokens.get(current).token == expected;
    }

    private Token previous() {
        return tokens.get(current - 1); // Return the most recently consumed token
    }

    private Token advance() {
        if (!isAtEnd()) current++;  // Move to the next token
        return previous();  // Return the token we just moved past
    }


    public void setTokens(ArrayList<Token> newTokens) {
        this.tokens = newTokens;
        this.current = 0;
    }

    private void error(String message) {
        System.err.println("Parsing error: " + message);
        System.exit(1);
    }
}