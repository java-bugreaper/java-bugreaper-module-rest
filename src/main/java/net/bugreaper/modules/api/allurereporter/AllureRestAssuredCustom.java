/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Portions of this file are derived from code originally from:
 * Allure Framework - allure-java (https://github.com/allure-framework/allure-java)
 * Copyright © the Allure contributors, licensed under the Apache License 2.0.
 */
package net.bugreaper.modules.api.allurereporter;

import io.qameta.allure.Allure;
import io.qameta.allure.attachment.AttachmentRenderException;
import io.qameta.allure.attachment.DefaultAttachmentProcessor;
import io.qameta.allure.attachment.FreemarkerAttachmentRenderer;
import io.qameta.allure.attachment.http.HttpRequestAttachment;
import io.qameta.allure.attachment.http.HttpResponseAttachment;
import io.restassured.filter.FilterContext;
import io.restassured.filter.OrderedFilter;
import io.restassured.internal.NameAndValue;
import io.restassured.internal.support.Prettifier;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static io.qameta.allure.attachment.http.HttpRequestAttachment.Builder.create;
import static io.qameta.allure.attachment.http.HttpResponseAttachment.Builder.create;

/**
 * Allure logger filter for Rest-assured with small fixes
 */
public class AllureRestAssuredCustom implements OrderedFilter {

    private static final String HIDDEN_PLACEHOLDER = "[ BLACKLISTED ]";
    private static final Logger logger = LoggerFactory.getLogger("AllureApiAttachments");

    public Response filter(final FilterableRequestSpecification requestSpec,
                           final FilterableResponseSpecification responseSpec,
                           final FilterContext filterContext) {
        final Prettifier prettifier = new Prettifier();
        final String url = requestSpec.getURI();

        final Set<String> hiddenHeaders = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        hiddenHeaders.addAll(Objects.requireNonNull(requestSpec.getConfig().getLogConfig().blacklistedHeaders()));

        final HttpRequestAttachment.Builder requestAttachmentBuilder = create("Request", url)
                .setMethod(requestSpec.getMethod())
                .setHeaders(toMapConverter(requestSpec.getHeaders(), hiddenHeaders))
                .setCookies(toMapConverter(requestSpec.getCookies(), new HashSet<>()));

        if (Objects.nonNull(requestSpec.getBody())) {
            requestAttachmentBuilder.setBody(prettifier.getPrettifiedBodyIfPossible(requestSpec));
        }

        if (Objects.nonNull(requestSpec.getFormParams())) {
            requestAttachmentBuilder.setFormParams(requestSpec.getFormParams());
        }

        final HttpRequestAttachment requestAttachment = requestAttachmentBuilder.build();

        try {
            new DefaultAttachmentProcessor().addAttachment(
                    requestAttachment,
                    new FreemarkerAttachmentRenderer("http-request.ftl")
            );
        } catch (AttachmentRenderException ignore) {
            logger.error("AttachmentRenderException occurred during attaching the request to the report.");
            attachLogs("Request", getRequestLog(requestAttachment));
        }

        final Response response = filterContext.next(requestSpec, responseSpec);

        final String attachmentName = response.getStatusLine();


        final HttpResponseAttachment responseAttachment = create(attachmentName)
                .setResponseCode(response.getStatusCode())
                .setHeaders(toMapConverter(response.getHeaders(), hiddenHeaders))
                .setBody(prettifier.getPrettifiedBodyIfPossible(response, response.getBody()))
                .build();

        try {
            new DefaultAttachmentProcessor().addAttachment(
                    responseAttachment,
                    new FreemarkerAttachmentRenderer("http-response.ftl")
            );
        } catch (AttachmentRenderException e) {
            logger.error("AttachmentRenderException occurred during attaching the Response to the report.");
            attachLogs("Response", getResponseLog(responseAttachment));
        }

        return response;
    }

    private static Map<String, String> toMapConverter(final Iterable<? extends NameAndValue> items,
                                                      final Set<String> toHide) {
        final Map<String, String> result = new HashMap<>();
        items.forEach(h -> result.put(h.getName(), toHide.contains(h.getName()) ? HIDDEN_PLACEHOLDER : h.getValue()));
        return result;
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }


    private static String getRequestLog(HttpRequestAttachment requestAttachment) {
        return """
                Method: %s
                URL: %s
                Body: %s
                """.formatted(
                requestAttachment.getMethod(),
                requestAttachment.getUrl(),
                requestAttachment.getBody()
        );
    }

    private static String getResponseLog(HttpResponseAttachment responseAttachment) {
        return """
                Status Code: %s
                Body: %s
                """.formatted(
                responseAttachment.getResponseCode(),
                responseAttachment.getBody()
        );
    }

    private static void attachLogs(String name, String content) {
        Allure.getLifecycle()
                .addAttachment(
                        name,
                        "text/plain",
                        "txt",
                        content.getBytes(StandardCharsets.UTF_8)
                );
    }

}
