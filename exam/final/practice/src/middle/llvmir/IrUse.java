package middle.llvmir;

/**
 * 实现了Use内部类，本质上相当于维护一条User<->Value的双向边
 * 实际上是通过在User和Value对象中都维护一个保存Use对象的容器来实现的
 */
public class IrUse {
    // Value在User中是第几个操作数，
    // 比如 add i32 %a,%b 中 %a 是第0个，记录这个关系的Use的operandRank就是0，%b是第1个
    private int operandRank;
    private IrUser user;
    private IrValue value;

    public IrUse(IrValue value, IrUser user, int operandRank) {
        this.user = user;
        this.value = value;
        this.operandRank = operandRank;
        this.value.addUse(this);
        this.user.addUse(this);
    }

    public void setValue(IrValue value) {
        this.value = value;
    }

    public IrValue getValue() {
        return value;
    }

    public int getOperandRank() {
        return operandRank;
    }
}