public class Token {
    Tokenizer.Token_Enum token;
    String keyword;
    //TBE
    int line;
    public Token(Tokenizer.Token_Enum tok,String str,int line){
        this.token = tok;
        this.keyword = str;
        this.line = line;
    }
}
