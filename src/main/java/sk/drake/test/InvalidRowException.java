package sk.drake.test;

public class InvalidRowException extends Exception {
  public InvalidRowException(String msg) {
    super(msg + " Tento riadok nebude ulozeny do vystupu.");
  }
}
