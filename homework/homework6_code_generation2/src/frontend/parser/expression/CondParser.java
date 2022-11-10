package frontend.parser.expression;

import frontend.lexer.TokenListIterator;
import frontend.parser.expression.Cond;
import frontend.parser.expression.multiexp.LOrExp;
import frontend.parser.expression.multiexp.LOrExpParser;
import middle.symbol.SymbolTable;

public class CondParser {
    private TokenListIterator iterator;
    private SymbolTable curSymbolTable;

    public CondParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public CondParser(TokenListIterator iterator, SymbolTable curSymbolTable) {
        this.iterator = iterator;
        this.curSymbolTable = curSymbolTable;
    }

    public Cond parseCond() {
        // LOrExpParser lorExpParser = new LOrExpParser(this.iterator);
        LOrExpParser lorExpParser = new LOrExpParser(this.iterator, this.curSymbolTable);
        LOrExp lorExp = lorExpParser.parseLOrExp();
        Cond cond = new Cond(lorExp);
        return cond;
    }
}
