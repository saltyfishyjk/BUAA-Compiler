package frontend.parser.expression;

/**
 * UnaryOp枚举类
 */
public enum UnaryOp {
    ADD('+'),
    SUB('-'),
    NOT('!');

    private char symbol;

    UnaryOp(char symbol) {
        this.symbol = symbol;
    }

}
