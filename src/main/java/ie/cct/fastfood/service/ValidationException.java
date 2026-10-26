package ie.cct.fastfood.service;

/** Thrown when something the user typed is not valid. */
public class ValidationException extends Exception {

    public ValidationException(String message) {
        super(message);
    }
}