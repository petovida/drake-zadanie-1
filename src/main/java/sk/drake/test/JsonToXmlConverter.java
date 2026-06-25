package sk.drake.test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
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
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JsonToXmlConverter {

  private Path inputDir;
  private Path outputDir;
  private LocalDate dateFrom;
  private LocalDate dateTo;

  private final Scanner scanner = new Scanner(System.in);
  private final ObjectMapper mapper = new ObjectMapper();
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
          System.out.print(
              "Datum konca musi byt neskorsi alebo rovnaky ako datum zaciatku! Skuste znovu: ");
        }
      } catch (DateTimeParseException e) {
        System.out.print("Nespravne zadany datum! Skuste znovu: ");
      }
    } while (!inputValid);
    this.dateTo = date;
    System.out.println("Zaznamy sa budu ukladat DO datumu: " + this.dateTo.toString());
    System.out.println("----------------------\n");
  }

  private Set<Path> findJsonFilesInFolder(Path inputDir) {
    try (Stream<Path> stream = Files.list(inputDir)) {
      return stream.filter(file -> !Files.isDirectory(file)).collect(Collectors.toSet());
    } catch (IOException e) {
      System.out.println("Nepodarilo sa nacitat subory z priecinka.");
    }
    return Set.of();
  }

  private Message processSingleRecord(JsonNode record) throws InvalidRowException {
    Message message = new Message();
    JsonNode node;

    // ID
    node = record.get("id");
    if (node == null || node.isNull() || node.asText().isEmpty()) {
      throw new InvalidRowException("Pole \"ID\" je prazdne alebo neexistuje.");
    } else {
      message.setId(node.asText());
    }

    // Type
    node = record.get("type");
    if (node == null || node.isNull() || node.asText().isEmpty()) {
      message.setType("UNKNOWN");
    } else {
      message.setType(node.asText());
    }

    // Created
    node = record.get("created");
    if (node == null || node.isNull() || node.asText().isEmpty()) {
      throw new InvalidRowException("Pole \"Created\" je prazdne alebo neexistuje.");
    } else {
      try {
        LocalDate parsedDate = LocalDate.parse(node.asText(), formatter);
        if (parsedDate.isBefore(this.dateFrom) || parsedDate.isAfter(this.dateTo)) {
          throw new InvalidRowException(
              "Datum zadany v poli \"Created\" nie je v pozadovanom rozsahu.");
        }
        message.setCreated(parsedDate);
      } catch (DateTimeParseException e) {
        throw new InvalidRowException("Pole \"Created\" nema datum v spravnom formate.");
      }
    }

    // Amount
    node = record.get("amount");
    BigDecimal amount;
    if (node == null || node.isNull()) {
      throw new InvalidRowException("Pole \"Amount\" je prazdne alebo neexistuje.");
    } else if (!node.isNumber()) {
      throw new InvalidRowException("V poli \"Amount\" nie je platne cislo.");
    } else {
      amount = node.decimalValue();
      message.setAmount(amount);
    }

    // Vat
    node = record.get("vat");
    int vat;
    if (node == null || node.isNull()) {
      throw new InvalidRowException("Pole \"Vat\" je prazdne alebo neexistuje.");
    } else if (!node.isNumber()) {
      throw new InvalidRowException("V poli \"Vat\" nie je platne cislo.");
    } else {
      vat = node.asInt();
      message.setVat(vat);
    }

    // compute amount with vat
    message.setAmountWithVat(amount.multiply(BigDecimal.valueOf(1 + vat / 100.0)));

    return message;
  }

  private void convertSingleJson(Path filePath, Path outputDir) throws IOException, JAXBException {
    System.out.println("Spracovavam subor " + filePath.toString());

    // read Json rows to Message objects (+ validation)
    JsonNode jsonNode;
    try {
      jsonNode = mapper.readTree(filePath.toFile());
      if (!jsonNode.isArray()) {
        System.out.println("JSON subor neobsahuje pole. Subor sa nespracuje.");
        return;
      }
    } catch (JsonParseException e) {
      System.out.println("Syntakticka chyba v JSON subore. Subor sa nespracuje.");
      return;
    }
    Messages validMessages = new Messages();
    Message message;
    int row = 0;
    for (JsonNode record : jsonNode) {
      row++;
      try {
        message = processSingleRecord(record);
        System.out.println(
            "Zaznam "
                + row
                + ": "
                + message.getId()
                + ", "
                + message.getType()
                + ", "
                + message.getCreated()
                + ", "
                + message.getAmount()
                + ", "
                + message.getVat()
                + ", "
                + message.getAmountWithVat());
        validMessages.getMessages().add(message);
      } catch (InvalidRowException e) {
        System.out.println("Zaznam " + row + ": " + e.getMessage());
      }
    }
    if (validMessages.getMessages().isEmpty()) {
      System.out.println(
          "V JSON subore sa nenachadzal ziadny platny zaznam. XML subor nebol vytvoreny.\n");
    } else {
      // write Message object to XML file
      File outputFile = outputDir.resolve(filePath.getFileName() + ".xml").toFile();
      JAXBContext context = JAXBContext.newInstance(Messages.class);
      Marshaller marshaller = context.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      marshaller.marshal(validMessages, outputFile);
      System.out.println(
          "JSON subor bol spracovany. Pocet platnych zaznamov ulozenych do XML suboru: "
              + validMessages.getMessages().size()
              + "\n");
    }
  }

  public void runApp() {
    getUserInput();
    Set<Path> jsonFilePaths = findJsonFilesInFolder(inputDir);
    for (Path filePath : jsonFilePaths) {
      try {
        convertSingleJson(filePath, outputDir);
      } catch (IOException e) {
        System.out.println("Nastala ina chyba pri spracovani JSON suboru:\n" + e + "\n");
      } catch (JAXBException e) {
        System.out.println("Nastala ina chyba pri ukladani XML suboru:\n" + e + "\n");
      }
    }
    System.out.println("Vsetky JSON subory boli spracovane.");
  }
}
