package frontend.parser.expression.primaryexp;

import frontend.parser.SyntaxNode;

/**
 * '(' <Exp> ')' | <LVal> | <Number>
 */
public interface PrimaryExpEle extends SyntaxNode {
    int getDimension();
}
