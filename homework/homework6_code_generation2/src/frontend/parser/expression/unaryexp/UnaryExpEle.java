package frontend.parser.expression.unaryexp;

import frontend.parser.SyntaxNode;
import middle.symbol.ValNode;

/**
 * UnaryExp文法基类接口
 */
public interface UnaryExpEle extends SyntaxNode, ValNode {
    int getDimension();
}
