package frontend.parser.declaration.constant.constinitval;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;

import java.util.ArrayList;

public class ConstInitValMultiParser {
    private TokenListIterator iterator;
    /* ConstInitMulti Attributes */
    private Token leftBrace = null; // '{'
    private ConstInitVal first; // MAY exist
    private ArrayList<Token> commas = new ArrayList<>(); // MAY exist
    private ArrayList<ConstInitVal> constInitVals = new ArrayList<>(); // MAY exist
    private Token rightBrace = null; // '}'

    public ConstInitValMultiParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public ConstInitValMulti parseConstInitValMulti() {
        this.commas = new ArrayList<>();
        this.constInitVals = new ArrayList<>();
        this.leftBrace = this.iterator.readNextToken();
        if (!this.leftBrace.getType().equals(TokenType.LBRACE)) {
            System.out.println("EXPECT LBRACE HERE");
        }
        Token token = this.iterator.readNextToken();
        if (!token.getType().equals(TokenType.RBRACE)) {
            this.iterator.unReadToken(1);
            ConstInitValParser constInitValParser = new ConstInitValParser(this.iterator);
            this.first = constInitValParser.parseConstInitVal();
            token = this.iterator.readNextToken();
            while (token.getType().equals(TokenType.COMMA)) { // ','
                this.commas.add(token);
                this.constInitVals.add(constInitValParser.parseConstInitVal());
                token = this.iterator.readNextToken();
            }
            this.iterator.unReadToken(1);
        } else {
            this.iterator.unReadToken(1);
        }
        this.rightBrace = this.iterator.readNextToken();
        ConstInitValMulti constInitValMulti = new ConstInitValMulti(this.leftBrace,
                this.first, this.commas, this.constInitVals, this.rightBrace);
        return constInitValMulti;
    }
}
