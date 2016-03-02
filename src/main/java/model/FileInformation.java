package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by igladush on 01.03.16.
 */
public class FileInformation {
    private String path;
    private List<WordCount> wordsCount = new ArrayList<>();


    public String getPath() {
        return this.path;
    }

    public FileInformation(String path) {
        this.path = path;
    }

    public int totalCount() {
        int answer = 0;
        for (WordCount wordCount : wordsCount) {
            answer += wordCount.getCount();
        }
        return answer;
    }

    public Collection<WordCount> getWords() {
        return Collections.unmodifiableCollection(wordsCount);
    }

    public void addWord(String word) {
        for (WordCount w : wordsCount) {
            if (w.getWord().equals(word)) {
                w.incCount();
                return;
            }
        }
        wordsCount.add(new WordCount(word));
    }
}