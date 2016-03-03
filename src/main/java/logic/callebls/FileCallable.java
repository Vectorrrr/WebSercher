package logic.callebls;

import logic.calcers.IdfCalcer;
import logic.calcers.TfCalcer;
import model.FileStatistics;
import model.IndexFile;
import model.IndexWord;
import model.WordStatistics;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by igladush on 03.03.16.
 */
public class FileCallable implements Callable<IndexFile> {

    private FileStatistics file;
    private List<FileStatistics> files;

    public FileCallable(FileStatistics file, List<FileStatistics> fileStatisticses) {
        this.file = file;
        this.files = fileStatisticses;
    }

    @Override
    public IndexFile call() throws Exception {
        List<Double> tf = TfCalcer.getTf(file);
        List<Double> idf = IdfCalcer.getIdf(file, files);

        IndexFile answer = new IndexFile(file.getPath());
        int i = 0;
        for (WordStatistics word : file.getWords()) {
            double tfWord = tf.get(i);
            double idfWord = idf.get(i++);
            answer.addWord(new IndexWord(word.getWord(), tfWord, idfWord));
        }
        return answer;
    }
}
