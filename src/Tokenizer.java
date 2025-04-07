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
        COMMA,COLON,PAREN_OPEN,PAREN_CLOSE,

        //Fixed Functions
        PRINT_FUNC,INPUT_FUNC,

        //conditionals
        IF_COND,ELSE_COND,ELIF_COND,CODE_BLOCK,
        FOR_LOOP
    }


    private class ExtendedPair{
        Token_Enum orig_enum;
        String possible_string;
        Token_Enum sec_enum;
        String third_string;
        Token_Enum third_enum;
        public ExtendedPair(String ch,Token_Enum orig,Token_Enum second){
            possible_string = ch;
            orig_enum = orig;
            sec_enum = second;
            third_string = new String();
        }
        public void Add_Third(String st, Token_Enum third){
            third_string = st;
            third_enum = third;
        }
        public Token_Enum getEnum(String str){
            if(str.equals(possible_string)){
                return sec_enum;
            }else if (!this.third_string.isEmpty() && third_string.equals(str)){
                return third_enum;
            }else{
                return Token_Enum.ERROR_TOKEN;
            }
        }
    }


    HashMap<String, Token_Enum> keyword_pairs;
    HashMap<String, Token_Enum> single_stoper;
    HashMap<Character,ExtendedPair> double_stopper;
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
        keyword_pairs.put("IPAKITA", Token_Enum.PRINT_FUNC);
        keyword_pairs.put("DAWAT", Token_Enum.INPUT_FUNC);

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

        //for double stoppers
        double_stopper =  new HashMap<>();
        double_stopper.put('=',new ExtendedPair("==",Token_Enum.EQUAL_ASSIGN,Token_Enum.EQUALTO_OPP));
        ExtendedPair lt = new ExtendedPair("<=",Token_Enum.LESS_THAN,Token_Enum.LT_EQUAL);
        lt.Add_Third("<>",Token_Enum.NOT_EQUAL);
        double_stopper.put('<',lt);
        double_stopper.put('>',new ExtendedPair(">=",Token_Enum.GREATER_THAN,Token_Enum.GT_EQUAL));
        double_stopper.put('-',new ExtendedPair("--",Token_Enum.SUB_OPP,Token_Enum.COMMENT_SIGN));

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
                        //True: add temp_str to tok as var
                        char last_ch = 'X'; // 'X' has no meaning
                        if (!temp_str.isEmpty())   {
                            last_ch = temp_str.charAt(temp_str.length()-1);
                        }
                        if(single_stoper.containsKey(String.valueOf(ch))){
                            if(!temp_str.isEmpty()){
                                Token tok = new Token(Token_Enum.VARIABLE_NAME, temp_str.toString(),line_len);
                                res.add(tok);
                            }
                            temp_str = new StringBuilder();
                            Token_Enum tok = this.single_stoper.get(String.valueOf(ch));
                            res.add(new Token(tok,String.valueOf(ch),line_len));
                        }
                        else if (double_stopper.containsKey(last_ch)){
                            StringBuilder sb = new StringBuilder();
                            sb.append(last_ch);
                            sb.append(ch);
                            ExtendedPair Ep = double_stopper.get(last_ch);
                            Token_Enum tok_enum = Ep.getEnum(sb.toString());
                            if(tok_enum == Token_Enum.COMMENT_SIGN){
                                is_comment = true;
                            }
                            if(tok_enum == Token_Enum.ERROR_TOKEN){
                                Token temp_tok = new Token(Token_Enum.VARIABLE_NAME,temp_str.substring(0,temp_str.length()-1),line_len);
                                res.add(temp_tok);
                                temp_str = new StringBuilder();
                                temp_str.append(ch);
                                Token tok = new Token(Ep.orig_enum,String.valueOf(last_ch),line_len);
                                res.add(tok);
                            }else{
//                                if (!sb.isEmpty()) {
//                                    String bufferedWord = sb.toString();
//                                    if (this.keyword_pairs.containsKey(bufferedWord)) {
//                                        res.add(new Token(keyword_pairs.get(bufferedWord), bufferedWord, line_len));
//                                    } else {
//                                        res.add(new Token(Token_Enum.VARIABLE_NAME, bufferedWord, line_len));
//                                    }
//                                    sb.setLength(0);
//                                }
                                String var_str = temp_str.substring(0,temp_str.length()-1);
                                if(!var_str.isEmpty()){
                                    res.add(new Token(Token_Enum.VARIABLE_NAME, var_str,line_len));
                                }
                                temp_str = new StringBuilder();
                                //adding the stoper
                                Token tok = new Token(tok_enum,sb.toString(),line_len);
                                res.add(tok);
                            }
                        }
                        //False: add ch to temp_str
                        else{
                            temp_str.append(ch);
                        }
                    }
                    if(!temp_str.isEmpty()){
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
