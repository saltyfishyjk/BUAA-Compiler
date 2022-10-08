package middle.error;

import java.util.TreeSet;

public class ErrorTable {
    private ErrorTable() {}

    private static TreeSet<Error> errors = new TreeSet<>();

    public static void addError(Error error) {
        errors.add(error);
    }

    public static TreeSet<Error> getErrors() {
        return errors;
    }
}
