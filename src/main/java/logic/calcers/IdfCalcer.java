package logic.calcers;

import model.FileStatistics;
import model.WordStatistics;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by igladush on 03.03.16.
 */
public class IdfCalcer {
    public static  List<Double> getIdf(FileStatistics indexFile, List<FileStatistics> indexFileList) {
        double countFile = indexFileList.size();
        List<Double> answer = new ArrayList<>();

        for (WordStatistics word : indexFile.getWords()) {
            double count = 0;
            for (FileStatistics file : indexFileList) {
                if (file.containsWord(word)) {
                    count++;
                }
            }
            answer.add(countFile/count);
        }
        return answer;
    }

}
