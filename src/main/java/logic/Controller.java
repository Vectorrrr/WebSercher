package logic;

import model.UserAnswerFormat;
import view.View;
import view.reader.ConsoleReader;
import view.writer.ConsoleWriter;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by igladush on 01.03.16.
 */
public class Controller {
    private final String ERROR_READ_PROPERTY="I can't read property";
    private View view;
    private ConsoleReader consoleReader;
    private ConsoleWriter consoleWriter;
    private Index index;
    private TfCalcer tfCalcer;

    public Controller() {
        view = new View();
        consoleReader = new ConsoleReader();
        consoleWriter = new ConsoleWriter();
        tfCalcer = new TfCalcer();
        index=new Index();
    }

    public void run() {
        while (true) {
            view.showMainMenu();
            String userAns = consoleReader.read();

            switch (userAns) {
                case ("1"):
                    consoleWriter.write("Input path to directory");
                    String inputString = consoleReader.read();
                    index.indexedDirectory(inputString);
                    break;
                case ("2"):
                    consoleWriter.write("Input path to directory that you want recalc");
                    inputString = consoleReader.read();
                    index.indexedDirectory(inputString);
                    break;
                case("3"):
                    consoleWriter.write("Input path to directory and word");
                    inputString=consoleReader.read();
                    for(UserAnswerFormat useAns:tfCalcer.searchWord(inputString)){
                        consoleWriter.write(useAns.toString());
                    }
                    break;
                case("4"):
                    Properties p = new Properties();
                    try {
                        p.load(new FileInputStream("property.txt"));
                    } catch (IOException e) {
                        consoleWriter.write(ERROR_READ_PROPERTY);
                        break;
                    }
                    String defaultString =p.get("defaultDirectory").toString();
                    consoleWriter.write("Input your words");
                    inputString=consoleReader.read();
                    for(UserAnswerFormat useAns:tfCalcer.searchWord(defaultString+" "+inputString)){
                        consoleWriter.write(useAns.toString());
                    }
                    break;
                case ("0"):
                    view.buy();
                    return;
            }
        }

    }
}