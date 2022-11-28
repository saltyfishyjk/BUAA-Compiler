package middle.llvmir.value.instructions;

/**
 * IrInstruction 类别枚举类
 * 将所有可能用到的在此处枚举
 * 具体地，可以分为三部分：
 * - Add等二元运算指令
 * - Br等终结指令（一定在基本块的末尾）
 * - Alloca等内存操作指令
 */
public enum IrInstructionType {
    /* Binary */
    /* Arithmetic Binary */
    Add,// +
    Sub,// -
    Mul,// *
    Div,// /
    Mod,// %
    /* Logic Binary */
    Lt, // <
    Le, // <=
    Ge, // >=
    Gt, // >
    Eq, // ==
    Ne, // !=
    And,// &
    Or, // |
    Not, // ! ONLY ONE PARAM
    Beq, // IrBeq branch if ==
    Bne, // IrBne branch if !=
    Blt, // IrBlt branch if less than <
    Ble, // IrBle branch if less or equal <=
    Bgt, // IrBgt branch if greater than >
    Bge, // IrBge branch if greater or equal >=
    Goto, // IrGoto
    /* Terminator */
    Br,
    Call,
    Ret,
    /* mem op */
    Alloca,
    Load,
    Store,
    GEP, // Get Element Ptr
    Zext,
    Phi,//用于 mem2reg
    /* label */
    Label,
}
