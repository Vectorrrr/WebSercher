package logic;

import model.IndexFile;
import model.UserAnswerFormat;
import view.reader.TfIdfFileReader;
import view.writer.ConsoleWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Created by igladush on 01.03.16.
 */
public class TfCalcer {
    private final String ERROR_READ = "I can't read file";
    private final String ERROR_READ_PROPERTY = "I can't read property file";
    private final String ERROR_GET_CANONICAL_PATH="I can't get canonical path";

    private ConsoleWriter consoleWriter= new ConsoleWriter();;
    private String path;
    public TfCalcer() {
        Properties p = new Properties();
        try {
            p.load(new FileInputStream("property.txt"));
        } catch (IOException e) {
           consoleWriter.write(ERROR_READ_PROPERTY);
        }

        try {
            this.path=new File(".").getCanonicalPath()+p.getProperty("path");
        } catch (IOException e) {
           consoleWriter.write(ERROR_GET_CANONICAL_PATH);
        }


    }

    public TfCalcer(String path){
        this.path=path;
    }

    public List<UserAnswerFormat> searchWord(String word) {
        if (!checkPath(path) ) {
            return new ArrayList<>();
        }

        List<UserAnswerFormat> answer = new ArrayList<>();
        for (IndexFile tfFile : readIndexFile(path)) {
            if (tfFile.containsWord(word)) {
                answer.add(new UserAnswerFormat(tfFile.getPath(), tfFile.getWord(word)));
            }
        }
        Collections.sort(answer);
        return answer;

    }

    private List<IndexFile> readIndexFile(String path) {
        Properties p = new Properties();
        try {
            p.load(new FileInputStream("property.txt"));
        } catch (IOException e) {
            consoleWriter.write(ERROR_READ_PROPERTY);
        }

        String indexFile = p.get("FileAnswerName").toString();
        try (TfIdfFileReader tfReader = new TfIdfFileReader(path + "/" + indexFile)) {
            return tfReader.read();

        } catch (IOException e) {
            throw new IllegalArgumentException(ERROR_READ);
        }
    }

    private boolean checkPath(String path) {
        return new File(path).exists();

    }

}