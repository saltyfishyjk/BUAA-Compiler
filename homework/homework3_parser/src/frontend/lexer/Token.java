package frontend.lexer;

import frontend.parser.SyntaxNode;

/**
 * 词语类，保存词语的相关信息
 */
public class Token implements SyntaxNode {
    private TokenType type; // this.TokenType
    private int lineNum; // this.lineNum
    private String content; // this.content

    public Token(TokenType type, int lineNum, String content) {
        this.type = type;
        this.lineNum = lineNum;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public int getLineNum() {
        return lineNum;
    }

    public TokenType getType() {
        return type;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getType() + " " + this.getContent() + "\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        return this.getContent();
    }
}
