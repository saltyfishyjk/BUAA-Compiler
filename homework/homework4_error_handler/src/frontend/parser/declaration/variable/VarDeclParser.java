package frontend.parser.declaration.variable;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;
import frontend.parser.declaration.BType;
import frontend.parser.declaration.BTypeParser;
import frontend.parser.declaration.variable.vardef.VarDef;
import frontend.parser.declaration.variable.vardef.VarDefParser;

import java.util.ArrayList;

public class VarDeclParser {
    private TokenListIterator iterator;
    /* VarDecl Attributes */
    private BType btype = null;
    private VarDef first = null;
    private ArrayList<Token> commas = new ArrayList<>(); // ','
    private ArrayList<VarDef> varDefs = new ArrayList<>();
    private Token semicn; // ';'

    public VarDeclParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public VarDecl parseVarDecl() {
        this.commas = new ArrayList<>();
        this.varDefs = new ArrayList<>();
        BTypeParser btypeparser = new BTypeParser(this.iterator);
        this.btype = btypeparser.parseBtype();
        VarDefParser varDefParser = new VarDefParser(this.iterator);
        this.first = varDefParser.parseVarDef();
        Token token = this.iterator.readNextToken();
        while (token.getType().equals(TokenType.COMMA)) { // ','
            this.commas.add(token);
            this.varDefs.add(varDefParser.parseVarDef());
            token = this.iterator.readNextToken();
        }
        this.semicn = token;
        VarDecl varDecl = new VarDecl(this.btype, this.first,
                this.commas, this.varDefs, this.semicn);
        return varDecl;
    }
}
