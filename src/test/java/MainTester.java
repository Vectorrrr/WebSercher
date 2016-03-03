import logic.Index;
import logic.Searcher;
import logic.calcers.IdfCalcer;
import logic.calcers.TfCalcer;
import logic.callebls.FileCallable;
import model.FileStatistics;
import model.IndexFile;
import model.IndexWord;
import model.UserAnswerFormat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import view.reader.IndexReader;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;

/**
 * Created by igladush on 26.02.16.
 */
public class MainTester {
    private final static String ERROR_WRITE = "I cna't write in the file";

    private final static double ACCURACY = 0.000000001;
    private Searcher calcer;
    private List<FileStatistics> filesStatistics = new ArrayList<>();
    private ExecutorService executor = Executors.newFixedThreadPool(10);
    private Index index;

    private String path;
    private String pathTestDirectory;
    private String firstFile;
    private String secondFile;
    private String thirdFile;
    private String answerFileName;

    @Before
    public void init() throws FileNotFoundException {
        index = new Index();
        Properties p = new Properties();
        try {
            p.load(new FileInputStream("property.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            path = new File(".").getCanonicalPath() + p.get("testPath").toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        firstFile = p.get("firstFile").toString();
        secondFile = p.get("secondFile").toString();
        thirdFile = p.get("thirdFile").toString();
        answerFileName = p.getProperty("FileAnswerName");
        try {
            pathTestDirectory = new File(".").getCanonicalPath() + p.get("nameTestDirectory");
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file = new File(path);

        if (!file.exists()) {
            file.   mkdirs();
            try (Writer fw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(path + firstFile), "utf-8"))) {
                fw.write("Vasya, Vanya\nPetya Katya");
            } catch (IOException e) {
                System.out.println(ERROR_WRITE);
            }

            try (Writer fw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(path + secondFile), "utf-8"))) {
                fw.write("Vasya.\n Vanya\nPetya\nVanya");
            } catch (IOException e) {
                System.out.println(ERROR_WRITE);
            }

            try (Writer fw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(path + thirdFile), "utf-8"))) {
                fw.write("Petya\nSveta");
            } catch (IOException e) {
                System.out.println(ERROR_WRITE);
            }
        }
        calcer = new Searcher(path);
        createFileStatisticses();
    }

    private void createFileStatisticses() {
        FileStatistics fileStatistics = new FileStatistics("default1");
        for (int i = 0; i < 5; ++i) {
            fileStatistics.addWord("A");
        }
        for (int i = 0; i < 10; ++i) {
            fileStatistics.addWord("B");
        }
        for (int i = 0; i < 10; ++i) {
            fileStatistics.addWord("C");
        }
        filesStatistics.add(fileStatistics);

        fileStatistics = new FileStatistics("default2");
        for (int i = 0; i < 2; ++i) {
            fileStatistics.addWord("A");
        }
        for (int i = 0; i < 4; ++i) {
            fileStatistics.addWord("D");
        }
        for (int i = 0; i < 4; ++i) {
            fileStatistics.addWord("E");
        }
        filesStatistics.add(fileStatistics);

        fileStatistics = new FileStatistics("default3");
        for (int i = 0; i < 4; i++) fileStatistics.addWord("A");
        for (int i = 0; i < 6; i++) fileStatistics.addWord("B");
        filesStatistics.add(fileStatistics);

        fileStatistics = new FileStatistics("default4");
        for (int i = 0; i < 10; i++) fileStatistics.addWord("A");
        filesStatistics.add(fileStatistics);
    }

    @Test
    public void tfCalcerTest1() {
        List<Double> tf = TfCalcer.getTf(filesStatistics.get(0));
        assertEquals(3, tf.size());
        assertEquals(0.20, tf.get(0), ACCURACY);
        assertEquals(0.40, tf.get(1), ACCURACY);
        assertEquals(0.40, tf.get(2), ACCURACY);
    }

    @Test
    public void tfCalcerTest2() {
        List<Double> tf = TfCalcer.getTf(filesStatistics.get(1));

        assertEquals(3, tf.size());
        assertEquals(0.20, tf.get(0), ACCURACY);
        assertEquals(0.40, tf.get(1), ACCURACY);
        assertEquals(0.40, tf.get(2), ACCURACY);
    }

    @Test
    public void tfCalcerTest3() {
        List<Double> tf = TfCalcer.getTf(filesStatistics.get(2));

        assertEquals(2, tf.size());
        assertEquals(0.40, tf.get(0), ACCURACY);
        assertEquals(0.60, tf.get(1), ACCURACY);
    }

    @Test
    public void idfCalcerTest1() {
        List<Double> tf = IdfCalcer.getIdf(filesStatistics.get(0), filesStatistics);

        assertEquals(3, tf.size());
        assertEquals(1, tf.get(0), ACCURACY);
        assertEquals(2, tf.get(1), ACCURACY);
        assertEquals(4, tf.get(2), ACCURACY);

    }

    @Test
    public void idfCalcerTest2() {
        List<Double> tf = IdfCalcer.getIdf(filesStatistics.get(3), filesStatistics);

        assertEquals(1, tf.size());
        assertEquals(1, tf.get(0), ACCURACY);

    }

    @Test
    public void fileCallableTest1() {
        Future<IndexFile> future = executor.submit(new FileCallable(filesStatistics.get(0), filesStatistics));
        IndexFile indexFile = null;
        try {
            indexFile = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Collection<IndexWord> words = indexFile.getWords();

        assertEquals(3, words.size());
        double[] tfIdf = new double[]{0.2, 0.8, 1.6};
        int i = 0;
        for (IndexWord word : words) {
            assertEquals(word.getTfIdf(), tfIdf[i++], ACCURACY);
        }
    }

    @Test
    public void fileCallableTest2() {
        Future<IndexFile> future = executor.submit(new FileCallable(filesStatistics.get(1), filesStatistics));
        IndexFile indexFile = null;
        try {
            indexFile = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Collection<IndexWord> words = indexFile.getWords();

        assertEquals(3, words.size());
        double[] tfIdf = new double[]{0.2, 1.6, 1.6};
        int i = 0;
        for (IndexWord word : words) {
            assertEquals(word.getTfIdf(), tfIdf[i++], ACCURACY);
        }
    }

    @Test
    public void indexSomeDirectionTest1() {

        index.indexedSomeDirectory(pathTestDirectory);
        try (IndexReader indexReader = new IndexReader(pathTestDirectory + answerFileName)) {
            List<IndexFile> indexFiles = indexReader.read();

            assertEquals(pathTestDirectory + firstFile, indexFiles.get(0).getPath());
            assertEquals(pathTestDirectory + secondFile, indexFiles.get(1).getPath());
            assertEquals(pathTestDirectory + thirdFile, indexFiles.get(2).getPath());

            assertEquals("Vasya",indexFiles.get(0).getWord("Vasya").getWord());
            assertEquals(0.375,indexFiles.get(0).getWord("Vasya").getTfIdf(),ACCURACY);

            assertEquals("Vanya",indexFiles.get(0).getWord("Vanya").getWord());
            assertEquals(0.375,indexFiles.get(0).getWord("Vanya").getTfIdf(),ACCURACY);

            assertEquals("Petya",indexFiles.get(0).getWord("Petya").getWord());
            assertEquals(0.25,indexFiles.get(0).getWord("Petya").getTfIdf(),ACCURACY);

            assertEquals("Katya",indexFiles.get(0).getWord("Katya").getWord());
            assertEquals(0.75,indexFiles.get(0).getWord("Katya").getTfIdf(),ACCURACY);


            assertEquals("Vasya",indexFiles.get(1).getWord("Vasya").getWord());
            assertEquals(0.375,indexFiles.get(1).getWord("Vasya").getTfIdf(),ACCURACY);

            assertEquals("Vanya",indexFiles.get(1).getWord("Vanya").getWord());
            assertEquals(0.75,indexFiles.get(1).getWord("Vanya").getTfIdf(),ACCURACY);

            assertEquals("Petya",indexFiles.get(1).getWord("Petya").getWord());
            assertEquals(0.25,indexFiles.get(1).getWord("Petya").getTfIdf(),ACCURACY);

            assertEquals("Petya",indexFiles.get(2).getWord("Petya").getWord());
            assertEquals(0.5,indexFiles.get(2).getWord("Petya").getTfIdf(),ACCURACY);

            assertEquals("Sveta",indexFiles.get(2).getWord("Sveta").getWord());
            assertEquals(1.5,indexFiles.get(2).getWord("Sveta").getTfIdf(),ACCURACY);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void oneAnswerWordTest() {
        List<UserAnswerFormat> usFormat = calcer.searchWord("Sveta");
        assertEquals(1, usFormat.size());
        assertEquals(1.5, usFormat.get(0).getTfIdf(), ACCURACY);
    }

    @Test
    public void twoAnswerWordTest() {
        List<UserAnswerFormat> usFormat = calcer.searchWord("Vanya");
        assertEquals(2, usFormat.size());
        assertEquals(0.75, usFormat.get(0).getTfIdf(), ACCURACY);
        assertEquals(0.375, usFormat.get(1).getTfIdf(), ACCURACY);
    }

    @Test
    public void threeAnswerWordTest() {
        List<UserAnswerFormat> usFormat = calcer.searchWord("Petya");
        assertEquals(3, usFormat.size());
        assertEquals(0.5, usFormat.get(0).getTfIdf(), ACCURACY);
        assertEquals(0.25, usFormat.get(1).getTfIdf(), ACCURACY);
        assertEquals(0.25, usFormat.get(2).getTfIdf(), ACCURACY);
    }


    @After
    public void close() {
        executor.shutdownNow();
    }

}
