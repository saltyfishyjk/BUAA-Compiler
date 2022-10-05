package frontend.parser.expression.unaryexp;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;

public class UnaryOpParser {
    private TokenListIterator iterator;

    public UnaryOpParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public UnaryOp parseUnaryOp() {
        Token token = this.iterator.readNextToken();
        if (!(token.getType().equals(TokenType.PLUS) ||
                token.getType().equals(TokenType.MINU) ||
                token.getType().equals(TokenType.NOT))) {
            System.out.println("EXPECT UNARYOP HERE");
        }
        UnaryOp unaryOp = new UnaryOp(token);
        return unaryOp;
    }
}
