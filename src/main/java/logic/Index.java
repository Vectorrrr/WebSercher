package logic;

import logic.callebls.FileCallable;
import model.*;
import view.writer.ConsoleWriter;
import view.writer.IndexWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;


/**
 * Created by igladush on 01.03.16.
 */
public class Index  implements Runnable {
    private final String ERROR_CREATE = "I can't create file information!";
    private final String ERROR_CORRECT = "The directory doesn't correct!";
    private final String ERROR_FUTURE_READ = "When we read file we have futures error!";
    private final String ERROR_FUTURE_INDEX = "When we calc index for directory we have error!";
    private final String ERROR_FUTURE_GET_INDEX = "When we get index file we have error!";
    private final String EMPTY_STRING = "";


    private volatile boolean isShare = false;
    private ExecutorService serviceFileProcessor = Executors.newFixedThreadPool(10);
    private ExecutorService serviceIndexFiles = Executors.newFixedThreadPool(10);
    private String path;
    private ConsoleWriter consoleWriter;

    @Override
    public void run() {
        indexedDirectory();
    }


    public Index() {
        consoleWriter = new ConsoleWriter();
        Properties p = new Properties();
        try {
            p.load(new FileInputStream("property.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            this.path = new File(".").getCanonicalPath() + p.getProperty("path");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void indexedDirectory() {
        File directory = new File(path);


        if (isShare) {
            consoleWriter.write("I'm indexing now!");
        } else {
            isShare = true;
            List<IndexFile> indexDirectory = createPreindex(directory);
            saveIndex(indexDirectory);
            isShare = false;
        }

    }

    public void indexedSomeDirectory(String someDirectory) {

        File directory = new File(someDirectory);
        if (!isCorrectDirectory(directory)) {
            throw new IllegalArgumentException(ERROR_CORRECT);
        }
        List<IndexFile> indexDirectory = createPreindex(directory);
        saveIndex(indexDirectory, someDirectory);
    }

    private List<IndexFile> createPreindex(File indexDirectory) {


        List<Future<FileStatistics>> futures = getFutures(indexDirectory);
        List<FileStatistics> answer = getFileStatisticses(futures);


        return createIndexFile(answer);
    }

    private List<IndexFile> createIndexFile(List<FileStatistics> files) {
        List<IndexFile> answer = new ArrayList<>();
        List<FileCallable> calls = new ArrayList<>();

        for (FileStatistics file : files) {
            calls.add(new FileCallable(file, files));
        }
        List<Future<IndexFile>> indexFiles = new ArrayList();
        try {
            indexFiles = serviceIndexFiles.invokeAll(calls);
        } catch (InterruptedException e) {
            consoleWriter.write(ERROR_FUTURE_INDEX);
            e.printStackTrace();
        }
        for (Future<IndexFile> future : indexFiles) {
            try {
                answer.add(future.get());
            } catch (InterruptedException e) {
                consoleWriter.write(ERROR_FUTURE_GET_INDEX + "InterruptedException");
                e.printStackTrace();
            } catch (ExecutionException e) {
                consoleWriter.write(ERROR_FUTURE_GET_INDEX + "ExecutionException");
                e.printStackTrace();
            }
        }
        return answer;

    }

    private List<FileStatistics> getFileStatisticses(List<Future<FileStatistics>> futures) {
        List<FileStatistics> answer = new ArrayList<>();

        for (Future<FileStatistics> future1 : futures) {
            try {
                answer.add(future1.get());
            } catch (InterruptedException e) {
                consoleWriter.write(ERROR_FUTURE_READ + "InterruptedException");
                e.printStackTrace();
            } catch (ExecutionException e) {
                consoleWriter.write(ERROR_FUTURE_READ + "ExecutionException");
                e.printStackTrace();
            }

        }
        return answer;
    }

    private List<Future<FileStatistics>> getFutures(File indexDirectory ) {
        List<Callable<FileStatistics>> calls = new ArrayList<>();
        for (File f : FileFiltres.valueOf("TXT").getFiles(indexDirectory)) {
            calls.add(new FileProcessor(f));
        }
        List<Future<FileStatistics>> futures = null;
        try {
            futures = serviceFileProcessor.invokeAll(calls);
        } catch (InterruptedException e) {
            consoleWriter.write(ERROR_FUTURE_READ);
            e.printStackTrace();
        }
        return futures;
    }

    private boolean isCorrectDirectory(File file) {
        return file.exists() && !file.isFile();
    }

    private void saveIndex(List<IndexFile> files) {
        try (IndexWriter indexWriter = new IndexWriter()) {
            indexWriter.write(files);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveIndex(List<IndexFile> files, String path) {
        try (IndexWriter indexWriter = new IndexWriter(path)) {
            indexWriter.write(files);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        serviceFileProcessor.shutdownNow();
        serviceIndexFiles.shutdownNow();
    }

    private class FileProcessor implements Callable<FileStatistics> {
        private File file;

        public FileProcessor(File f) {
            this.file = f;
        }

        @Override
        public FileStatistics call() throws Exception {
            if (file.canRead()) {
                return createFileInformation(file);
            }

            consoleWriter.write("I return null!!! BECOUSE I CAN'T READ THIS FILE");
            return null;
        }


        private FileStatistics createFileInformation(File file) {
            System.out.println("Read file....." + file.getAbsolutePath());

            FileStatistics fileInformation = new FileStatistics(file.getAbsolutePath());
            String read;
            try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {

                read = fileReader.readLine();
                while (!EMPTY_STRING.equals(read) && read != null) {
                    String[] temp = read.split("[,. /]");
                    for (String s : temp) {
                        if (s.length() > 0) {
                            System.out.println("Add word " + s);
                            fileInformation.addWord(s);
                        }
                    }
                    read = fileReader.readLine();
                }

            } catch (IOException e) {
                consoleWriter.write(ERROR_CREATE);
            }
            return fileInformation;
        }


    }

}