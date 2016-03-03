package logic;

import model.UserAnswerFormat;
import view.View;
import view.reader.ConsoleReader;
import view.writer.ConsoleWriter;


import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by igladush on 01.03.16.
 */
public class Controller {;

    private View view;
    private ConsoleReader consoleReader;
    private ConsoleWriter consoleWriter;
    private Index index;
    private Searcher tfCalcer;

    private  AtomicInteger isIndexed=new AtomicInteger();

    public Controller() {
        view = new View();
        consoleReader = new ConsoleReader();
        consoleWriter = new ConsoleWriter();
        tfCalcer = new Searcher();
        index=new Index();
    }

    public void run() {
        while (true) {
            view.showMainMenu();
            String userAns = consoleReader.read();
            switch (userAns) {
                case ("1"):
                    if(isIndexed.get()!=0){
                           consoleWriter.write("I can't index now!");
                        break;
                    }
                    isIndexed.addAndGet(-1);
                    index.indexedDirectory();
                    isIndexed.addAndGet(1);
                    break;
                case("2"):
                    if(isIndexed.get()<0){
                        consoleWriter.write("I can't search now. I'm indexing now!!!!");
                    }
                    isIndexed.addAndGet(1);
                    consoleWriter.write("Input your words");
                    String inputString=consoleReader.read();
                    for(UserAnswerFormat useAns:tfCalcer.searchWord(inputString)){
                        consoleWriter.write(useAns.toString());
                    }
                    isIndexed.addAndGet(-1);
                    break;
                case ("0"):
                    view.buy();
                    index.stop();
                    return;
            }
        }

    }

}