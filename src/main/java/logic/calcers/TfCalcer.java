package logic.calcers;

import model.FileStatistics;
import model.WordStatistics;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by igladush on 03.03.16.
 */
public class TfCalcer {
    public static  List<Double> getTf(FileStatistics file){
        double countWord=file.getTotalCount();
        List<Double> answer=new ArrayList<>();
        for(WordStatistics word:file.getWords()){
            answer.add(word.getCount()/countWord);
        }
        return answer;
    }
 }
