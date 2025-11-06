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
     * Enabling or disabling enchanted mockVerify report
     *
     * @param enchantedReport true=on/false=off
     * @return  this instance {@link MocksApi}
     */
    MocksApi withEnchantedReport(boolean enchantedReport);

}
