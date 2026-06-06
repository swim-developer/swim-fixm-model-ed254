package aero.fixm.validation;

public class Ed254XsdValidator {

    private final Ed254UnmarshallerPool pool = new Ed254UnmarshallerPool();

    public void validateAndUnmarshal(String xml) throws ValidationException {
        try {
            pool.unmarshalAndValidate(xml);
        } catch (Ed254UnmarshallerPool.Ed254UnmarshalException e) {
            throw new ValidationException(e.getMessage(), e);
        }
    }

    public static class ValidationException extends Exception {
        public ValidationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
