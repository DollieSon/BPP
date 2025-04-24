import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Parser {
    private ArrayList<Token> tokens;
    private int current = 0;

    public Parser(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }

    public N_ProgramNode parse() {
        N_ProgramNode program = new N_ProgramNode();
        while (match(Tokenizer.Token_Enum.COMMENT_STRING) || match(Tokenizer.Token_Enum.COMMENT_SIGN) ){}

        if (!match(Tokenizer.Token_Enum.PROGRAM_START)) {
            error(String.format("[Line: %s] Expected SUGOD at the beginning", tokens.get(current).line));
        }
        System.out.println("Program Start: SUGOD");
        // Processing sa tunga tunga
        while (!isAtLast()) {
            // AHAHHAHAHA BRO MADE A MATCH STATEMENT ICANT
            if (match(Tokenizer.Token_Enum.VAR_DECLARE)) { //MUGNA
//                System.out.println("1");
                program.statements.add(parseVariableDeclaration());
            } else if (match(Tokenizer.Token_Enum.PRINT_FUNC)) {
//                System.out.println("2");
                program.statements.add(parsePrintStatement());
            } else if (check(Tokenizer.Token_Enum.VARIABLE_NAME)) {
//                System.out.println("3");
                //Reserved keywords checking
                if(Objects.equals(tokens.get(current).keyword, "IPAKITA")){
                    error(String.format("[Line: %s] Expected a colon after IPAKITA", tokens.get(current).line));
                }
                program.statements.add(parseAssignment());
            } else {
//                System.out.println("CURRENT TOKEN: " + tokens.get(current).token + " " + tokens.get(current).keyword);
                if (check(Tokenizer.Token_Enum.PROGRAM_END)){
                    processStatement();
                    break;
                }
                System.out.println("99");
                processStatement(); // Placeholder for future statements
            }
        }

        while (match(Tokenizer.Token_Enum.COMMENT_STRING) || match(Tokenizer.Token_Enum.COMMENT_SIGN) ){}
        if (!match(Tokenizer.Token_Enum.PROGRAM_END) && !isAtEnd()) {
            error(String.format("[Line: %s] Expected KATAPUSAN at the end", tokens.get(current).line));
        }

        System.out.println("Program End: KATAPUSAN");

        System.out.println("Parsing successful!");

        return program;
    }

    private N_VariableDeclarationNode parseVariableDeclaration() {
        N_VariableDeclarationNode declNode = new N_VariableDeclarationNode();
        declNode.type = advance().token; // NUMERO, LETRA, etc.

//        System.out.println("DELC: " + declNode.type);
        if(declNode.type != Tokenizer.Token_Enum.INT_TYPE &&
            declNode.type != Tokenizer.Token_Enum.BOOL_TYPE &&
            declNode.type != Tokenizer.Token_Enum.CHAR_TYPE &&
            declNode.type != Tokenizer.Token_Enum.FLOAT_TYPE ){
            error(String.format("[Line: %s] Expected type after MUGNA", tokens.get(current).line));
        }

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
        //TYPE VALIDATION
        //WALA PA PANG CHECK IF NANA BA NI NA VARIABLE AND IF GI ASSIGN NA BA
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
                error(String.format("[Line: %s] Missing Parenthesis", tokens.get(current).line));
            }
            return expr;
        }

        // Handle LETRA (e.g. 'a')
        if (match(Tokenizer.Token_Enum.SINGLE_QOUTE)) {
            Token charToken = advance(); // should be the character
            if (!match(Tokenizer.Token_Enum.SINGLE_QOUTE)) {
                error(String.format("[Line: %s] Missing closing single quote", tokens.get(current).line));
            }
            return new N_LiteralNode(charToken.keyword.charAt(0)); // convert to char
        }

        // Handle TINUOD (e.g. "OO")
        if (match(Tokenizer.Token_Enum.DOUBLE_QOUTE)) {
            Token boolToken = advance(); // should be OO or DILI
            if (!match(Tokenizer.Token_Enum.DOUBLE_QOUTE)) {
                error(String.format("[Line: %s] Missing closing double quote", tokens.get(current).line));
            }
            return new N_LiteralNode(boolToken.keyword); // keep as string, let interpreter handle true/false
        }

        Token token = advance();
        //USELESS KAY ALWAYS SIYA VARIABLE NODE
        switch (token.token) {
            case VARIABLE_NAME:
                return new N_VariableNode(token.keyword);
            case INT_TYPE:
                return new N_LiteralNode(Integer.parseInt(token.keyword));
            case FLOAT_TYPE:
                return new N_LiteralNode(Double.parseDouble(token.keyword));
            case BOOL_TYPE:
                return new N_LiteralNode(token.keyword.equals("OO"));
            default:
                error(String.format("[Line: %s] Expected expression", token.line));
                return null;
        }
    }


    private void processStatement() {
//        System.out.println("Processing: " + tokens.get(current).keyword);
//        System.out.println("CURRENT TOKEN: " + tokens.get(current).token + " " + tokens.get(current).keyword + " " + current);
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

    private void printCurrent(){
        System.out.println("CURRENT TOKEN: " + tokens.get(current).token + " " + tokens.get(current).keyword);
    }

    private boolean isAtStart(Tokenizer.Token_Enum expected) {
        return !tokens.isEmpty() && tokens.get(0).token == expected;
    }

    private boolean isAtEnd() {
        return current >= tokens.size();
    }

    private boolean isAtLast(){
        return current >= tokens.size()-1;
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

    public void setTokens(ArrayList<Token> newTokens) {
        this.tokens = newTokens;
        this.current = 0;
    }

    private void error(String message) {
        System.err.println("Parsing error: " + message);
        System.exit(1);
    }
}