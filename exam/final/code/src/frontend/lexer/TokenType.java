package frontend.lexer;

import java.util.regex.Pattern;

/**
 * 词法分析枚举类
 * 每个枚举对象有两个属性：是否贪婪匹配(boolean) & 正则表达式规则(Pattern)
 * 具体而言，对于g
 */
public enum TokenType {
    /* ---------- specific elements begin ---------- */
    MAINTK(true, "main"),
    CONSTTK(true, "const"),
    INTTK(true, "int"),
    BREAKTK(true, "break"),
    CONTINUETK(true, "continue"),
    IFTK(true, "if"),
    ELSETK(true, "else"),
    WHILETK(true, "while"),
    GETINTTK(true, "getint"),
    PRINTFTK(true, "printf"),
    RETURNTK(true, "return"),
    VOIDTK(true, "void"),
    BITANDTK(true, "bitand"),
    /* ---------- specific elements end ---------- */
    /* ----------- begin ---------- */
    IDENFR(false, "[_A-Za-z][_A-Za-z0-9]*"),
    INTCON(false, "[0-9]+"),
    STRCON(false, "\\\"[^\\\"]*\\\""),
    /* ---------- end ---------- */
    /* ----------- comparison operation begin ---------- */
    LEQ(false, "<="),
    LSS(false, "<"),
    GEQ(false, ">="),
    GRE(false, ">"),
    EQL(false, "=="),
    NEQ(false, "!="),
    /* ---------- comparison operation end ---------- */
    /* ---------- arithmetic operation begin ---------- */
    PLUS(false, "\\+"),
    MINU(false, "-"),
    MULT(false, "\\*"),
    DIV(false, "/"),
    /* ---------- arithmetic operation end ---------- */
    /* ---------- logical operation begin ---------- */
    NOT(false, "!"),
    AND(false, "&&"),
    OR(false, "\\|\\|"),
    MOD(false, "%"),
    /* ---------- logical operation end ---------- */
    ASSIGN(false, "="),
    SEMICN(false, ";"),
    COMMA(false, ","),
    /* ---------- brackets begin ---------- */
    LPARENT(false, "\\("),
    RPARENT(false, "\\)"),
    LBRACK(false, "\\["),
    RBRACK(false, "]"),
    LBRACE(false, "\\{"),
    RBRACE(false, "}");
    /* ---------- brackets end ---------- */

    private boolean isGreed;
    private String patternString;
    private Pattern pattern;

    TokenType(boolean isGreed, String patternString) {
        this.isGreed = isGreed;
        this.patternString = patternString;
        /* 需要在每个正则表达式前添加^，以保证匹配的是紧接着的字符串 */
        if (this.isGreed) {
            this.pattern = Pattern.compile("^" + this.patternString + "(?![_A-Za-z0-9])");
        } else {
            this.pattern = Pattern.compile("^" + this.patternString);
        }
    }

    public Pattern getPattern() {
        return this.pattern;
    }

    public boolean getIsGreed() {
        return this.isGreed;
    }

    public String getPatternString() {
        return this.patternString;
    }

    @Override
    public String toString() {
        return this.name();
    }

}