package frontend.parser.declaration.constant.constinitval;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;
import frontend.parser.expression.ConstExpParser;

public class ConstInitValParser {
    private TokenListIterator iterator;
    /* ConstInitVal Attribute */
    private ConstInitValEle constInitValEle;

    public ConstInitValParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public ConstInitVal parseConstInitVal() {
        Token token = this.iterator.readNextToken();
        if (token.getType().equals(TokenType.LBRACE)) { // '{'
            this.iterator.unReadToken(1);
            ConstInitValMultiParser constInitValMultiParser =
                    new ConstInitValMultiParser(this.iterator);
            this.constInitValEle = constInitValMultiParser.parseConstInitValMulti();
        } else {
            this.iterator.unReadToken(1);
            ConstExpParser constExpParser = new ConstExpParser(this.iterator);
            this.constInitValEle = constExpParser.parseConstExp();
        }
        ConstInitVal constInitVal = new ConstInitVal(this.constInitValEle);
        return constInitVal;
    }
}
