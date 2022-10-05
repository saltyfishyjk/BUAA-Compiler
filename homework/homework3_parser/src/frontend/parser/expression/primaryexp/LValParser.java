package frontend.parser.expression.primaryexp;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;
import frontend.parser.expression.Exp;
import frontend.parser.expression.ExpParser;
import frontend.parser.terminal.Ident;
import frontend.parser.terminal.IdentParser;

import java.util.ArrayList;

public class LValParser {
    private TokenListIterator iterator;
    /* LVal Attributes */
    private Ident ident = null;
    private ArrayList<Token> leftBrackets = new ArrayList<>(); // '['
    private ArrayList<Exp> exps = new ArrayList<>();
    private ArrayList<Token> rightBrackets = new ArrayList<>();

    public LValParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public LVal parseLVal() {
        this.leftBrackets = new ArrayList<>();
        this.exps = new ArrayList<>();
        this.rightBrackets = new ArrayList<>();
        IdentParser identParser = new IdentParser(this.iterator);
        this.ident = identParser.parseIdent();
        Token token = this.iterator.readNextToken();
        while (token.getType().equals(TokenType.LBRACK)) { // '['
            this.leftBrackets.add(token);
            ExpParser expParser = new ExpParser(this.iterator);
            this.exps.add(expParser.parseExp());
            token = this.iterator.readNextToken(); // ']'
            this.rightBrackets.add(token);
            token = this.iterator.readNextToken();
        }
        this.iterator.unReadToken(1);
        LVal lval = new LVal(this.ident, this.leftBrackets, this.exps, this.rightBrackets);
        return lval;
    }
}
