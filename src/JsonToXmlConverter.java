import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JsonToXmlConverter {

    private Path inputDir;
    private Path outputDir;
    private Date dateFrom;
    private Date dateTo;

    private void getUserInput() {
        System.out.println("Zadajte vstupne udaje:");
    }

    private List<Path> findJsonFilesInFolder(Path inputDir) {
        return new ArrayList<>();
    }

    private void convertSingleJson(Path filePath, Path outputDir) {

    }

    public void runApp() {
        getUserInput();
        List<Path> jsonFilePaths = findJsonFilesInFolder(inputDir);
        for (Path filePath : jsonFilePaths) {
            convertSingleJson(filePath, outputDir);
        }
        System.out.println("Vsetky subory boli uspesne spracovane.");
    }

}
