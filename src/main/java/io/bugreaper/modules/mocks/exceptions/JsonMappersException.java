package io.bugreaper.modules.mocks.exceptions;

public class JsonMappersException extends RuntimeException {

    public JsonMappersException(Throwable cause) {
        super(cause);
    }

    public JsonMappersException(String message, Throwable cause) {
        super(message, cause);
    }

}
