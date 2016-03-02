package logic;

import model.UserAnswerFormat;
import view.View;
import view.reader.ConsoleReader;
import view.writer.ConsoleWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by igladush on 01.03.16.
 */
public class Controller {;

    private View view;
    private ConsoleReader consoleReader;
    private ConsoleWriter consoleWriter;
    private Index index;
    private TfCalcer tfCalcer;

    private  AtomicInteger isIndexed=new AtomicInteger();

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
                case("3"):
                    test();
                case ("0"):
                    view.buy();
                    index.stop();
                    return;
            }
        }

    }

    public void test() {

        ExecutorService exutor= Executors.newCachedThreadPool();
        List<String> s =new ArrayList<String>(){
            {
                add("Vanya");
                add("Petya");
            }
        };
        List<Future> serchers=new ArrayList<>();
        for(int i=0;i<500;++i){
            if(i%2==0){
                if(isIndexed.get()!=0){
                    consoleWriter.write("I can't index now!");
                    break;
                }
                isIndexed.addAndGet(-1);
                index.indexedDirectory();
                isIndexed.addAndGet(1);
            }else{}

        }
    }
}