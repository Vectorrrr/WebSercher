package view.reader;

import java.io.Closeable;

/**
 * Created by igladush on 01.03.16.
 */
public interface Reader<T>  extends AutoCloseable,Closeable {
    T read();
}