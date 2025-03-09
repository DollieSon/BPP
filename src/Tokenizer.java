public class Tokenizer{
    public enum Token {
        PROGRAM_START, PROGRAM_END,
        VAR_DECLARE,

        //DATA TYPES
        INT_TYPE,CHAR_TYPE,BOOL_TYPE,FLOAT_TYPE,

        //Operators
        ADD_OPP,SUB_OPP,MUL_OPP,MOD_OPP,DIV_OPP,

        //Last Option
        VARIABLE_NAME
    }
    public static Token getToken(String str){
        //Check for single tokens
        //not sure if this is an optimized approach btw
        if(str.length() == 1){ //single token
            switch (str){
                case "+":
                    return Token.ADD_OPP;
                case "-":
                    return Token.SUB_OPP;
                case "*":
                    return Token.MUL_OPP;
                case "/":
                    return Token.DIV_OPP;
                case "%":
                    return Token.MOD_OPP;
            }
        }
        //Check for reserved words
        else{
            switch (str){
                case "SUGOD":
                    return Token.PROGRAM_START;
                case "KATAPUSAN":
                    return Token.PROGRAM_END;
                case "MUGNA":
                    return Token.VAR_DECLARE;
            }
        }
        //Assume that it's a variable name
        return Token.VARIABLE_NAME;
    }
}
