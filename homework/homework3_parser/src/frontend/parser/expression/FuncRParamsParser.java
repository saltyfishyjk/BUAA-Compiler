package frontend.parser.expression;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;

import java.util.ArrayList;

public class FuncRParamsParser {
    private TokenListIterator iterator;
    /* FuncRParams Attributes */
    private Exp first = null;
    private ArrayList<Token> commas = new ArrayList<>();
    private ArrayList<Exp> exps = new ArrayList<>();

    public FuncRParamsParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public FuncRParams parseFuncRParams() {
        this.commas = new ArrayList<>();
        this.exps = new ArrayList<>();
        ExpParser expParser = new ExpParser(this.iterator);
        first = expParser.parseExp();
        Token token = this.iterator.readNextToken();
        while (token.getType().equals(TokenType.COMMA)) { // ','
            this.commas.add(token);
            this.exps.add(expParser.parseExp());
            token = this.iterator.readNextToken();
        }
        this.iterator.unReadToken(1);
        FuncRParams funcRParams = new FuncRParams(this.first, this.commas, this.exps);
        return funcRParams;
    }
}
