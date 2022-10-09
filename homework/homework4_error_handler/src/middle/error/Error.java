package middle.error;

/**
 * 错误类
 */
public class Error implements Comparable<Error> {
    private ErrorType type; // 所属错误类别
    private int lineNum; // 从1开始

    public Error(int lineNum, ErrorType type) {
        this.lineNum = lineNum;
        this.type = type;
    }

    public ErrorType getType() {
        return type;
    }

    public int getLineNum() {
        return lineNum;
    }

    public String getErrorTypeCode() {
        return this.type.getCode();
    }

    @Override
    public int compareTo(Error o) {
        return Integer.compare(this.lineNum, o.getLineNum());
    }

    public String output() {
        String s = new String(this.lineNum + " " + this.type.getCode() + "\n");
        return s;
    }
}
