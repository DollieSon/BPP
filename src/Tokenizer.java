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
        ERROR_TOKEN
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
    //This is just shit
    public Token getToken(String str){
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
    //This is unoptimized
    public ArrayList<Token> getTok(RandomAccessFile line){
        //Should be an array of an object that holds the string,token, and location
        ArrayList<Token> res = new ArrayList<>();
        byte[] ident = new byte[1];
        try {
            line.read(ident);
            String first_ch = new String(ident);
            //check if it is a single character token
            switch (first_ch){
                case "+":
                    res.add(Token.ADD_OPP);
                    break;
                case "-":
                    res.add(Token.SUB_OPP);
                    break;
                case "=":
                    long marker = line.getFilePointer();
                    byte[] nextchar = new byte[1];
                    line.read(nextchar);
                    if(nextchar.equals("=")){
                        res.add(Token.EQUALTO_OPP);
                    }else{
                        res.add(Token.EQUAL_ASSIGN);
                        line.seek(marker);
                    }
                    //lookahead
                    break;
            }
            //check if it is a reserved keyword
            //check if a valid variable name
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return res;
    }
    // 3rd and final and better version
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
