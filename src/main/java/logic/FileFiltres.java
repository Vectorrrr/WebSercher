package logic;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by igladush on 03.03.16.
 */
public enum FileFiltres {
    TXT() {
        @Override
        public List<File> getFiles(File directory) {
            FilenameFilter only = new FilenameFilter() {
                private String ext = ".txt";

                @Override
                public boolean accept(File file, String s) {
                    return s.endsWith(ext);
                }
            };
            List<File> answer = new ArrayList<>();
            for (File file : directory.listFiles(only)) {
                if (file.canRead()) {
                    answer.add(file);
                }
            }
            return answer;
        }
    };


    public abstract List<File> getFiles(File directory);

}
