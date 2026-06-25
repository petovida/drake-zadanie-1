package sk.drake.test.exception;

public class InvalidRowException extends Exception {
  public InvalidRowException(String msg) {
    super(msg + " Tento riadok nebude ulozeny do vystupu.");
  }
}
