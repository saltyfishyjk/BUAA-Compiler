package frontend.parser.declaration.variable.vardef;

import frontend.lexer.Token;
import frontend.parser.SyntaxNode;
import frontend.parser.terminal.Ident;

import java.util.ArrayList;

public interface VarDefEle extends SyntaxNode {
    Ident getIdent();

    ArrayList<Token> getLeftBraces();

}
