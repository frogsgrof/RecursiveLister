import java.io.File;
import java.util.Arrays;
import java.util.Objects;

public class RecursiveLister {

    static FileFrame frame;

    public static void main(String[] args) {
        frame = new FileFrame();
        frame.setVisible(true);
    }

    public static void listFiles(File dir, int indents) {
        if (dir == null) return; // terminating condition

        // checks if this file is a directory
        if (dir.isDirectory()) {

            // if so, labels it as a directory, adding the appropriate number of indents for formatting,
            // and adds it onto the JTextArea
            frame.updateFileList("\t".repeat(indents).concat("Directory: ").concat
                    (dir.getAbsolutePath()));

            // recurses on any files contained in the directory
            if (dir.list() == null) return;
            Arrays.stream(Objects.requireNonNull(dir.list()))
            .map(File::new).forEach(f -> listFiles(f, indents + 1)); // adds one more indent
            return;
        }

        // if this file is not a directory, the program simply adds its name to the JTextArea, along with the
        // right number of indents
        frame.updateFileList("\t".repeat(indents).concat(dir.getName()));
    }
}