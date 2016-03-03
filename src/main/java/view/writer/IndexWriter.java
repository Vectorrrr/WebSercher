package view.writer;

import model.IndexFile;
import model.IndexWord;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 * Created by igladush on 01.03.16.
 */
public class IndexWriter implements Writer<IndexFile>, AutoCloseable,Closeable {
    private final String ERROR_WRITER = "I have exception when i write in your file";
    private final String ERROR_GET_CANONICAL_PATH = "I can't get canonical path";
    public final static String SEPARATOR = "========";

    private ConsoleWriter consoleWriter = new ConsoleWriter();
    private String path;

    public IndexWriter() {
        Properties p = new Properties();
        try {
            p.load(new FileInputStream("property.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            path = new File(".").getCanonicalPath() + p.getProperty("path") + p.get("FileAnswerName").toString();
        } catch (IOException e) {
            consoleWriter.write(ERROR_GET_CANONICAL_PATH);
        }
    }

    public IndexWriter(String path) {
        Properties p = new Properties();
        try {
            p.load(new FileInputStream("property.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.path = path + p.get("FileAnswerName").toString();
    }

    private void writes(Collection<IndexFile> tfIdfFileList) {
        byte[] separator = System.getProperty("line.separator").getBytes();
        try (FileOutputStream fileOutputStream = new FileOutputStream(path)) {

            for (IndexFile temp : tfIdfFileList) {
                fileOutputStream.write(temp.toString().getBytes());
                fileOutputStream.write(separator);
                for (IndexWord word : temp.getWords()) {
                    fileOutputStream.write(word.toString().getBytes());
                    fileOutputStream.write(separator);
                }
            }
            fileOutputStream.write(SEPARATOR.getBytes());

        } catch (IOException e) {
            consoleWriter.write(ERROR_WRITER);
        }
    }


    @Override
    public void close() throws IOException {
    }

    @Override
    public void write(IndexFile... s) {
        writes(Arrays.asList(s));
    }

    @Override
    public void write(List<IndexFile> s) {
        writes(s);
    }
}