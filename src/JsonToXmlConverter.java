import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class JsonToXmlConverter {

  private Path inputDir;
  private Path outputDir;
  private LocalDate dateFrom;
  private LocalDate dateTo;

  private final Scanner scanner = new Scanner(System.in);

  private void getUserInput() {
    System.out.println("Zadajte vstupne udaje:");
    System.out.println("----------------------\n");

    // inputDir

    System.out.print("Zadajte priecinok, v ktorom sa nachadzaju JSON subory: ");
    Path path = null;
    boolean inputValid = false;
    do {
      try {
        path = Paths.get(scanner.nextLine());
        if (!Files.exists(path) || !Files.isDirectory(path)) {
          System.out.print("Zadany priecinok neexistuje! Skuste znovu: ");
        } else {
          inputValid = true;
        }
      } catch (InvalidPathException e) {
        System.out.print("Nespravne zadana cesta! Skuste znovu: ");
      }
    } while (!inputValid);
    this.inputDir = path;
    System.out.println("Subory sa budu nacitavat z priecinka: " + this.inputDir.toString());
    System.out.println("----------------------\n");

    // outputDir

    System.out.print("Zadajte priecinok, do ktoreho sa ulozia XML subory: ");
    path = null;
    inputValid = false;
    do {
      try {
        path = Paths.get(scanner.nextLine());
        if (!Files.exists(path)) {
          Files.createDirectories(path);
          System.out.println("Priecinok neexistuje a bude vytvoreny.");
        }
        inputValid = true;
      } catch (IOException e) {
        System.out.print("Nepodarilo sa vytvorit priecinok! Skuste znovu: ");
      } catch (InvalidPathException e) {
        System.out.print("Nespravne zadana cesta! Skuste znovu: ");
      }
    } while (!inputValid);
    this.outputDir = path;
    System.out.println("Subory sa budu ukladat do priecinka: " + this.outputDir.toString());
    System.out.println("----------------------\n");

    // dateFrom

    System.out.print(
        "Zadajte datum, OD ktoreho (vratane) sa budu zaznamy ukladat do XML suborov (v tvare YYYY-MM-DD): ");
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate date = null;
    inputValid = false;
    do {
      try {
        date = LocalDate.parse(scanner.nextLine(), formatter);
        inputValid = true;
      } catch (DateTimeParseException e) {
        System.out.print("Nespravne zadany datum! Skuste znovu: ");
      }
    } while (!inputValid);
    this.dateFrom = date;
    System.out.println("Zaznamy sa budu ukladat OD datumu: " + this.dateFrom.toString());
    System.out.println("----------------------\n");

    // dateTo

    System.out.print(
        "Zadajte datum, DO ktoreho (vratane) sa budu zaznamy ukladat do XML suborov (v tvare YYYY-MM-DD): ");
    date = null;
    inputValid = false;
    do {
      try {
        date = LocalDate.parse(scanner.nextLine(), formatter);
        if (date.isAfter(this.dateFrom) || date.isEqual(this.dateFrom)) {
          inputValid = true;
        } else {
          System.out.print("Datum konca musi byt neskorsi alebo rovnaky ako datum zaciatku! Skuste znovu: ");
        }
      } catch (DateTimeParseException e) {
        System.out.print("Nespravne zadany datum! Skuste znovu: ");
      }
    } while (!inputValid);
    this.dateTo = date;
    System.out.println("Zaznamy sa budu ukladat DO datumu: " + this.dateTo.toString());
    System.out.println("----------------------\n");
  }

  private List<Path> findJsonFilesInFolder(Path inputDir) {
    return new ArrayList<>();
  }

  private void convertSingleJson(Path filePath, Path outputDir) {}

  public void runApp() {
    getUserInput();
    List<Path> jsonFilePaths = findJsonFilesInFolder(inputDir);
    for (Path filePath : jsonFilePaths) {
      convertSingleJson(filePath, outputDir);
    }
    System.out.println("Vsetky subory boli uspesne spracovane.");
  }
}
