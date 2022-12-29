package frontend.parser.expression.unaryexp;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;
import frontend.parser.expression.primaryexp.PrimaryExpParser;
import middle.symbol.SymbolTable;

public class UnaryExpParser {
    private TokenListIterator iterator;
    /* UnaryExp Attributes */
    private UnaryExpEle unaryExpEle = null;
    private SymbolTable curSymbolTable;

    public UnaryExpParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public UnaryExpParser(TokenListIterator iterator, SymbolTable curSymbolTable) {
        this.iterator = iterator;
        this.curSymbolTable = curSymbolTable;
    }

    public UnaryExp parseUnaryExp() {
        Token first = this.iterator.readNextToken();
        Token second = this.iterator.readNextToken();
        if (isIdentFirst(first, second)) {
            this.iterator.unReadToken(2);
            // UnaryExpFuncParser unaryExpFuncParser = new UnaryExpFuncParser(this.iterator);
            UnaryExpFuncParser unaryExpFuncParser = new UnaryExpFuncParser(this.iterator,
                    this.curSymbolTable);
            this.unaryExpEle = unaryExpFuncParser.parseUnaryFuncExp();
        } else if (isPrimaryExpFirst(first)) {
            this.iterator.unReadToken(2);
            // PrimaryExpParser primaryExpParser = new PrimaryExpParser(this.iterator);
            PrimaryExpParser primaryExpParser = new PrimaryExpParser(this.iterator,
                    this.curSymbolTable);
            this.unaryExpEle = primaryExpParser.parsePrimaryExp();
        } else if (isUnaryFirst(first)) {
            this.iterator.unReadToken(2);
            // UnaryExpOpParser unaryExpOpParser = new UnaryExpOpParser(this.iterator);
            UnaryExpOpParser unaryExpOpParser = new UnaryExpOpParser(this.iterator,
                    this.curSymbolTable);
            this.unaryExpEle = unaryExpOpParser.parseUnaryExpOp();
        }
        UnaryExp unaryExp = new UnaryExp(this.unaryExpEle);
        return unaryExp;
    }

    private boolean isPrimaryExpFirst(Token first) {
        return first.getType().equals(TokenType.LPARENT) ||
                first.getType().equals(TokenType.IDENFR) ||
                first.getType().equals(TokenType.INTCON);
    }

    private boolean isIdentFirst(Token first, Token second) {
        return first.getType().equals(TokenType.IDENFR) &&
                second.getType().equals(TokenType.LPARENT);
    }

    private boolean isUnaryFirst(Token first) {
        return first.getType().equals(TokenType.PLUS) ||
                first.getType().equals(TokenType.MINU) ||
                first.getType().equals(TokenType.NOT);
    }
}
