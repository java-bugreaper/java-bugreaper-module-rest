package io.bugreaper.modules.mocks.exceptions;

public class MockEnchantedException extends RuntimeException {

    public MockEnchantedException(Throwable cause) {
        super(cause);
    }

    public MockEnchantedException(String message) {
        super(message);
    }

    public MockEnchantedException(String message, Throwable cause) {
        super(message, cause);
    }
}
