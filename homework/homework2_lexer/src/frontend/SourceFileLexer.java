package frontend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 源文件词法分析器
 * 由于有注释、字符串等语义复杂的情况，我们将这部分功能解耦出去，
 * 不交给本源文件词法分析器处理。类似OS中的特权指令，
 * 我们为TokenLexer提供高级权限，允许其执行包括跳行等高级操作。
 * 在提供的词法分析方法中，需要牢记始终先判断是否endOfFile
 */
public class SourceFileLexer {
    private InputStream inputStream;
    private ArrayList<String> lines;
    private int lineNum;
    private int columnNum;

    public SourceFileLexer(InputStream inputStream) {
        /* init attributes */
        this.inputStream = inputStream;
        this.lines = new ArrayList<>();
        this.lineNum = 0; // line ptr ->
        this.columnNum = 0; // column ptr ->
        /* read lines from input stream */
        readLines();
    }

    /* read lines from specific input stream */
    private void readLines() {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.inputStream));
        try {
            String lineNow = null;
            while (true) {
                lineNow = bufferedReader.readLine();
                if (lineNow == null) {
                    break;
                }
                this.lines.add(lineNow);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // System.out.println("Hello"); // used for break point test
    }

    /* judge if reach end of line */
    public boolean endOfLine() {
        if (endOfFile()) {
            return true;
        } else if (lineNum >= this.lines.size()) {
            return true;
        }
        return this.columnNum >= this.lines.get(lineNum).length();
    }

    /* judge if reach end of file */
    public boolean endOfFile() {
        return this.lineNum >= this.lines.size();
    }

    /* get now line */
    public String peekLine() {
        if (endOfFile()) { // always judge endOfFile firstly
            return "";
        } else {
            return this.lines.get(lineNum);
        }
    }

    /* get now char */
    public char peekChar() {
        if (endOfLine()) { // always judge endOfFile firstly
            return '\n';
        } else if (endOfFile()) { // judge endOfLine
            return 0; // illegal num stands for end of file
        } else {
            return peekLine().charAt(columnNum);
        }
    }

    /* get next len sub str */
    public String peekSubStr(int len) {
        if (endOfFile()) {
            return "";
        } else if (this.columnNum + len >= peekLine().length()) {
            return peekLine().substring(this.columnNum);
        } else {
            return peekLine().substring(this.columnNum, this.columnNum + len);
        }
    }

    /* judge if char is white space */
    private boolean isWhiteSpace(char c) {
        return Character.isWhitespace(c);
    }

    /* skip white spaces */
    public void skipWhiteSpace() {
        while (!endOfFile() && isWhiteSpace(peekChar())) {
            moveForward(1);
        }
    }

    /* move ptr forward specific steps */
    public void moveForward(int steps) {
        int cnt = steps;
        while (!this.endOfFile() && cnt > 0) {
            int lineLen = peekLine().length();
            if (columnNum + cnt >= lineLen) {
                lineNum++;
                cnt -= (lineLen - columnNum + 1);
                columnNum = 0;
            } else {
                columnNum += cnt;
                cnt = 0;
            }
        }
    }

    /* jump to next line and make column ptr zero */
    public void nextLine() {
        if (!endOfFile()) {
            lineNum++;
            columnNum = 0;
        }
    }

    /* get left part of current line */
    public String getLeftLine() {
        if (endOfFile() || endOfLine()) {
            return "";
        } else {
            return peekLine().substring(columnNum);
        }
    }

    /* try to hit following sub str */
    public String hitSubStr(Pattern pattern) {
        String leftLine = getLeftLine();
        Matcher matcher = pattern.matcher(leftLine);
        if (matcher.find()) {
            return matcher.group(0);
        } else {
            return null; // not match
        }
    }

    /* get current line num START WITH 1 NOT 0 */
    public int getLineNum() {
        return lineNum + 1;
    }
}
