package middle.llvmir;

import middle.llvmir.type.IrValueType;
import middle.llvmir.value.IrNode;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * 在LLVM IR中，【几乎】所有实体都是Value的子类
 */
public class IrValue implements IrNode {
    private IrValueType valueType; // Value 类型
    private String name; //
    private boolean needName; // TODO
    private LinkedList<IrUse> uses;
    private boolean isParam = false;
    private int dimension = 0;
    private int dimension1 = 0;
    private int dimension2 = 0;
    private ArrayList<Integer> inits1;
    private ArrayList<ArrayList<Integer>> inits2;

    public IrValue(IrValueType valueType) {
        this.valueType = valueType;
        this.name = "";
        this.needName = true;
        this.uses = new LinkedList<>();
    }

    public IrValue(IrValueType valueType, String name) {
        this(valueType);
        this.name = name;
    }

    public IrValue(IrValueType valueType, String name, boolean isParam) {
        this(valueType, name);
        this.isParam = isParam;
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

    public String getName() {
        return this.name;
    }

    @Override
    public ArrayList<String> irOutput() {
        ArrayList<String> ret = new ArrayList<>();
        ret.add(this.name);
        return ret;
    }

    public boolean isParam() {
        return this.isParam;
    }

    public void setInits1(ArrayList<Integer> inits1) {
        this.inits1 = inits1;
    }

    public void setInits2(ArrayList<ArrayList<Integer>> inits2) {
        this.inits2 = inits2;
    }

    public ArrayList<ArrayList<Integer>> getInits2() {
        return this.inits2;
    }

    public ArrayList<Integer> getInits1() {
        return this.inits1;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public void setDimension1(int dimension1) {
        this.dimension1 = dimension1;
    }

    public void setDimension2(int dimension2) {
        this.dimension2 = dimension2;
    }

    public int getDimension() {
        return this.dimension;
    }

    public int getDimension1() {
        return this.dimension1;
    }

    public int getDimension2() {
        return this.dimension2;
    }
}
