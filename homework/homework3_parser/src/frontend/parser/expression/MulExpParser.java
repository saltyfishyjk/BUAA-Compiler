package frontend.parser.expression;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;
import frontend.parser.expression.multiexp.MulExp;
import frontend.parser.expression.unaryexp.UnaryExp;
import frontend.parser.expression.unaryexp.UnaryExpParser;

import java.util.ArrayList;

/**
 * <MulExp> -> <UnaryExp> | { ('*' | '/' | '%') <UnaryExp> }
 */
public class MulExpParser {
    private TokenListIterator iterator;
    /* MulExp Attributes */
    private UnaryExp first = null;
    private ArrayList<Token> operators = new ArrayList<>();
    private ArrayList<UnaryExp> operands = new ArrayList<>();

    public MulExpParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public MulExp parseMulExp() {
        UnaryExpParser unaryExpParser = new UnaryExpParser(this.iterator);
        first = unaryExpParser.parseUnaryExp();
        Token token = this.iterator.readNextToken();
        while (token.getType().equals(TokenType.MULT) ||
                token.getType().equals(TokenType.DIV) ||
                token.getType().equals(TokenType.MOD)) {
            /* token -> * / % */
            this.operators.add(token);
            this.operands.add(unaryExpParser.parseUnaryExp());
            token = this.iterator.readNextToken();
        }
        this.iterator.unReadToken(1);
        MulExp mulExp = new MulExp(first, operators, operands);
        return mulExp;
    }
}