package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by igladush on 01.03.16.
 */
public class FileStatistics {
    private String path;
    private List<WordStatistics> wordsCount = new ArrayList<>();


    public String getPath() {
        return this.path;
    }

    public FileStatistics(String path) {
        this.path = path;
    }

    public int getCount(){
        return wordsCount.size();
    }
    public int getTotalCount() {
        int answer = 0;
        for (WordStatistics wordCount : wordsCount) {
            answer += wordCount.getCount();
        }
        return answer;
    }

    public Collection<WordStatistics> getWords() {
        return Collections.unmodifiableCollection(wordsCount);
    }

    public void addWord(String word) {
        for (WordStatistics w : wordsCount) {
            if (w.getWord().equals(word)) {
                w.incCount();
                return;
            }
        }
        wordsCount.add(new WordStatistics(word));
    }

    private boolean containsWord(String word){
        for(WordStatistics w: wordsCount){
            if(w.getWord().equals(word)){
                return true;
            }
        }
        return false;
    }

    public boolean containsWord(WordStatistics word) {
        return containsWord(word.getWord());
    }
}