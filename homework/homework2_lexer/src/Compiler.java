import frontend.SourceFileLexer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Compiler {
    public static void main(String[] args) {
        String inputFileName = "src/testfile.txt"; // 注意文件路径的书写，是以相对项目而言的
        InputStream inputFileStream = null;
        try {
            inputFileStream = new FileInputStream(inputFileName);
        } catch (FileNotFoundException e) {
            System.err.println("Can not open " + inputFileName);
        }
        SourceFileLexer sourceFileLexer = new SourceFileLexer(inputFileStream);
    }
}
