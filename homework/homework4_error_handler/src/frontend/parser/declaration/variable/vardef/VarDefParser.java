package frontend.parser.declaration.variable.vardef;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;
import frontend.parser.declaration.variable.initval.InitVal;
import frontend.parser.declaration.variable.initval.InitValParser;
import frontend.parser.expression.ConstExp;
import frontend.parser.expression.ConstExpParser;
import frontend.parser.terminal.Ident;
import frontend.parser.terminal.IdentParser;

import java.util.ArrayList;

public class VarDefParser {
    private TokenListIterator iterator;
    /* VarDef Attributes */
    private Ident ident = null;
    private ArrayList<Token> leftBrackets = new ArrayList<>();
    private ArrayList<ConstExp> constExps = new ArrayList<>();
    private ArrayList<Token> rightBrackets = new ArrayList<>();
    /* Init Val */
    private Token eq = null;
    private InitVal initVal = null;
    /* VarDefEle */
    private VarDefEle varDefEle = null;

    public VarDefParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public VarDef parseVarDef() {
        this.leftBrackets = new ArrayList<>();
        this.constExps = new ArrayList<>();
        this.rightBrackets = new ArrayList<>();
        IdentParser identParser = new IdentParser(this.iterator);
        this.ident = identParser.parseIdent();
        Token token = this.iterator.readNextToken();
        while (token.getType().equals(TokenType.LBRACK)) {
            this.leftBrackets.add(token);
            ConstExpParser expParser = new ConstExpParser(this.iterator);
            this.constExps.add(expParser.parseConstExp());
            token = this.iterator.readNextToken();
            this.rightBrackets.add(token);
            token = this.iterator.readNextToken();
        }
        if (token.getType().equals(TokenType.ASSIGN)) { // '='
            this.eq = token;
            InitValParser initValParser = new InitValParser(this.iterator);
            this.initVal = initValParser.parseInitVal();
            this.varDefEle = new VarDefInit(this.ident, this.leftBrackets,
                    this.constExps, this.rightBrackets, this.eq, this.initVal);
        } else {
            // token now is ';', need to backspace
            this.iterator.unReadToken(1);
            this.varDefEle = new VarDefNull(this.ident, this.leftBrackets,
                    this.constExps, this.rightBrackets);
        }
        VarDef varDef = new VarDef(this.varDefEle);
        return varDef;
    }
}
