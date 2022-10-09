package frontend.parser.declaration.constant;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;
import frontend.parser.declaration.BType;
import frontend.parser.declaration.BTypeParser;
import middle.error.Error;
import middle.error.ErrorTable;
import middle.error.ErrorType;
import middle.symbol.SymbolTable;

import java.util.ArrayList;

public class ConstDeclParser {
    private TokenListIterator iterator;
    /* ConstDecl Attributes */
    private Token constTk = null; // 'const'
    private BType btype = null;
    private ConstDef first = null;
    private ArrayList<Token> commas = new ArrayList<>();
    private ArrayList<ConstDef> constDefs = new ArrayList<>();
    private Token semicn = null; // ';'
    private SymbolTable curSymbolTable;

    public ConstDeclParser(TokenListIterator iterator) {
        this.iterator =  iterator;
    }

    public ConstDeclParser(TokenListIterator iterator, SymbolTable curSymbolTable) {
        this.iterator = iterator;
        this.curSymbolTable = curSymbolTable;
    }

    public ConstDecl parseConstDecl() {
        this.commas = new ArrayList<>();
        this.constDefs = new ArrayList<>();
        Token token = this.iterator.readNextToken(); // SHOULD be CONST
        /* MAY need handle error */
        if (token.getType().equals(TokenType.CONSTTK)) {
            constTk = token;
        } else {
            System.out.println("ERROR : EXPECT CONSTTK");
        }
        BTypeParser btypeParser = new BTypeParser(this.iterator);
        btype = btypeParser.parseBtype();
        //ConstDefParser constDefParser = new ConstDefParser(this.iterator);
        ConstDefParser constDefParser = new ConstDefParser(this.iterator, this.curSymbolTable);
        first = constDefParser.parseConstDef();
        token = this.iterator.readNextToken();
        while (token.getType().equals(TokenType.COMMA)) {
            /* is ',' */
            this.commas.add(token);
            this.constDefs.add(constDefParser.parseConstDef());
            token = this.iterator.readNextToken();
        }
        /* 处理i类错误：缺失; */
        handleIError(token);
        ConstDecl constDecl = new ConstDecl(this.constTk, this.btype,
                this.first, this.commas, this.constDefs, this.semicn);
        return constDecl;
    }

    private void handleIError(Token token) {
        this.semicn = token;
        if (!this.semicn.getType().equals(TokenType.SEMICN)) {
            this.iterator.unReadToken(2); // 后退两格以方便确定分号上一个非终结符位置
            Error error = new Error(this.iterator.readNextToken().getLineNum(),
                    ErrorType.MISSING_SEMICN);
            ErrorTable.addError(error);
        }
    }

}
