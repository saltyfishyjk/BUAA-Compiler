package frontend.parser.expression;

import frontend.lexer.TokenListIterator;
import frontend.parser.expression.multiexp.AddExp;
import frontend.parser.expression.multiexp.AddExpParser;
import middle.symbol.SymbolTable;

public class ConstExpParser {
    private TokenListIterator iterator;
    /* ConstExp Attributes */
    private AddExp addExp = null;
    private SymbolTable curSymbolTable;

    public ConstExpParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public ConstExpParser(TokenListIterator iterator, SymbolTable curSymbolTable) {
        this.iterator = iterator;
        this.curSymbolTable = curSymbolTable;
    }

    public ConstExp parseConstExp() {
        // AddExpParser addExpParser = new AddExpParser(this.iterator);
        AddExpParser addExpParser = new AddExpParser(this.iterator, this.curSymbolTable);
        this.addExp = addExpParser.parseAddExp();
        ConstExp constExp = new ConstExp(this.addExp);
        return constExp;
    }
}
