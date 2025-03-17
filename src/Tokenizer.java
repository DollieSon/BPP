import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Tokenizer{
    public enum Token {
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

        //Stoppers or Esc
        COMMA,
    }
    HashMap<String,Token> keyword_pairs;
    public Tokenizer(){
        keyword_pairs = new HashMap<>();
        keyword_pairs.put("SUGOD",Token.PROGRAM_START);
        keyword_pairs.put("KATAPUSAN",Token.PROGRAM_END);
        keyword_pairs.put("MUGNA",Token.VAR_DECLARE);
        keyword_pairs.put("NUMERO",Token.INT_TYPE);
        keyword_pairs.put("LETRA",Token.CHAR_TYPE);
        keyword_pairs.put("TINOUD",Token.BOOL_TYPE);
        keyword_pairs.put("TIPIK",Token.FLOAT_TYPE);
        keyword_pairs.put("UG",Token.AND_BOOL);
        keyword_pairs.put("O",Token.OR_BOOL);

    }
    public ArrayList<Token> tokenize(Scanner input){
        ArrayList<Token> res = new ArrayList<>();
        while(input.hasNextLine()){
            String line = input.nextLine();
            String[] keywords = line.split("\\s+");
            for (String word : keywords){
                System.out.println("Parsing : " + word);
                if (this.keyword_pairs.containsKey(word)){
                    res.add(this.keyword_pairs.get(word));
                }else{
                    //parse by else

                }
            }
        }
        return res;
    }
}
