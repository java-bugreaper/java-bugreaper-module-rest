package net.bugreaper.modules.mocks.exceptions;

public class MockEnchantedException extends RuntimeException {

    public MockEnchantedException(String message) {
        super(message);
    }

    public MockEnchantedException(String message, Throwable cause) {
        super(message, cause);
    }
}
