package frontend.parser.expression;

import frontend.parser.expression.multiexp.LOrExp;

/**
 * 条件表达式
 */
public class Cond {
    private LOrExp lorExp;

    public Cond(LOrExp lorExp) {
        this.lorExp = lorExp;
    }
}
