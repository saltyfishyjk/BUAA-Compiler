package frontend.lexer;

import frontend.SourceFileLexer;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * 词法成分分析器
 * 主要功能是构建包含源代码中所有有效Tokens的TokenList
 * 首先预处理掉所有注释内容，由于其对后续环节没有作用，因此直接舍弃
 * 接着通过SourceFileLexer提供的各种接口，实现对词法内容的有效分析，并加入TokenList
 */
public class TokenLexer {
    private SourceFileLexer sourceFileLexer;
    private TokenList tokenList;
    private final String lineComment = "//";
    private final String blockCommentSt = "/*";
    private final String blockCommentEd = "*/";

    public TokenLexer(SourceFileLexer sourceFileLexer) {
        this.sourceFileLexer = sourceFileLexer;
        this.tokenList = new TokenList();
        tokenize();
    }

    private void tokenize() {
        while (!this.sourceFileLexer.endOfFile()) {
            // skip white spaces first
            skipWhiteSpace();
            // judge comments second
            if (skipComment()) {
                continue;
            }
            // handle tokens
            addToken();
        }
    }

    private void skipWhiteSpace() {
        this.sourceFileLexer.skipWhiteSpace();
    }

    private boolean skipComment() {
        if (lineComment.equals(this.sourceFileLexer.peekSubStr(2))) {
            this.sourceFileLexer.nextLine();
            return true;
        } else if (blockCommentSt.equals(this.sourceFileLexer.peekSubStr(2))) {
            sourceFileLexer.moveForward(this.blockCommentSt.length());
            while (!this.sourceFileLexer.endOfFile() &&
                    !blockCommentEd.equals(this.sourceFileLexer.peekSubStr(2))) {
                this.sourceFileLexer.moveForward(1);
            }
            if (blockCommentEd.equals(this.sourceFileLexer.peekSubStr(2))) {
                this.sourceFileLexer.moveForward(2);
                return true;
            }
        }
        return false;
    }

    /**
     * 枚举匹配下一个token，需要注意前缀相同的情况
     * 对于==和=的情况，主要通过在TokenType中声明的先后顺序实现贪婪匹配
     * 对于int和inta的情况，主要通过为int等标识符设置后缀不能添加数字、字符和下划线来实现
     */
    private void addToken() {
        for (TokenType tokenType : TokenType.values()) {
            Pattern pattern = tokenType.getPattern();
            String tokenStr = this.sourceFileLexer.hitSubStr(pattern);
            if (tokenStr == null) {
                continue;
            } else {
                if (tokenType.equals(TokenType.PLUSPLUS)) {
                    // ++
                    ArrayList<Token> tokens = this.tokenList.getTokens();
                    int len = tokens.size();
                    Token token = tokens.get(len - 1);
                    int lineNum = sourceFileLexer.getLineNum();
                    // 添加=
                    Token assign = new Token(TokenType.ASSIGN, lineNum, "=");
                    this.tokenList.addToken(assign);
                    // 添加一个新的token
                    Token newToken = new Token(token.getType(), lineNum, token.getContent());
                    this.tokenList.addToken(newToken);
                    // 添加+
                    Token plus = new Token(TokenType.PLUS, lineNum, "+");
                    this.tokenList.addToken(plus);
                    // 添加1
                    Token one = new Token(TokenType.INTCON, lineNum, "1");
                    this.tokenList.addToken(one);
                } else if (tokenType.equals(TokenType.MINUMINU)) {
                    // --
                    ArrayList<Token> tokens = this.tokenList.getTokens();
                    int len = tokens.size();
                    Token token = tokens.get(len - 1);
                    int lineNum = sourceFileLexer.getLineNum();
                    // 添加=
                    Token assign = new Token(TokenType.ASSIGN, lineNum, "=");
                    this.tokenList.addToken(assign);
                    // 添加一个新的token
                    Token newToken = new Token(token.getType(), lineNum, token.getContent());
                    this.tokenList.addToken(newToken);
                    // 添加-
                    Token minus = new Token(TokenType.MINU, lineNum, "-");
                    this.tokenList.addToken(minus);
                    // 添加1
                    Token one = new Token(TokenType.INTCON, lineNum, "1");
                    this.tokenList.addToken(one);
                } else {
                    Token token = new Token(tokenType, sourceFileLexer.getLineNum(), tokenStr);
                    this.tokenList.addToken(token);
                }
                this.sourceFileLexer.moveForward(tokenStr.length());
                break;
            }
        }
    }
    
    public TokenList getTokenList() {
        return tokenList;
    }

}