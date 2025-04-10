import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Tokenizer{
    public enum Token_Enum {
        PROGRAM_START, PROGRAM_END,
        VAR_DECLARE,EQUAL_ASSIGN,

        //comments
        COMMENT_SIGN,COMMENT_STRING,
        //DATA TYPES
        INT_TYPE,CHAR_TYPE,BOOL_TYPE,FLOAT_TYPE,

        //Operators
        ADD_OPP,SUB_OPP,MUL_OPP,MOD_OPP,DIV_OPP,CONCAT_OPP,

        //Boolean Operators
        EQUALTO_OPP,AND_BOOL,OR_BOOL,

        //Comparisson Opperators
        GREATER_THAN,LESS_THAN,GT_EQUAL,LT_EQUAL,NOT_EQUAL,

        //Last Option
        VARIABLE_NAME,
        //Error
        ERROR_TOKEN,

        //Stoppers or Separators
        COMMA,COLON,PAREN_OPEN,PAREN_CLOSE,SINGLE_QOUTE,DOUBLE_QOUTE,BRACKET_OPEN,BRACKET_CLOSE,BACK_TICK,

        //Fixed Functions
        PRINT_FUNC,INPUT_FUNC,

        //conditionals
        IF_COND,ELSE_COND,ELIF_COND,CODE_BLOCK,
        FOR_LOOP
    }

    private class ExtendedPair{
        Token orig_tok;
        HashMap<String,Token> pair;
        public ExtendedPair(Token orig){
            orig_tok = orig;
            pair = new HashMap<>();
        }
        // if for = then string should be
        public void add_pair(String sign,Token towken){
            pair.put(sign,towken);
        }
        public void getEnum(ArrayList<Token> res_list,char ch,StringBuilder sb){
            Token top = res_list.remove(res_list.size()-1);
            if(pair.containsKey(top.keyword)){

            }else{

            }
        }
    }

    HashMap<String, Token_Enum> keyword_pairs;
    HashMap<String, Token_Enum> single_stoper;
    HashMap<String, Token_Enum> functions;
    public Tokenizer(){
        keyword_pairs = new HashMap<>();
        keyword_pairs.put("SUGOD", Token_Enum.PROGRAM_START);
        keyword_pairs.put("KATAPUSAN", Token_Enum.PROGRAM_END);
        keyword_pairs.put("MUGNA", Token_Enum.VAR_DECLARE);
        keyword_pairs.put("NUMERO", Token_Enum.INT_TYPE);
        keyword_pairs.put("LETRA", Token_Enum.CHAR_TYPE);
        keyword_pairs.put("TINOUD", Token_Enum.BOOL_TYPE);
        keyword_pairs.put("TIPIK", Token_Enum.FLOAT_TYPE);
        keyword_pairs.put("UG", Token_Enum.AND_BOOL);
        keyword_pairs.put("O", Token_Enum.OR_BOOL);
        keyword_pairs.put("KUNG", Token_Enum.IF_COND);
        keyword_pairs.put("PUNDOK", Token_Enum.CODE_BLOCK);

        //for functions
        functions = new HashMap<>();
        functions.put("IPAKITA", Token_Enum.PRINT_FUNC);
        functions.put("DAWAT", Token_Enum.INPUT_FUNC);


        //for single stoppers
        single_stoper = new HashMap<>();
        single_stoper.put(",", Token_Enum.COMMA);
        single_stoper.put(":", Token_Enum.COLON);
        single_stoper.put("(", Token_Enum.PAREN_OPEN);
        single_stoper.put(")", Token_Enum.PAREN_CLOSE);
        single_stoper.put("/", Token_Enum.DIV_OPP);
        single_stoper.put("*", Token_Enum.MUL_OPP);
        single_stoper.put("%", Token_Enum.MOD_OPP);
        single_stoper.put("+", Token_Enum.ADD_OPP);
        single_stoper.put("&", Token_Enum.CONCAT_OPP);
        single_stoper.put("[", Token_Enum.BRACKET_OPEN);
        single_stoper.put("]", Token_Enum.BRACKET_CLOSE);
        single_stoper.put("'", Token_Enum.SINGLE_QOUTE);
        single_stoper.put("\"", Token_Enum.DOUBLE_QOUTE);
        single_stoper.put("`", Token_Enum.BACK_TICK);
    }

    /*Algorithm
        0.Seperate by white space
        1.Checks For Word-Keywords
        2.Make StringBuilder (assuming that it is a variable name)
        3.Checks For Single Characters Keywords (:, +, *, /)
        3.1Checks For Double Character Keywords  (==, <>, <=)
        3.2a if found dump StringBuilder and keyword
        3.2b else Put char on a stringBuilder and repeat step 3
    */
    public ArrayList<Token> tokenize(Scanner input){
        ArrayList<Token> res = new ArrayList<>();
        int line_len = 0;
        while(input.hasNextLine()){
            line_len+=1;
            boolean is_comment = false;
            String line = input.nextLine();
            String[] keywords = line.split("\\s+");
            for (String word : keywords){
//                System.out.println("Parsing : " + word);
                if (this.keyword_pairs.containsKey(word)){
                    Token_Enum tok = this.keyword_pairs.get(word);
                    Token token = new Token(tok,word,line_len);
                    res.add(token);
                } else if (is_comment) {
                    res.add(new Token(Token_Enum.COMMENT_STRING,word,line_len));
                } else{
                    //parse per character
                    StringBuilder temp_str = new StringBuilder();
                    for(char ch : word.toCharArray()){
                        //check if ch is a stoper
                        if(single_stoper.containsKey(String.valueOf(ch))){
                            //check if the previous string is a function
                            if(ch == ':' &&  this.functions.containsKey(temp_str.toString())){
                                Token tok = new Token(functions.get(temp_str.toString()),temp_str.toString(),line_len);
                                res.add(tok);
                            }
                            else if(!temp_str.isEmpty()){
                                Token tok = new Token(Token_Enum.VARIABLE_NAME, temp_str.toString(),line_len);
                                res.add(tok);
                            }
                            temp_str = new StringBuilder();
                            Token_Enum tok = this.single_stoper.get(String.valueOf(ch));
                            res.add(new Token(tok,String.valueOf(ch),line_len));
                            continue;
                        }
                        //bruteforce approach
                        if(ch == '=' && !res.isEmpty()){
                            Token top_tok = res.remove(res.size()-1); // topmost token
                            switch (top_tok.token){
                                case EQUAL_ASSIGN -> {
                                    Token tok = new Token(Token_Enum.EQUALTO_OPP,"==",line_len);
                                    res.add(tok);
                                    break;
                                }
                                case LESS_THAN -> {
                                    Token tok = new Token(Token_Enum.LT_EQUAL,"<=",line_len);
                                    res.add(tok);
                                    break;
                                }
                                case GREATER_THAN -> {
                                    Token tok = new Token(Token_Enum.GT_EQUAL,">=",line_len);
                                    res.add(tok);
                                    break;
                                }
                                default -> {
                                    res.add(top_tok);
                                    if(!temp_str.isEmpty()){
                                        res.add(new Token(Token_Enum.VARIABLE_NAME, temp_str.toString(), line_len));
                                        temp_str = new StringBuilder();
                                    }
                                    Token tok = new Token(Token_Enum.EQUAL_ASSIGN,"=",line_len);
                                    res.add(tok);
                                    break;
                                }
                            }
                            continue;
                        }
                        if(ch == '>'){
                            Token top_tok = res.remove(res.size()-1); // topmost token
                            switch (top_tok.token){
                                case LESS_THAN -> {
                                    Token tok = new Token(Token_Enum.NOT_EQUAL,"<>",line_len);
                                    res.add(tok);
                                }
                                default -> {
                                    res.add(top_tok);
                                    if(!temp_str.isEmpty()){
                                        res.add(new Token(Token_Enum.VARIABLE_NAME, temp_str.toString(), line_len));
                                        temp_str = new StringBuilder();
                                    }
                                    Token tok = new Token(Token_Enum.GREATER_THAN,">",line_len);
                                }
                            }
                        }
                        if(ch=='-'){
                            Token top_tok = res.remove(res.size()-1); // topmost token
                            switch (top_tok.token){
                                case SUB_OPP -> {
                                    Token tok = new Token(Token_Enum.COMMENT_SIGN,"--",line_len);
                                    is_comment = true;
                                    res.add(tok);
                                }
                                default -> {
                                    res.add(top_tok);
                                    if(!temp_str.isEmpty()){
                                        res.add(new Token(Token_Enum.VARIABLE_NAME, temp_str.toString(), line_len));
                                        temp_str = new StringBuilder();
                                    }
                                    Token tok = new Token(Token_Enum.SUB_OPP,"-",line_len);
                                    res.add(tok);
                                }
                            }
                        }
                        temp_str.append(ch);
                    }
                    if(!temp_str.isEmpty() && !is_comment){
                        res.add(new Token(Token_Enum.VARIABLE_NAME, temp_str.toString(),line_len));
                    }
                }
            }
        }
        for(Token token : res){
            System.out.println("Keyword: " + token.keyword + " / " + token.token);
        }
        return res;
    }
}
