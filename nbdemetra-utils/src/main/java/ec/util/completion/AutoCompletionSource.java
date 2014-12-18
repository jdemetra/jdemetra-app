package ec.util.completion;

import java.util.List;

public interface AutoCompletionSource {

    public enum Behavior {

        NONE,
        SYNC,
        ASYNC
    }

    Behavior getBehavior(String term);

    String toString(Object value);

    List<?> getValues(String term) throws Exception;
}