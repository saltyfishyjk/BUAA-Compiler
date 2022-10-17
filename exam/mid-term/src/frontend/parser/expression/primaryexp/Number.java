package frontend.parser.expression.primaryexp;

import frontend.parser.terminal.IntConst;

/**
 * Number 非终结符
 * Number -> IntConst
 */
public class Number implements PrimaryExpEle {
    private final String name = "<Number>";
    //private IntConst intConst;
    private NumberEle numberEle;

    /*public Number(IntConst intConst) {
        this.intConst = intConst;
    }*/

    public Number(NumberEle numberEle) {
        this.numberEle = numberEle;
    }

    /*public IntConst getIntConst() {
        return intConst;
    }*/

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        // sb.append(intConst.syntaxOutput());
        sb.append(this.numberEle.syntaxOutput());
        sb.append(this.name + "\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        // return String.valueOf(this.intConst.getNum());
        return this.numberEle.getNum();
    }
}
