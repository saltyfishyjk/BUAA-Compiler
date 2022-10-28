package middle.llvmir;

import middle.llvmir.type.IrValueType;

import java.util.LinkedList;

/**
 * 在LLVM IR中，【几乎】所有实体都是Value的子类
 */
public class IrValue {
    private IrValueType valueType; // Value 类型
    private String name; //
    private boolean needName; // TODO
    private LinkedList<IrUse> uses;

    public IrValue(IrValueType valueType) {
        this.valueType = valueType;
        this.name = "";
        this.needName = true;
        this.uses = new LinkedList<>();
    }

    public void setValueType(IrValueType valueType) {
        this.valueType = valueType;
    }

    public void addUse(IrUse use) {
        this.uses.add(use);
    }

    public void removeUse(IrUse use) {
        this.uses.removeIf(h -> h.equals(use));
    }

    public IrValueType getValueType() {
        return valueType;
    }

    public void setName(String name) {
        this.name = name;
    }
}
