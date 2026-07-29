package net.bugreaper.modules.mocks.interfaces;


import net.bugreaper.modules.mocks.MocksApi;

/**
 * Interface that defines helper configuration methods for helper operations.
 * Validates that all required methods are implemented.
 */
public interface MocksConfig {

    /**
     * Configures the global await timeout for assertions and operations that use await.
     *
     * @param awaitMs await timeout in milliseconds
     * @return this
     * @throws IllegalArgumentException if the provided timeout is invalid or less than 200 milliseconds
     */
    MocksApi setAwaitMs(int awaitMs);

    /**
     * Configure await for next assert with await (than await rollback to global).
     * <p>global {@link #setAwaitMs(int)} will be ignored</p>
     *
     * @param awaitMs ms await
     * @return this instance for method chaining
     * @throws IllegalArgumentException if the setup is invalid
     */
    MocksApi withAwaitMs(int awaitMs);

    /**
     * Enables or disables logging manually (debug log level will print logs anyway!).
     *
     * @param enable true=request/response logging (Allure on always!)
     * @return this instance for method chaining
     */
    MocksApi setLogging(boolean enable);

    /**
     * Enabling or disabling enchanted mockVerify report.
     *
     * <p>Same behavior as {@link net.bugreaper.modules.mocks.enchanted.MockEnchantedDiffer}.
     *
     * @param enchantedReport true=on/false=off
     * @return this instance for method chaining
     */
    MocksApi setEnchantedReport(boolean enchantedReport);

    /**
     * Returns and logs (at INFO level) a human-readable summary of all resolved
     * configuration values.
     * <p>
     * The summary includes values loaded from the YAML configuration file as well as
     * any fields overridden programmatically after construction. Optional fields that
     * were not present in the configuration and resolved via default values may also
     * be included.
     *
     * @return String with summary
     */
    String getConfigSummary();

}
