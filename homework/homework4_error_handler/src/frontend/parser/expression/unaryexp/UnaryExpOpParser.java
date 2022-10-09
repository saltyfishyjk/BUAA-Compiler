package frontend.parser.expression.unaryexp;

import frontend.lexer.TokenListIterator;
import middle.symbol.SymbolTable;

public class UnaryExpOpParser {
    private TokenListIterator iterator;
    /* UnaryExpOp Attributes */
    private UnaryOp unaryOp;
    private UnaryExp unaryExp;
    private SymbolTable curSymbolTable;

    public UnaryExpOpParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public UnaryExpOpParser(TokenListIterator iterator, SymbolTable curSymbolTable) {
        this.iterator = iterator;
        this.curSymbolTable = curSymbolTable;
    }

    public UnaryExpOp parseUnaryExpOp() {
        UnaryOpParser unaryOpParser = new UnaryOpParser(this.iterator);
        this.unaryOp = unaryOpParser.parseUnaryOp();
        // UnaryExpParser unaryExpParser = new UnaryExpParser(this.iterator);
        UnaryExpParser unaryExpParser = new UnaryExpParser(this.iterator, this.curSymbolTable);
        this.unaryExp = unaryExpParser.parseUnaryExp();
        UnaryExpOp unaryExpOp = new UnaryExpOp(this.unaryOp, this.unaryExp);
        return unaryExpOp;
    }
}
