package frontend.parser.declaration;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;
import frontend.parser.declaration.constant.ConstDeclParser;
import frontend.parser.declaration.variable.VarDeclParser;
import middle.symbol.SymbolTable;

public class DeclParser {
    private TokenListIterator iterator;
    private SymbolTable curSymbolTable;

    public DeclParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public DeclParser(TokenListIterator iterator, SymbolTable curSymbolTable) {
        this.iterator = iterator;
        this.curSymbolTable = curSymbolTable;
    }

    public Decl parseDecl() {
        Token first = this.iterator.readNextToken();
        this.iterator.unReadToken(1);
        DeclEle declEle = null;
        if (first.getType().equals(TokenType.CONSTTK)) {
            //ConstDeclParser constDeclParser = new ConstDeclParser(this.iterator);
            ConstDeclParser constDeclParser = new ConstDeclParser(this.iterator,
                    this.curSymbolTable);
            declEle = constDeclParser.parseConstDecl();
        } else if (first.getType().equals(TokenType.INTTK)) {
            //VarDeclParser varDeclParser = new VarDeclParser(this.iterator);
            VarDeclParser varDeclParser = new VarDeclParser(this.iterator, this.curSymbolTable);
            declEle = varDeclParser.parseVarDecl();
        } else {
            /* ERROR */
            System.out.println("READ UNEXPECTED TOKEN ");
        }
        Decl decl = new Decl(declEle);
        return decl;
    }
}
