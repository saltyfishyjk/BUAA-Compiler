package backend.function;

import backend.function.MipsFunction;
import middle.llvmir.value.function.IrFunction;

public class MipsFunctionBuilder {
    private IrFunction irFunction;

    public MipsFunctionBuilder(IrFunction irFunction) {
        this.irFunction = irFunction;
    }

    public MipsFunction genMipsFunction() {
        /* TODO : 待施工 */
        return null;
    }
}
