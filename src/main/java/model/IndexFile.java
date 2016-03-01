package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by igladush on 01.03.16.
 */
public class IndexFile {
    private final String ERROR_GET_WORD = "I can't finde word";

    private String path;
    private List<IndexWord> words;

    public String getPath() {
        return this.path;
    }

    public IndexFile(String path) {
        this.path = path;
        words = new ArrayList<>();
    }

    public boolean containsWord(String word) {

        return words.contains(new IndexWord(word, 1.0));
    }

    public Collection<IndexWord> getWords() {
        return Collections.unmodifiableCollection(words);

    }

    public void addWord(IndexWord word) {
        if (word == null) {
            return;
        }
        if (words.contains(word)) {
            return;
        }
        words.add(word);
    }

    public IndexWord getWord(String word) {
        for (IndexWord temp : words) {
            if (temp.getWord().equals(word)) {
                return temp;
            }
        }
        throw new IllegalStateException(ERROR_GET_WORD);
    }


    public String toString() {
        return path + " " + words.size() + " ";
    }
}