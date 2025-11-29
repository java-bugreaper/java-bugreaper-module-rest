package io.bugreaper.modules.mocks.interfaces;


import io.bugreaper.modules.mocks.MocksApi;

public interface MocksConfig {

    /**
     * Configure await in asserts with await
     *
     * @param awaitMs await ms
     * @return  this instance {@link MocksApi}
     */
    MocksApi withAwaitMs(int awaitMs);

    /**
     * Enables or disables logging manually (debug log level will print logs anyway!)
     *
     * @param enable true=request/response logging (Allure on always!)
     * @return this instance {@link MocksApi}
     */
    MocksApi withLogging(boolean enable);

    /**
     * Enabling or disabling enchanted mockVerify report
     *
     * @param enchantedReport true=on/false=off
     * @return  this instance {@link MocksApi}
     */
    MocksApi withEnchantedReport(boolean enchantedReport);

}
