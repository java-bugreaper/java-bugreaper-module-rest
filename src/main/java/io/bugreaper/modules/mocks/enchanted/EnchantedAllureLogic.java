/*
 *  Copyright 2025 Oleksii Betin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.bugreaper.modules.mocks.enchanted;

import io.qameta.allure.Allure;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;

import static io.bugreaper.core.mappers.JsonMappers.jsonObjectFromString;
import static io.bugreaper.modules.mocks.mappers.MapDifferenceReporter.jsonDifLogic;


public class EnchantedAllureLogic extends EnchantedSetup {

    /**
     * @param assertResult     String[][] = [][№, passed/failed, difference]
     * @param actualBodiesList List(String) list with actual bodies
     */
    public static void mockSchemaMatcherAllure(
            String[][] assertResult,
            List<String> actualBodiesList) {

        int i = 0;
        for (String[] everyAssert : assertResult) {
            String allureName = MessageFormat.format(
                    "jsonSchema validate №{0} {1}:",
                    everyAssert[0], //№
                    everyAssert[1]); //result


            String allureContent = MessageFormat.format(
                    "\nActual body\n{0}\n========================\n{1}",
                    actualBodiesList.get(i),
                    everyAssert[2]); //difference

            logger_mer.debug("\n{}{}", allureName, allureContent);

            Allure.addAttachment(allureName,
                    "application/json",
                    allureContent);

            i = i + 1;
        }

    }

    public static void mockJsonMatcherAllure(
            String[][] assertResult,
            String assertKey,
            String expectedObject,
            Boolean isStrict,
            String assertion) {

        int i = 0;
        for (String[] everyAssert : assertResult) {

            String allureName = MessageFormat.format(
                    "{0} {1} №{2} {3}:",
                    assertion,
                    assertKey,
                    everyAssert[0],
                    everyAssert[1]);

            String difference = differenceContent(assertKey, expectedObject, everyAssert[2], isStrict);

            String allureContent = MessageFormat.format(
                    "\nActual {0}\n{1}\n========================\n{2}",
                    assertKey,
                    everyAssert[2],
                    difference);

            logger_mer.debug("\n{}{}", allureName, allureContent);

            Allure.addAttachment(allureName,
                    "application/json",
                    allureContent);

            i = i + 1;
        }
    }

    public static String differenceContent(String assertKey,
                                           String expectedObject,
                                           String actualContent,
                                           Boolean isStrict) {
        JSONObject jsonObject;

        if (Objects.equals(assertKey, BODY_KEY)) {
            jsonObject = jsonObjectFromString(actualContent);

            if (jsonObject.has(BODY_WRONG_KEY)) {
                return (MessageFormat.format("\nActual {0} replaced & skipped difference check because it`s not JSON\n", assertKey));
            } else if (jsonObject.has(BODY_ABSENT_KEY)) {
                return (MessageFormat.format("\nActual {0} is absent\n", assertKey));
            } else {
                return jsonDifLogic(expectedObject, actualContent, isStrict);
            }

        }

        return jsonDifLogic(expectedObject, actualContent, isStrict);
    }

}
