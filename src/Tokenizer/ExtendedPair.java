package Tokenizer;

import java.util.ArrayList;
import java.util.HashMap;

public class ExtendedPair {

    Token orig_tok;
    HashMap<String, Token> pair;
    public ExtendedPair(Token orig){
        orig_tok = orig;
        pair = new HashMap<>();
    }
    // if for = then string should be < for <= or > for >=
    public void add_pair(String sign, Token towken){
        pair.put(sign,towken);
    }
    public void getEnum(ArrayList<Token> res_list, char ch, StringBuilder sb, int line_num){
        Token cloned_tok;
        try {
            cloned_tok =  (Token) orig_tok.clone();
            cloned_tok.setLine(line_num);
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        if(!sb.isEmpty()){
            res_list.add(new Token(Tokenizer.Token_Enum.VARIABLE_NAME, sb.toString(),line_num));
            res_list.add(cloned_tok);
            sb.replace(0,sb.length(),"");
            return;
        }
        if(res_list.isEmpty()){
            res_list.add(cloned_tok);
            sb.replace(0,sb.length(),"");
            return;
        }
        Token top = res_list.remove(res_list.size()-1);
        if(pair.containsKey(top.keyword)){
            try {
                Token temp_tok = (Token)pair.get(top.keyword).clone();
                temp_tok.setLine(line_num);
                res_list.add(temp_tok);
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }else{
            res_list.add(top);
            res_list.add(cloned_tok);
        }
    }
}
