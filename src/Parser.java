import java.util.ArrayList;
import java.util.List;

public class Parser {
    private ArrayList<Token> tokens;
    private int current = 0;

    public Parser(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }

    public N_ProgramNode parse() {
        N_ProgramNode program = new N_ProgramNode();
        if (!isAtStart(Tokenizer.Token_Enum.PROGRAM_START)) {
            //Maybe static var para ma change change ra ang word na sugod and katapusan and stuff
            error("Expected SUGOD at the beginning");
        }
        System.out.println("Program Start: SUGOD");
        processStatement();
        // Processing sa tunga tunga
        while (!isAtEnd() && !check(Tokenizer.Token_Enum.PROGRAM_END)) {
            // AHAHHAHAHA BRO MADE A MATCH STATEMENT ICANT
            if (match(Tokenizer.Token_Enum.VAR_DECLARE)) { //MUGNA
                System.out.println("1");
                program.statements.add(parseVariableDeclaration());
            } else if (match(Tokenizer.Token_Enum.PRINT_FUNC)) {
                System.out.println("2");
                program.statements.add(parsePrintStatement());
            } else if (check(Tokenizer.Token_Enum.VARIABLE_NAME)) {
                System.out.println("3");
                program.statements.add(parseAssignment());
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

        System.out.println("TREE: " + program);
        return program;
    }

    private N_VariableDeclarationNode parseVariableDeclaration() {
        N_VariableDeclarationNode declNode = new N_VariableDeclarationNode();
        declNode.type = advance().token; // NUMERO, LETRA, etc.

        do {
            // Use parameterized constructor
            N_VariableNode varNode = new N_VariableNode(advance().keyword);

            if (match(Tokenizer.Token_Enum.EQUAL_ASSIGN)) {
                varNode.value = parseExpression();
                System.out.println("Variable Declaration: [" + varNode.name + "] = " + varNode.value);
            } else {
                System.out.println("Variable Declaration: [" + varNode.name + "]");
            }
            declNode.variables.add(varNode);
        } while (match(Tokenizer.Token_Enum.COMMA));

        return declNode;
    }

    private N_AssignmentNode parseAssignment() {
        String varName = advance().keyword; // Get variable name
        match(Tokenizer.Token_Enum.EQUAL_ASSIGN); // Consume '='

        // Parse the full expression
        N_ASTNode expr = parseExpression();

        System.out.println("Assignment: " + varName + " = " + expr);
        return new N_AssignmentNode(varName, expr);
    }

    private N_PrintNode parsePrintStatement() {
        N_PrintNode printNode = new N_PrintNode();

        // Skip the colon
        if (!match(Tokenizer.Token_Enum.COLON)) {
            error("Expected a colon after IPAKITA");
        }

        // Parse expressions
        do {
            if (check(Tokenizer.Token_Enum.VARIABLE_NAME)) {
                Token varToken = advance();
                printNode.addExpression(new N_VariableNode(varToken.keyword));  // Wrap in VariableNode
            }
            else if (check(Tokenizer.Token_Enum.DOUBLE_QOUTE) ||
                    check(Tokenizer.Token_Enum.BACK_TICK) ||
                    check(Tokenizer.Token_Enum.BRACKET_OPEN)) {

                Token openingToken = advance();
                Token valueToken = advance();

                // Handle special symbols
                if (openingToken.token == Tokenizer.Token_Enum.BACK_TICK) {
                    if (valueToken.keyword.equals("$")) {
                        printNode.addExpression(new N_LiteralNode("\n"));  // Newline symbol
                    } else if (valueToken.keyword.equals("[#]")) {
                        printNode.addExpression(new N_LiteralNode("#"));  // Escape symbol
                    }
                }
                // Handle regular strings
                else {
                    printNode.addExpression(new N_LiteralNode(valueToken.keyword));
                }

                advance(); // Skip closing symbol
            }
        } while (match(Tokenizer.Token_Enum.CONCAT_OPP));

        return printNode;
    }

    private N_ASTNode parseExpression() {
        return parseLogicalOr(); // Start with lowest precedence
    }

    // Lowest precedence (O - OR)
    private N_ASTNode parseLogicalOr() {
        N_ASTNode node = parseLogicalAnd();
        while (match(Tokenizer.Token_Enum.OR_BOOL)) {
            Token op = previous();
            node = new N_BinaryOpNode(op, node, parseLogicalAnd());
        }
        return node;
    }

    // UG - AND
    private N_ASTNode parseLogicalAnd() {
        N_ASTNode node = parseEquality();
        while (match(Tokenizer.Token_Enum.AND_BOOL)) {
            Token op = previous();
            node = new N_BinaryOpNode(op, node, parseEquality());
        }
        return node;
    }

    // ==, <>
    private N_ASTNode parseEquality() {
        N_ASTNode node = parseComparison();
        while (true) {
            if (match(Tokenizer.Token_Enum.EQUALTO_OPP)) {
                Token op = previous();
                node = new N_BinaryOpNode(op, node, parseComparison());
            } else if (match(Tokenizer.Token_Enum.NOT_EQUAL)) {
                Token op = previous();
                node = new N_BinaryOpNode(op, node, parseComparison());
            } else {
                break;
            }
        }
        return node;
    }

    // >, <, >=, <=
    private N_ASTNode parseComparison() {
        N_ASTNode node = parseAddSub();
        while (true) {
            if (match(Tokenizer.Token_Enum.GREATER_THAN)) {
                Token op = previous();
                node = new N_BinaryOpNode(op, node, parseAddSub());
            } else if (match(Tokenizer.Token_Enum.LESS_THAN)) {
                Token op = previous();
                node = new N_BinaryOpNode(op, node, parseAddSub());
            } else if (match(Tokenizer.Token_Enum.GT_EQUAL)) {
                Token op = previous();
                node = new N_BinaryOpNode(op, node, parseAddSub());
            } else if (match(Tokenizer.Token_Enum.LT_EQUAL)) {
                Token op = previous();
                node = new N_BinaryOpNode(op, node, parseAddSub());
            } else {
                break;
            }
        }
        return node;
    }

    // +, -
    private N_ASTNode parseAddSub() {
        N_ASTNode node = parseMulDivMod();
        while (true) {
            if (match(Tokenizer.Token_Enum.ADD_OPP)) {
                Token op = previous();
                node = new N_BinaryOpNode(op, node, parseMulDivMod());
            } else if (match(Tokenizer.Token_Enum.SUB_OPP)) {
                Token op = previous();
                node = new N_BinaryOpNode(op, node, parseMulDivMod());
            } else {
                break;
            }
        }
        return node;
    }

    // *, /, %
    private N_ASTNode parseMulDivMod() {
        N_ASTNode node = parseUnary();
        while (true) {
            if (match(Tokenizer.Token_Enum.MUL_OPP)) {
                Token op = previous();
                node = new N_BinaryOpNode(op, node, parseUnary());
            } else if (match(Tokenizer.Token_Enum.DIV_OPP)) {
                Token op = previous();
                node = new N_BinaryOpNode(op, node, parseUnary());
            } else if (match(Tokenizer.Token_Enum.MOD_OPP)) {
                Token op = previous();
                node = new N_BinaryOpNode(op, node, parseUnary());
            } else {
                break;
            }
        }
        return node;
    }

    // +, -, DILI (unary)
    private N_ASTNode parseUnary() {
        if (match(Tokenizer.Token_Enum.SUB_OPP)) {
            Token op = previous();
            return new N_UnaryOpNode(op, parseUnary());
        } else if (match(Tokenizer.Token_Enum.ADD_OPP)) {
            Token op = previous();
            return new N_UnaryOpNode(op, parseUnary());
        }
        //WALA PAY DILI
//        else if (match(Tokenizer.Token_Enum.NOT_BOOL)) { // DILI
//            Token op = previous();
//            return new N_UnaryOpNode(op, parseUnary());
//        }
        return parsePrimary();
    }

    // Base case (variables, literals, parentheses)
    private N_ASTNode parsePrimary() {
        if (match(Tokenizer.Token_Enum.PAREN_OPEN)) {
            N_ASTNode expr = parseExpression();
            if (!match(Tokenizer.Token_Enum.PAREN_CLOSE)) {
                error("Missing closing parenthesis");
            }
            return expr;
        }

        Token token = advance();
        switch (token.token) {
            case VARIABLE_NAME:
                return new N_VariableNode(token.keyword);
            case INT_TYPE:  // Make sure your tokenizer emits these
                return new N_LiteralNode(Integer.parseInt(token.keyword));
            case FLOAT_TYPE:
                return new N_LiteralNode(Double.parseDouble(token.keyword));
            case BOOL_TYPE:
                return new N_LiteralNode(token.keyword.equals("OO"));
            default:
                error("Expected expression");
                return null;
        }
    }

    // Helper: Check if a string is numeric
    private boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  // Handles integers and decimals
    }

    private void processStatement() {
//        System.out.println("Processing: " + tokens.get(current).keyword);
//        System.out.println("CURRENT TOKEN: " + tokens.get(current).token + " " + tokens.get(current).keyword);
        current++;
    }

    private boolean match(Tokenizer.Token_Enum expected) {
//        System.out.println("EXPECTED: " + expected);
//        System.out.println("CURRENT: " + tokens.get(current).token);
        if (current < tokens.size() && tokens.get(current).token == expected) {
            processStatement();
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
        if (!isAtEnd()) processStatement();  // Move to the next token
        return previous();  // Return the token we just moved past
    }

    private Object parseValue() {
        Token token = advance();

        // Handle quoted values
        if (token.token == Tokenizer.Token_Enum.BACK_TICK ||
                token.token == Tokenizer.Token_Enum.DOUBLE_QOUTE) {

            Token valueToken = advance(); // Get actual value
            advance(); // Skip closing quote
            return valueToken.keyword;
        }

        // Handle plain values (numbers, variables)
        return token.keyword;
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