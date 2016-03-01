package view;

import view.writer.ConsoleWriter;

/**
 * Created by igladush on 01.03.16.
 */
public class View {
    private ConsoleWriter consoleWriter;

    public View() {
        consoleWriter = new ConsoleWriter();
    }

    public void showMainMenu() {
        consoleWriter.write("If you want index some directory type 1");
        consoleWriter.write("If you want index some directory type 2");
        consoleWriter.write("If you want calc tf-idf type 3");
        consoleWriter.write("If you want calc tf-idf from default directory type 4");
        consoleWriter.write("If you want exit type 0");

    }

    public void buy() {
        consoleWriter.write("Good luck, bro!)");
    }
}