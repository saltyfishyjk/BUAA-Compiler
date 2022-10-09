package frontend.parser.expression.primaryexp;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.parser.expression.Exp;
import frontend.parser.expression.ExpParser;
import middle.symbol.SymbolTable;

public class PrimaryExpExpParser {
    private TokenListIterator iterator;
    /* PrimaryExpExp Attribute */
    private Token leftParent = null; // '('
    private Exp exp = null;
    private Token rightParent = null; // ')'
    private SymbolTable curSymbolTable;

    public PrimaryExpExpParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public PrimaryExpExpParser(TokenListIterator iterator, SymbolTable curSymbolTable) {
        this.iterator = iterator;
        this.curSymbolTable = curSymbolTable;
    }

    public PrimaryExpExp parsePrimaryExpExp() {
        this.leftParent = this.iterator.readNextToken();
        // ExpParser expParser = new ExpParser(this.iterator);
        ExpParser expParser = new ExpParser(this.iterator, this.curSymbolTable);
        this.exp = expParser.parseExp();
        this.rightParent = this.iterator.readNextToken();
        PrimaryExpExp primaryExpExp = new
                PrimaryExpExp(this.leftParent, this.exp, this.rightParent);
        return primaryExpExp;
    }
}
