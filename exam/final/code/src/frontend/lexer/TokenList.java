package frontend.lexer;

import java.util.ArrayList;

public class TokenList {
    private ArrayList<Token> tokens;

    public TokenList() {
        this.tokens = new ArrayList<>();
    }

    public void addToken(Token token) {
        this.tokens.add(token);
    }

    private void checkTokens() {
        boolean flag = true;
        while (flag) {
            int len = this.tokens.size();
            flag = false;
            ArrayList<Token> temp = new ArrayList<>();
            for (int i = len - 1; i >= 0; i--) {
                if (flag) {
                    break;
                }
                Token token = this.tokens.get(i);
                if (token.getType().equals(TokenType.GETINTTK)) {
                    // 说明找到一个getint，应当检查是否是新增文法
                    int j;
                    for (j = i - 1; j >= 0; j--) {
                        if (this.tokens.get(j).getType().equals(TokenType.SEMICN)) {
                            // 找到最近的一个分号
                            break;
                        }
                    }
                    for (int k = Math.max(j, 0); k < i; k++) {
                        if (this.tokens.get(k).getType().equals(TokenType.INTTK)) {
                            // 在这之间找到一个int，说明是新文法
                            for (int index = 0; index < i - 1; index++) {
                                temp.add(this.tokens.get(index));
                            }
                            int l = i + 1;
                            // 找到该vardef的分号
                            for (l = i + 1; l < len; l++) {
                                if (this.tokens.get(l).getType().equals(TokenType.SEMICN)) {
                                    break;
                                }
                            }
                            for (int index = i + 3; index <= l; index++) {
                                temp.add(this.tokens.get(index));
                            }
                            Token ident = this.tokens.get(i - 2);
                            Token copy = new Token(ident.getType(), ident.getLineNum(), ident.getContent());
                            temp.add(copy);
                            Token assign = new Token(TokenType.ASSIGN, ident.getLineNum(), "=");
                            temp.add(assign);
                            Token getint = new Token(TokenType.GETINTTK, ident.getLineNum(), "getint");
                            temp.add(getint);
                            Token leftBracket = new Token(TokenType.LPARENT, ident.getLineNum(), "(");
                            temp.add(leftBracket);
                            Token rightBracket = new Token(TokenType.RPARENT, ident.getLineNum(), ")");
                            temp.add(rightBracket);
                            Token semicn = new Token(TokenType.SEMICN, ident.getLineNum(), ";");
                            temp.add(semicn);
                            for (int index = l + 1; index < len; index++) {
                                temp.add(this.tokens.get(index));
                            }
                            flag = true;
                            break;
                        }
                    }
                }
            }
            if (flag) {
                this.tokens = temp;
            }
        }
    }
    
    public ArrayList<Token> getTokens() {
        checkTokens();
        return this.tokens;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Token token : tokens) {
            sb.append(token.syntaxOutput());
            //sb.append(token.getType() + " " + token.getContent() + "\n");
        }
        return sb.toString();
    }
}
