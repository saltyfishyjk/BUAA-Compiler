package frontend.parser.expression.multiexp;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;
import middle.symbol.SymbolTable;

import java.util.ArrayList;

/**
 * <LOrExp> -> <LAndExp> { '||' <LAndExp> }
 */
public class LOrExpParser {
    private TokenListIterator iterator;
    /* LOrExp Attributes */
    private LAndExp first = null;
    private ArrayList<Token> operators = new ArrayList<>();
    private ArrayList<LAndExp> operands = new ArrayList<>();
    private SymbolTable curSymbolTable;

    public LOrExpParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public LOrExpParser(TokenListIterator iterator, SymbolTable curSymbolTable) {
        this.iterator = iterator;
        this.curSymbolTable = curSymbolTable;
    }

    public LOrExp parseLOrExp() {
        this.operands = new ArrayList<>();
        this.operators = new ArrayList<>();
        // LAndExpParser landExpParser = new LAndExpParser(this.iterator);
        LAndExpParser landExpParser = new LAndExpParser(this.iterator, this.curSymbolTable);
        this.first = landExpParser.parseLAndExp();
        Token token = this.iterator.readNextToken();
        while (token.getType().equals(TokenType.OR)) { // '||'
            this.operators.add(token);
            this.operands.add(landExpParser.parseLAndExp());
            token = this.iterator.readNextToken();
        }
        this.iterator.unReadToken(1);
        LOrExp lorExp = new LOrExp(this.first, this.operators, this.operands);
        return lorExp;
    }

}
