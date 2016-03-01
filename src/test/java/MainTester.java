import logic.TfCalcer;
import model.UserAnswerFormat;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by igladush on 26.02.16.
 */
public class MainTester {
    private final static String ERROR_WRITE = "I cna't write in the file";

    private final static double ACCURACY = 0.000000001;
    private TfCalcer calcer;

    private String path;
    private String firstFile;
    private String secondFile;
    private String thirdFile;

    @Before
    public void init() throws FileNotFoundException {

        Properties p = new Properties();
        try {
            p.load(new FileInputStream("property.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        path = p.get("path").toString();
        firstFile=p.get("firstFile").toString();
        secondFile=p.get("secondFile").toString();
        thirdFile=p.get("thirdFile").toString();

        calcer = new TfCalcer();

        File file = new File(path);

        if (!file.exists()) {
            file.mkdirs();
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

    }

    @org.junit.Test
    public void existWordTest() {
        List<UserAnswerFormat> ans = calcer.searchWord(path + " VVVV");
        assertEquals(0, ans.size());
    }

    @Test
    public void oneAnswerWordTest() {
        List<UserAnswerFormat> usFormat = calcer.searchWord(path + " Sveta");
        assertEquals(1, usFormat.size());
        assertTrue((path + thirdFile).equals(usFormat.get(0).getPath()));
        assertEquals(1.5, usFormat.get(0).getTfIdf(), ACCURACY);
    }

    @Test
    public void twoAnswerWordTest() {
        List<UserAnswerFormat> usFormat = calcer.searchWord(path + " Vanya");
        assertEquals(2, usFormat.size());
        assertTrue((path + secondFile).equals(usFormat.get(0).getPath()));
        assertEquals(0.75, usFormat.get(0).getTfIdf(), ACCURACY);

        assertTrue((path + firstFile).equals(usFormat.get(1).getPath()));
        assertEquals(0.375, usFormat.get(1).getTfIdf(), ACCURACY);
    }

    @Test
    public void threeAnswerWordTest() {
        List<UserAnswerFormat> usFormat = calcer.searchWord(path + " Petya");
        assertEquals(3, usFormat.size());
        assertTrue((path + thirdFile).equals(usFormat.get(0).getPath()));
        assertEquals(0.5, usFormat.get(0).getTfIdf(), ACCURACY);

        assertTrue((path + firstFile).equals(usFormat.get(1).getPath()));
        assertEquals(0.25, usFormat.get(1).getTfIdf(), ACCURACY);

        assertTrue((path + secondFile).equals(usFormat.get(2).getPath()));
        assertEquals(0.25, usFormat.get(2).getTfIdf(), ACCURACY);
    }

}
