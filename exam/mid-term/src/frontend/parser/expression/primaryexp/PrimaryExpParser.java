package frontend.parser.expression.primaryexp;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;

public class PrimaryExpParser {
    private TokenListIterator iterator;
    /* PrimaryExp Attributes */
    private PrimaryExpEle primaryExpEle;

    public PrimaryExpParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public PrimaryExp parsePrimaryExp() {
        Token token = this.iterator.readNextToken();
        if (token.getType().equals(TokenType.LPARENT)) { // '('
            this.iterator.unReadToken(1);
            PrimaryExpExpParser primaryExpExpParser = new PrimaryExpExpParser(this.iterator);
            this.primaryExpEle = primaryExpExpParser.parsePrimaryExpExp();
        } else if (token.getType().equals(TokenType.IDENFR)) { // IDENFR
            this.iterator.unReadToken(1);
            LValParser lvalParser = new LValParser(this.iterator);
            this.primaryExpEle = lvalParser.parseLVal();
        } else if (token.getType().equals(TokenType.INTCON) ||
                    token.getType().equals(TokenType.HEXCON)) { // INT
            this.iterator.unReadToken(1);
            NumberParser numberParser = new NumberParser(this.iterator);
            this.primaryExpEle = numberParser.parseNumber();
        }
        PrimaryExp primaryExp = new PrimaryExp(this.primaryExpEle);
        return primaryExp;
    }
}
