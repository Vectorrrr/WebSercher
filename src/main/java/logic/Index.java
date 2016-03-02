package logic;

import model.*;
import view.reader.FileReader;
import view.writer.ConsoleWriter;
import view.writer.IndexWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;

import static java.lang.Thread.yield;


/**
 * Created by igladush on 01.03.16.
 */
public class Index  implements Runnable {
    private final String ERROR_CREATE = "I can't create file information";
    private final String ERROR_READ_PROPERTY = "I can't read property";
    private final String ERROR_CORRECT = "The directory doesn't correct";
    private final String ERROR_FUTURE = "When  use future we have error!";

    private  ExecutorService service = Executors.newFixedThreadPool(10);
    private String path;
    private ConsoleWriter consoleWriter;
    private volatile boolean isShare=false;

    @Override
    public void run() {
        indexedDirectory();
    }


    public Index() {
        consoleWriter = new ConsoleWriter();
        Properties p=new Properties();
        try {
            p.load(new FileInputStream("property.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            this.path=new File(".").getCanonicalPath()+p.getProperty("path");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void indexedDirectory() {
        File directory = new File(path);

        if (!isCorrectDirectory(directory)) {
            throw new IllegalArgumentException(ERROR_CORRECT);
        }
        if(isShare){
           consoleWriter.write("I'm indexing now!");
        }
        else {
            isShare=true;
            List<IndexFile> indexDirectory = createPreindex(directory);
            calcIdf(indexDirectory);
            saveIndex(indexDirectory, directory.getAbsolutePath());
            isShare=false;
        }

    }


    private List<IndexFile> createPreindex(File indexDirectory) {

        Future<List<IndexFile>> future = service.submit(new FileShare(indexDirectory));
        while (!future.isDone()) {
            yield();
        }
        List<IndexFile> answer = new ArrayList<>();

        try {
            answer = future.get();
        } catch (InterruptedException e) {
            consoleWriter.write(ERROR_FUTURE + "InterruptedException");
        } catch (ExecutionException e) {
            consoleWriter.write(ERROR_FUTURE + "ExecutionException");
        }
        return answer;
    }

    private boolean isCorrectDirectory(File file) {
        return file.exists() && !file.isFile();
    }

    private boolean containsIndexFile(File directory) {
        Properties p = new Properties();
        try {
            p.load(new FileInputStream("property.txt"));
        } catch (IOException e) {
            System.err.print(ERROR_READ_PROPERTY);
        }
        String indexFileName = p.get("FileAnswerName").toString();
        for (File f : directory.listFiles()) {
            if (f.isFile() && indexFileName.equals(f.getName())) {
                return true;
            }
        }
        return false;
    }

    private void calcIdf(List<IndexFile> files) {
        int countFiles = files.size();

        for (IndexFile file : files) {
            for (IndexWord word : file.getWords()) {
                double count = 0;
                for (IndexFile temp : files) {
                    if (temp.containsWord(word.getWord())) {
                        count++;
                    }
                }
                word.setIdf(countFiles / count);
            }
        }
    }

    private void saveIndex(List<IndexFile> files, String path) {
        try (IndexWriter indexWriter = new IndexWriter()) {
            indexWriter.write(files);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public  void stop() {
        service.shutdown();
        try {
            service.awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
       service.shutdownNow();
    }

    private class FileShare implements Callable<List<IndexFile>> {
        private File directory;
        private List<IndexFile> indexFile = new ArrayList<>();

        public FileShare(File f) {
            this.directory = f;
        }

        @Override
        public List<IndexFile> call() throws Exception {
            for (File file : getFiles(directory)) {
                if (file.canRead()) {
                    indexFile.add(prepackFile(createFileInformation(file)));
                }
            }
            return indexFile;
        }

        private List<File> getFiles(File f) {
            FilenameFilter only = new FilenameFilter() {
                private String ext = ".txt";

                @Override
                public boolean accept(File file, String s) {
                    return s.endsWith(ext);
                }
            };
            List<File> answer = new ArrayList<>();
            for (File file : f.listFiles(only)) {
                if (file.canRead()) {
                    answer.add(file);
                }
            }
            return answer;
        }

        private FileInformation createFileInformation(File file) {
            System.out.println("Read file....." + file.getAbsolutePath());

            FileInformation fileInformation = new FileInformation(file.getAbsolutePath());

            try (FileReader fileReader = new FileReader(file)) {
                while (fileReader.canRead()) {

                    String[] temp = fileReader.read().split("[,. /]");
                    for (String s : temp) {
                        if (s.length() > 0) {
                            System.out.println("Add word " + s);
                            fileInformation.addWord(s);
                        }
                    }
                }
            } catch (IOException e) {
                consoleWriter.write(ERROR_CREATE);
            }
            return fileInformation;
        }

        private IndexFile prepackFile(FileInformation fileInformation) {
            IndexFile file = new IndexFile(fileInformation.getPath());

            for (WordCount wordCount : fileInformation.getWords()) {
                double tf = (1.0 * wordCount.getCount()) / fileInformation.totalCount();
                IndexWord word = new IndexWord(wordCount.getWord(), tf);
                file.addWord(word);
            }
            return file;
        }


    }

}