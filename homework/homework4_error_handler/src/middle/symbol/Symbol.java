package middle.symbol;

/**
 * 符号
 */
public class Symbol {
    private int lineNum; // 从1开始
    private String name;

    public String getName() {
        return name;
    }

    public int getLineNum() {
        return lineNum;
    }

    public Symbol(int lineNum, String name) {
        this.lineNum = lineNum;
        this.name = name;
    }
}