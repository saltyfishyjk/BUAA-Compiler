package middle.llvmir.value;

import middle.llvmir.IrUser;
import middle.llvmir.type.IrPointerType;
import middle.llvmir.type.IrValueType;
import middle.llvmir.value.constant.IrConstant;

/**
 * LLVM 全局变量
 */
public class IrGlobalVariable extends IrUser {
    private boolean isConst; // 标记是否是常量
    private IrConstant init; // 初始化的值

    public IrGlobalVariable(String name, IrValueType type) {
        super(new IrPointerType(type));
        this.setName(name);
    }
}
