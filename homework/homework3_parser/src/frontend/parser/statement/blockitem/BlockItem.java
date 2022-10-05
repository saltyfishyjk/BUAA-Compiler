package frontend.parser.statement.blockitem;

import frontend.parser.SyntaxNode;

/**
 * 语句块项
 * 包含Decl | Stmt两种情况
 */
public class BlockItem implements SyntaxNode {
    private final String name = "<BlockItem>";
    private BlockItemEle blockItemEle;

    public BlockItem(BlockItemEle blockItemEle) {
        this.blockItemEle = blockItemEle;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.blockItemEle.syntaxOutput());
        //sb.append(this.name + "\n");
        return sb.toString();
    }
}
