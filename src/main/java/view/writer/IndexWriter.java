package view.writer;

import model.IndexFile;
import model.IndexWord;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;

/**
 * Created by igladush on 01.03.16.
 */
public class IndexWriter implements AutoCloseable,Closeable {
    private final String ERROR_WRITER = "I have exception when i write in your file";
    public final static String SEPARATOR = "========";

    private ConsoleWriter consoleWriter;

    public IndexWriter() {
        consoleWriter = new ConsoleWriter();
    }

    public void write(Collection<IndexFile> tfIdfFileList, String path) {

        Properties p=new Properties();
        try {
            p.load(new FileInputStream("property.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        path=path+"/"+p.get("FileAnswerName").toString();

        byte[] separator=System.getProperty("line.separator").getBytes();
        try (FileOutputStream fileOutputStream = new FileOutputStream(path)) {
            for (IndexFile temp : tfIdfFileList) {
                fileOutputStream.write(temp.toString().getBytes());
                fileOutputStream.write(separator);
                for (IndexWord word:temp.getWords()) {
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
}