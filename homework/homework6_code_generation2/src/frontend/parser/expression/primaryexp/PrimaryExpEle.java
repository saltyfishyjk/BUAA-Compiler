package frontend.parser.expression.primaryexp;

import frontend.parser.SyntaxNode;
import middle.symbol.ValNode;

/**
 * '(' <Exp> ')' | <LVal> | <Number>
 */
public interface PrimaryExpEle extends SyntaxNode, ValNode {
    int getDimension();
}
