import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Tokenizer{
    public enum Token_Enum {
        PROGRAM_START, PROGRAM_END,
        VAR_DECLARE,EQUAL_ASSIGN,

        //DATA TYPES
        INT_TYPE,CHAR_TYPE,BOOL_TYPE,FLOAT_TYPE,

        //Operators
        ADD_OPP,SUB_OPP,MUL_OPP,MOD_OPP,DIV_OPP,

        //Boolean Operators
        EQUALTO_OPP,AND_BOOL,OR_BOOL,

        //Last Option
        VARIABLE_NAME,
        //Error
        ERROR_TOKEN,

        //Stoppers or Separators
        COMMA,PAREN_OPEN,PAREN_CLOSE,
    }
    HashMap<String, Token_Enum> keyword_pairs;
    HashMap<String, Token_Enum> single_stoper;
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

        //for single stoppers
        single_stoper = new HashMap<>();
        single_stoper.put(",", Token_Enum.COMMA);
        single_stoper.put("(", Token_Enum.PAREN_OPEN);
        single_stoper.put(")", Token_Enum.PAREN_CLOSE);
        single_stoper.put("/", Token_Enum.DIV_OPP);
        single_stoper.put("*", Token_Enum.MUL_OPP);
        single_stoper.put("%", Token_Enum.MOD_OPP);
        single_stoper.put("+", Token_Enum.ADD_OPP);
        single_stoper.put("-", Token_Enum.SUB_OPP);

    }
    public ArrayList<Token> tokenize(Scanner input){
        ArrayList<Token> res = new ArrayList<>();
        while(input.hasNextLine()){
            String line = input.nextLine();
            String[] keywords = line.split("\\s+");
            for (String word : keywords){
                System.out.println("Parsing : " + word);
                if (this.keyword_pairs.containsKey(word)){
                    Token_Enum tok = this.keyword_pairs.get(word);
                    Token token = new Token(tok,word);
                    res.add(token);
                }else{
                    //parse per character
                    StringBuilder temp_str = new StringBuilder();
                    for(char ch : word.toCharArray()){
                        //check if ch is a stoper
                        //True: add temp_str to tok as var
                        if(single_stoper.containsKey(String.valueOf(ch))){
                            if(!temp_str.isEmpty()){
                                Token tok = new Token(Token_Enum.VARIABLE_NAME, temp_str.toString());
                                res.add(tok);
                            }
                            temp_str = new StringBuilder();
                            Token_Enum tok = this.single_stoper.get(String.valueOf(ch));
                            res.add(new Token(tok,String.valueOf(ch)));
                        }
                        //False: add ch to temp_str
                        else{
                            temp_str.append(ch);
                        }
                    }
                    res.add(new Token(Token_Enum.VARIABLE_NAME, temp_str.toString()));
                }
            }
        }
        return res;
    }
}
