package view.reader;

import view.writer.ConsoleWriter;

import java.io.*;

/**
 * Created by igladush on 01.03.16.
 */
public class FileReader implements Reader,AutoCloseable,Closeable {
    private final String EMPTY_LINE = "";
    private final String ERROR_READ = "I can't read more!";

    private BufferedReader bufferedReader;
    private String nextLine;
    private ConsoleWriter consoleWriter = new ConsoleWriter();

    public FileReader(File file) throws IOException {
        bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        nextLine = bufferedReader.readLine();
    }

    private void getNextLine() {
        try {
            nextLine = bufferedReader.readLine();
        } catch (IOException e) {
            consoleWriter.write(ERROR_READ);
        }
    }

    public boolean canRead() {
        return nextLine != null && !EMPTY_LINE.equals(nextLine);
    }

    @Override
    public void close() throws IOException {
        bufferedReader.close();
    }

    @Override
    public String read() {
        String temp = nextLine;
        getNextLine();
        return temp;
    }

}