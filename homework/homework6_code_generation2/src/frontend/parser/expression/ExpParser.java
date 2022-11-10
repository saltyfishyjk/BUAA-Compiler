package frontend.parser.expression;

import frontend.lexer.TokenListIterator;
import frontend.parser.expression.multiexp.AddExp;
import frontend.parser.expression.multiexp.AddExpParser;
import middle.symbol.SymbolTable;

public class ExpParser {
    private TokenListIterator iterator;
    private SymbolTable curSymbolTabl;

    public ExpParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public ExpParser(TokenListIterator iterator, SymbolTable curSymbolTabl) {
        this.iterator = iterator;
        this.curSymbolTabl = curSymbolTabl;
    }

    public Exp parseExp() {
        // AddExpParser addExpParser = new AddExpParser(this.iterator);\
        AddExpParser addExpParser = new AddExpParser(this.iterator, this.curSymbolTabl);
        AddExp addExp = addExpParser.parseAddExp();
        Exp exp = new Exp(addExp);
        return exp;
    }
}
