import backend.MipsBuilder;
import backend.MipsModule;
import frontend.SourceFileLexer;
import frontend.lexer.TokenLexer;
import frontend.parser.CompUnit;
import frontend.parser.CompUnitParser;
import middle.error.ErrorTable;
import middle.llvmir.IrBuilder;
import middle.llvmir.IrModule;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class Compiler {
    /**
     * 3 -> llvm_ir
     * 4 -> mips
     */
    private static int choose = 4;
    private static boolean testllvm = true;
    private static boolean testError = true;

    public static void main(String[] args) {
        String inputFileName = "testfile.txt"; // 注意文件路径的书写，是以相对项目而言的
        InputStream inputFileStream = null;
        try {
            inputFileStream = new FileInputStream(inputFileName);
        } catch (FileNotFoundException e) {
            System.err.println("Can not open " + inputFileName);
        }
        SourceFileLexer sourceFileLexer = new SourceFileLexer(inputFileStream);
        TokenLexer tokenLexer = new TokenLexer(sourceFileLexer);
        CompUnitParser compUnitParser = new CompUnitParser(tokenLexer.getTokenList());
        CompUnit compUnit = compUnitParser.parseCompUnit();
        IrBuilder irBuilder = new IrBuilder(compUnit);
        IrModule irModule = irBuilder.genIrModule();
        //String outputFileName = "output.txt";
        String errorFileName = "error.txt";
        String llvmFileName = "llvm_ir.txt";
        String mipsFileName = "mips.txt";
        if (choose == 2 || testError) {
            try {
                // OutputStream outputStream = new FileOutputStream(outputFileName);
                OutputStream outputStream = new FileOutputStream(errorFileName);
                try {
                    outputStream.write(ErrorTable.output().getBytes());
                    //outputStream.write(compUnit.syntaxOutput().getBytes());
                } catch (IOException e) {
                    //System.err.println("Can not write " + outputFileName);
                    System.err.println("Can not write " + errorFileName);
                }
            } catch (FileNotFoundException e) {
                // System.err.println("Can not open " + outputFileName);
                System.err.println("Can not open " + errorFileName);
            }
        } else if (choose == 3 || testllvm) {
            ArrayList<String> irs = irModule.irOutput();
            String ans = "";
            int tableCnt = 0;
            for (String s : irs) {
                if (s.contains("}")) {
                    tableCnt -= 1;
                }
                if (tableCnt > 0) {
                    String temp = "";
                    for (int j = 0; j < tableCnt; j++) {
                        temp = temp + "    ";
                    }
                    ans = ans + temp + s;
                } else {
                    ans = ans + s;
                }
                if (s.contains("{")) {
                    tableCnt += 1;
                }
            }
            try {
                OutputStream outputStream = new FileOutputStream(llvmFileName);
                try {
                    outputStream.write(ans.getBytes());
                } catch (IOException e) {
                    System.err.println("Can not write " + llvmFileName);
                }
            } catch (FileNotFoundException e) {
                System.err.println("Can not open " + llvmFileName);
            }
        }
        if (choose == 4) {
            MipsBuilder mipsBuilder = new MipsBuilder(irModule);
            MipsModule mipsModule = mipsBuilder.genMipsModule();
            ArrayList<String> mips = mipsModule.mipsOutput();
            String ans = "";
            int tableCnt = 0; // 换行符个数
            String table = "";
            String tableSing = "    ";
            /* 制表符美化mips代码 */
            for (String index : mips) {
                if (index.contains("**********")) {
                    tableCnt -= 1;
                    table = "";
                    for (int j = 0; j < tableCnt; j++) {
                        table = table + tableSing;
                    }
                }
                if (index.contains(":") && tableCnt > 0) {
                    ans = ans + table.substring(0, table.length() - 4) + index;
                } else {
                    ans = ans + table + index;
                }
                if (index.contains("----------")) {
                    tableCnt += 1;
                    table = table + tableSing;
                }
            }
            try {
                OutputStream outputStream = new FileOutputStream(mipsFileName);
                try {
                    outputStream.write(ans.getBytes());
                } catch (IOException e) {
                    System.err.println("Can not write " + mipsFileName);
                }
            } catch (FileNotFoundException e) {
                System.err.println("Can not open " + mipsFileName);
            }
        }
        //System.out.println(tokenLexer.getTokenList());
    }
}
