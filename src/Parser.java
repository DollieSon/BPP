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
        while (!isAtEnd() && !check(Tokenizer.Token_Enum.PROGRAM_END)) {
            System.out.println("CURRENT TOKEN: " + tokens.get(current).token + " " + tokens.get(current).keyword);
            // AHAHHAHAHA BRO MADE A MATCH STATEMENT ICANT
            if (match(Tokenizer.Token_Enum.VAR_DECLARE)) { //MUGNA
                System.out.println("1");
                parseVariableDeclaration();
            } else if (match(Tokenizer.Token_Enum.PRINT_FUNC)) {
                System.out.println("2");
                parsePrintStatement();
            } else if (check(Tokenizer.Token_Enum.VARIABLE_NAME)) {
                System.out.println("3");
                parseAssignment();
            } else {
                System.out.println("99");
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

    private void parsePrintStatement() {
        System.out.println("asfk;jasklhjf");
        // Step 1: Ensure the colon (`:`) is present after `IPAKITA`
//        if (!match(Tokenizer.Token_Enum.COLON)) {
//            error("Expected ':' after IPAKITA");
//        }

        // Step 2: Collect print expressions
        ArrayList<String> expressions = new ArrayList<>();
        do {
            if (match(Tokenizer.Token_Enum.VARIABLE_NAME) || match(Tokenizer.Token_Enum.PRINT_FUNC)) {
                expressions.add(previous().keyword);
            } else if (match(Tokenizer.Token_Enum.ERROR_TOKEN)) {
                error("Invalid print expression");
            } else {
                error("Expected a variable, string, or number after IPAKITA:");
            }
        } while (match(Tokenizer.Token_Enum.CONCAT_OPP)); // `&` is the concatenation operator

        // Step 3: Output parsed print statement
        System.out.println("Print Statement: " + String.join(" & ", expressions));
    }

    //BROKEN
    private void parseAssignment() {
        // Step 1: Start with a variable
        Token varToken = advance(); // consume variable
        if (!match(Tokenizer.Token_Enum.EQUAL_ASSIGN)) {
            error("Expected '=' after variable name for assignment");
            return;
        }

        // Step 2: Support chaining (e.g., x = y = 4)
        ArrayList<String> chain = new ArrayList<>();
        chain.add(varToken.keyword);

        // Keep processing if there's another VARIABLE and EQUAL_ASSIGN
        while (match(Tokenizer.Token_Enum.VARIABLE_NAME) && match(Tokenizer.Token_Enum.EQUAL_ASSIGN)) {
            chain.add(previous().keyword);
        }

        // Step 3: Expect a value at the end
        if (match(Tokenizer.Token_Enum.INT_TYPE) ||
                match(Tokenizer.Token_Enum.CHAR_TYPE) ||
                match(Tokenizer.Token_Enum.FLOAT_TYPE) ||
                match(Tokenizer.Token_Enum.BOOL_TYPE) ||
                match(Tokenizer.Token_Enum.VARIABLE_NAME)) {

            String finalValue = previous().keyword;
            System.out.println("Variable Assignment: " + String.join(" = ", chain) + " = " + finalValue);
        } else {
            error("Expected a literal or variable after '='");
        }
    }

    private void processStatement() {
//        System.out.println("Processing: " + tokens.get(current).keyword);
        current++;
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