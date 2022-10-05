package frontend.parser.expression.unaryexp;

import frontend.lexer.TokenListIterator;

public class UnaryExpOpParser {
    private TokenListIterator iterator;
    /* UnaryExpOp Attributes */
    private UnaryOp unaryOp;
    private UnaryExp unaryExp;

    public UnaryExpOpParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public UnaryExpOp parseUnaryExpOp() {
        UnaryOpParser unaryOpParser = new UnaryOpParser(this.iterator);
        this.unaryOp = unaryOpParser.parseUnaryOp();
        UnaryExpParser unaryExpParser = new UnaryExpParser(this.iterator);
        this.unaryExp = unaryExpParser.parseUnaryExp();
        UnaryExpOp unaryExpOp = new UnaryExpOp(this.unaryOp, this.unaryExp);
        return unaryExpOp;
    }
}
