package frontend.parser.expression.multiexp;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;

import java.util.ArrayList;

public class EqExpParser {
    private TokenListIterator iterator;
    /* EqExp Attributes */
    private RelExp first = null;
    private ArrayList<Token> operators = new ArrayList<>();
    private ArrayList<RelExp> operands = new ArrayList<>();

    public EqExpParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public EqExp parseEqExp() {
        this.operands = new ArrayList<>();
        this.operators = new ArrayList<>();
        RelExpParser relExpParser = new RelExpParser(this.iterator);
        this.first = relExpParser.parseRelExp();
        Token token = this.iterator.readNextToken();
        while (token.getType().equals(TokenType.EQL) || // '=='
                token.getType().equals(TokenType.NEQ)) { // '!='
            this.operators.add(token);
            this.operands.add(relExpParser.parseRelExp());
            token = this.iterator.readNextToken();
        }
        this.iterator.unReadToken(1);
        EqExp eqExp = new EqExp(this.first, this.operators, this.operands);
        return eqExp;
    }
}
