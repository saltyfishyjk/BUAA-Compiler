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

    /* 返回当前BlockItem与return的关系*/
    /* 返回0说明不是return语句 */
    /* 返回1说明是return; */
    /* 返回2说明是return xxx;*/
    public int checkReturn() {
        return this.blockItemEle.checkReturn();
    }

    public BlockItemEle getBlockItemEle() {
        return blockItemEle;
    }
}
