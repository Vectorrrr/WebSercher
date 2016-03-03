package model;

/**
 * Created by igladush on 01.03.16.
 */
public class WordStatistics {

    private String word;
    private long count;

    public String getWord() {
        return word;
    }

    public long getCount() {
        return count;
    }

    public void incCount() {
        this.count++;
    }

    public WordStatistics(String word) {
        this.word = word;
        this.count = 1;
    }
    public WordStatistics(String word,int count){
        this(word);
        this.count=count;

    }
    @Override
    public String toString() {
        return word + " " + count + " ";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;

        if (!(o instanceof String)) {
            return false;
        }
        String s = (String) o;

        return s.equals(word);
    }

}