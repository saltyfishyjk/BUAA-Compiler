package middle.llvmir.value.constant;

import middle.llvmir.type.IrValueType;

import java.util.ArrayList;

/* TODO : 本次作业不涉及数组 */
/**
 * 数组数值常量
 * 如果是一维数组，每个元素是常数常量IrConstantInt
 * 如果是二维数组，每个元素是一维数组IrConstantArray
 */
public class IrConstantArray extends IrConstant {
    /* 如果是1维数组，用constants1表示 */
    private ArrayList<IrConstantInt> constants1;
    /* 如果是2维数组，用constants2表示 */
    private ArrayList<ArrayList<IrConstantInt>> constants2;

    private int dimension = 0; // 1表示这是1维数组，2表示这是2维数组
    private int dimension1 = 0; // 标记数组第1维的长度
    private int dimension2 = 0; // 标记数组第2维的长度

    /* 1维数组初始化 */
    public IrConstantArray(IrValueType type,
                           int dimension,
                           int dimension1,
                           ArrayList<IrConstantInt> arr) {
        super(type, arr.size());
        this.dimension = dimension;
        this.dimension1 = dimension1;
        int len = arr.size();
        for (int i = 0; i < len; i++) {
            this.setOperand(arr.get(i), i);
        }
        this.constants1 = arr;
    }

    /* 2维数组初始化 */
    public IrConstantArray(IrValueType type,
                           int dimension,
                           int dimension1,
                           int dimension2,
                           ArrayList<ArrayList<IrConstantInt>> arr) {
        super(type, dimension1 * dimension2);
        this.dimension = dimension;
        this.dimension1 = dimension1;
        this.dimension2 = dimension2;
        int cnt = 0;
        for (int i = 0; i < dimension1; i++) {
            if (i >= arr.size()) {
                break;
            }
            for (int j = 0; j < dimension2; j++) {
                this.setOperand(arr.get(i).get(j), cnt);
                cnt += 1;
            }
        }
        this.constants2 = arr;
    }

    @Override
    public ArrayList<String> irOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        if (this.dimension == 1) {
            /* 1维数组 */
            for (int i = 0; i < this.dimension1; i++) {
                if (i >= this.constants1.size()) {
                    break;
                }
                sb.append(this.constants1.get(i).irOutput().get(0));
                if (i != this.dimension1 - 1) {
                    sb.append(", ");
                }
            }
        } else if (this.dimension == 2) {
            for (int i = 0; i < this.dimension1; i++) {
                if (i >= this.constants2.size()) {
                    break;
                }
                sb.append("[");
                for (int j = 0; j < this.dimension2; j++) {
                    sb.append(this.constants2.get(i).get(j).irOutput().get(0));
                    if (j != this.dimension2 - 1) {
                        sb.append(", ");
                    }
                }
                sb.append("]");
                if (i != this.dimension1 - 1) {
                    sb.append(", ");
                }
            }
        } else {
            System.out.println("ERROR IN IrConstantArray : should not reach here");
        }
        sb.append("]");
        ArrayList<String> ret = new ArrayList<>();
        ret.add(sb.toString());
        return ret;
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

    public ArrayList<IrConstantInt> getConstants1() {
        return this.constants1;
    }

    public ArrayList<ArrayList<IrConstantInt>> getConstants2() {
        return this.constants2;
    }
}
