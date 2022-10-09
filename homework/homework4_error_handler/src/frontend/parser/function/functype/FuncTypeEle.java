package frontend.parser.function.functype;

import frontend.lexer.TokenType;
import frontend.parser.SyntaxNode;

public interface FuncTypeEle extends SyntaxNode {
    TokenType getType();
}
