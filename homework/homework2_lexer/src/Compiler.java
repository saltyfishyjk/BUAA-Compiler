import frontend.SourceFileLexer;
import frontend.lexer.TokenLexer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Compiler {
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
        String outputFileName = "output.txt";
        try {
            OutputStream outputStream = new FileOutputStream(outputFileName);
            try {
                outputStream.write(tokenLexer.getTokenList().toString().getBytes());
            } catch (IOException e) {
                System.err.println("Can not write " + outputFileName);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Can not open " + outputFileName);
        }
        //System.out.println(tokenLexer.getTokenList());
    }
}
