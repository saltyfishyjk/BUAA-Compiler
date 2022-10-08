package frontend.parser.declaration;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;
import frontend.parser.declaration.constant.ConstDeclParser;
import frontend.parser.declaration.variable.VarDeclParser;

public class DeclParser {
    private TokenListIterator iterator;

    public DeclParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public Decl parseDecl() {
        Token first = this.iterator.readNextToken();
        this.iterator.unReadToken(1);
        DeclEle declEle = null;
        if (first.getType().equals(TokenType.CONSTTK)) {
            ConstDeclParser constDeclParser = new ConstDeclParser(this.iterator);
            declEle = constDeclParser.parseConstDecl();
        } else if (first.getType().equals(TokenType.INTTK)) {
            VarDeclParser varDeclParser = new VarDeclParser(this.iterator);
            declEle = varDeclParser.parseVarDecl();
        } else {
            /* ERROR */
            System.out.println("READ UNEXPECTED TOKEN ");
        }
        Decl decl = new Decl(declEle);
        return decl;
    }
}
