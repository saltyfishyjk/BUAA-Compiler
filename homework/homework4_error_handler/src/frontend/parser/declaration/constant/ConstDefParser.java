package frontend.parser.declaration.constant;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;
import frontend.parser.terminal.Ident;
import frontend.parser.declaration.constant.constinitval.ConstInitVal;
import frontend.parser.declaration.constant.constinitval.ConstInitValParser;
import frontend.parser.expression.ConstExp;
import frontend.parser.expression.ConstExpParser;
import frontend.parser.terminal.IdentParser;

import java.util.ArrayList;

public class ConstDefParser {
    private TokenListIterator iterator;
    /* ConstDef Attributes */
    private Ident ident;
    private ArrayList<Token> leftBrackets = new ArrayList<>();
    private ArrayList<ConstExp> constExps = new ArrayList<>();
    private ArrayList<Token> rightBrackets = new ArrayList<>();
    private Token eq; // =
    private ConstInitVal constInitVal;

    public ConstDefParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public ConstDef parseConstDef() {
        this.leftBrackets = new ArrayList<>();
        this.constExps = new ArrayList<>();
        this.rightBrackets = new ArrayList<>();
        IdentParser identParser = new IdentParser(this.iterator);
        ident = identParser.parseIdent();
        Token token = iterator.readNextToken();
        while (token.getType().equals(TokenType.LBRACK)) {
            /* '[' */
            this.leftBrackets.add(token);
            /* ConstExp */
            ConstExpParser constExpParser = new ConstExpParser(this.iterator);
            ConstExp constExp = constExpParser.parseConstExp();
            this.constExps.add(constExp);
            token = this.iterator.readNextToken();
            /* ']' */
            if (!token.getType().equals(TokenType.RBRACK)) {
                System.out.println("EXPECT RBRACK HERE");
            }
            this.rightBrackets.add(token);
            token = this.iterator.readNextToken();
        }
        if (!token.getType().equals(TokenType.ASSIGN)) {
            System.out.println("EXPECT ASSIGN HERE");
        }
        this.eq = token;
        ConstInitValParser constInitValParser = new ConstInitValParser(this.iterator);
        this.constInitVal = constInitValParser.parseConstInitVal();
        ConstDef constDef = new ConstDef(this.ident, this.leftBrackets, this.constExps,
                this.rightBrackets, this.eq, this.constInitVal);
        return constDef;
    }
}
