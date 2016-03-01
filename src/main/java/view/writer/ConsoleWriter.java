package view.writer;

import java.util.List;

/**
 * Created by igladush on 01.03.16.
 */
public class ConsoleWriter implements Writer<String> {
    @Override
    public void write(String... s) {
        for(String temp: s){
            System.out.println(temp);
        }
    }

    @Override
    public void write(List<String> s) {
        for(String string: s){
            System.out.println(s);
        }
    }


    @Override
    public void close()  {

    }
}