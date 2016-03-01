package view.reader;

import java.io.IOException;
import java.util.Scanner;

/**
 * Created by igladush on 01.03.16.
 */
public class ConsoleReader implements Reader<String> {
    private Scanner scanner;

    public ConsoleReader() {
        scanner = new Scanner(System.in);
    }

    @Override
    public String read() {
        return scanner.nextLine();
    }

    @Override
    public void close() throws IOException {

    }
}