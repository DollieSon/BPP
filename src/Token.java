public class Token implements Cloneable {
    Tokenizer.Token_Enum token;
    String keyword;
    //TBE
    int line;
    public Token(Tokenizer.Token_Enum tok,String str,int line){
        this.token = tok;
        this.keyword = str;
        this.line = line;
    }

    public Token setLine(int line) {
        this.line = line;
        return this;
    }

    @Override
    public Object clone() throws CloneNotSupportedException{
        return super.clone();
    }
}
