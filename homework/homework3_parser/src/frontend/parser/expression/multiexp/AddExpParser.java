package frontend.parser.expression.multiexp;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;

import java.util.ArrayList;

/**
 * <AddExp> -> <MulExp> { ('+' | '-') <MulExp> }
 */
public class AddExpParser {
    private TokenListIterator iterator;
    /* AddExp Attributes */
    private MulExp first = null;
    private ArrayList<Token> operators = new ArrayList<>(); // '+' '-'
    private ArrayList<MulExp> operands = new ArrayList<>();

    public AddExpParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public AddExp parseAddExp() {
        this.operands = new ArrayList<>();
        this.operators = new ArrayList<>();
        MulExpParser mulExpParser = new MulExpParser(this.iterator);
        this.first = mulExpParser.parseMulExp();
        Token token = this.iterator.readNextToken();
        while (token.getType().equals(TokenType.PLUS) ||
                token.getType().equals(TokenType.MINU)) {
            this.operators.add(token);
            this.operands.add(mulExpParser.parseMulExp());
            token = this.iterator.readNextToken();
        }
        this.iterator.unReadToken(1);
        AddExp addExp = new AddExp(this.first, this.operators, this.operands);
        return addExp;
    }
}
