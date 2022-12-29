package frontend.parser.expression.multiexp;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;
import middle.symbol.SymbolTable;

import java.util.ArrayList;

/**
 * <LAndExp> -> <EqExp> { '&&' <EqExp> }
 */
public class LAndExpParser {
    private TokenListIterator iterator;
    /* LAndExp Attributes */
    private EqExp first = null;
    private ArrayList<Token> operators = new ArrayList<>();
    private ArrayList<EqExp> operands = new ArrayList<>();
    private SymbolTable curSymbolTable;

    public LAndExpParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public LAndExpParser(TokenListIterator iterator, SymbolTable curSymbolTable) {
        this.iterator = iterator;
        this.curSymbolTable = curSymbolTable;
    }

    public LAndExp parseLAndExp() {
        this.operands = new ArrayList<>();
        this.operators = new ArrayList<>();
        // EqExpParser eqExpParser = new EqExpParser(this.iterator);
        EqExpParser eqExpParser = new EqExpParser(this.iterator, this.curSymbolTable);
        this.first = eqExpParser.parseEqExp();
        Token token = this.iterator.readNextToken();
        while (token.getType().equals(TokenType.AND)) { // '&&'
            this.operators.add(token);
            this.operands.add(eqExpParser.parseEqExp());
            token = this.iterator.readNextToken();
        }
        this.iterator.unReadToken(1);
        LAndExp landExp = new LAndExp(this.first, this.operators, this.operands);
        return landExp;
    }
}
