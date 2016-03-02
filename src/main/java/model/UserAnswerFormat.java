package model;

/**
 * Created by igladush on 01.03.16.
 */
public class UserAnswerFormat implements Comparable<UserAnswerFormat> {
    private String path;
    private IndexWord word;

    public String getPath() {
        return path;
    }

    public UserAnswerFormat(String path, IndexWord word) {
        this.path = path;
        this.word = word;
    }

    public double getTfIdf() {
        return word.getTfIdf();
    }

    @Override
    public String toString() {
        return path + " " + word.getTfIdf();
    }

    @Override
    public int compareTo(UserAnswerFormat o) {
        if (o == null || o.getTfIdf() < this.getTfIdf()) {
            return -1;
        }
        if (o.getTfIdf() < this.getTfIdf()) {
            return 0;
        }
        return 1;
    }
}