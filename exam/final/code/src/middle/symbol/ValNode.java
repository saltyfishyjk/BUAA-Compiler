package middle.symbol;

/**
 * ValNode
 * SysY中的可计算值结点
 * 根据给定的SymbolTable从本层开始向上递归寻找ident
 * 计算其值
 */
public interface ValNode {
    int calcNode(SymbolTable symbolTable);
}
