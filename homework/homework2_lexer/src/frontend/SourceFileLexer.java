package frontend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SourceFileLexer {
    private InputStream inputStream;
    private ArrayList<String> lines;

    public SourceFileLexer(InputStream inputStream) {
        this.inputStream = inputStream;
        this.lines = new ArrayList<>();
        readLines();
    }

    private void readLines() {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.inputStream));
        try {
            String lineNow = null;
            while (true) {
                lineNow = bufferedReader.readLine();
                if (lineNow == null) {
                    break;
                }
                this.lines.add(lineNow);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Hello");
    }
}
