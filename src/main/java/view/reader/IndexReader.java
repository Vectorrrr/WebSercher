package view.reader;

import model.IndexFile;
import model.IndexWord;
import view.writer.ConsoleWriter;
import view.writer.IndexWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by igladush on 01.03.16.
 */
public  class IndexReader implements Reader<List<IndexFile>> {
    private final String ERROR_READ = "I can't read";

    private ConsoleWriter consoleWriter;
    private BufferedReader bufferedReader;
    private String nextLine;

    public IndexReader(String path) throws IOException {
        consoleWriter = new ConsoleWriter();
        bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
        nextLine = bufferedReader.readLine();
    }

    @Override
    public List<IndexFile> read() {
        List<IndexFile> answer = new ArrayList<>();
        IndexFile temp = null;
        while (canRead()) {
            String[] input = nextLine.split(" ");
            if (input.length == 2) {
                temp = new IndexFile(input[0]);
                answer.add(temp);
            } else {
                String word = input[0];
                double tf = Double.parseDouble(input[1]);
                double idf = Double.parseDouble(input[2]);
                temp.addWord(new IndexWord(word, tf, idf));
            }
            getNextLine();
        }
        return answer;
    }

    private boolean canRead() {
        return !IndexWriter.SEPARATOR.equals(nextLine);
    }

    private void getNextLine() {
        try {
            nextLine = bufferedReader.readLine();
        } catch (IOException e) {
            consoleWriter.write(ERROR_READ);
        }
    }


    @Override
    public void close() throws IOException {
        bufferedReader.close();
    }

}
