package frontend.parser.function.functype;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;

public class FuncTypeParser {
    private TokenListIterator iterator;
    /* FuncType Attributes */
    private FuncTypeEle funcTypeEle = null;

    public FuncTypeParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public FuncType parseFuncType() {
        Token token = this.iterator.readNextToken();
        if (token.getType().equals(TokenType.VOIDTK)) { // 'void'
            this.funcTypeEle = new FuncTypeVoid(token);
        } else if (token.getType().equals(TokenType.INTTK)) { // 'int'
            this.funcTypeEle = new FuncTypeInt(token);
        } else {
            System.out.println("EXPECT VOID OR INT HERE");
        }
        FuncType funcType = new FuncType(this.funcTypeEle);
        return funcType;
    }
}
