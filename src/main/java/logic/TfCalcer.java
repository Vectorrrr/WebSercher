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
    private final String ERROR_FORMAT_INPUT_DATA = "You input incorrect string";
    private final String ERROR_WORD_FORMAT = "You input incorrect word";

    private ConsoleWriter consoleWriter;


    private Index index = new Index();

    public TfCalcer() {
        consoleWriter = new ConsoleWriter();

    }

    public List<UserAnswerFormat> searchWord(String s) {
        String path = createPath(s);
        String word = createWord(s);
        if (!checkPath(path) || !index.isIndexed(path)) {
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

    private String createPath(String s) {
        String[] splitInput = s.split(" ");
        if (splitInput.length != 2) {
            throw new IllegalArgumentException(ERROR_FORMAT_INPUT_DATA);
        }
        return splitInput[0];
    }

    private String createWord(String s) {
        String[] split=s.split(" ");
        if (split.length != 2 ) {
            throw new IllegalArgumentException(ERROR_WORD_FORMAT);
        }
        return split[1];
    }

    private boolean checkPath(String path) {
        return new File(path).exists();

    }

}