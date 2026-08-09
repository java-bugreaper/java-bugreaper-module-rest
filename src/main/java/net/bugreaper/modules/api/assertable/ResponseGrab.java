package net.bugreaper.modules.api.assertable;


public interface ResponseGrab {

    /**
     * Returns the response header value.
     *
     * @param header header name
     * @return response header value as a String
     */
    String grabResponseHeader(String header);

    /**
     * Returns the full response body.
     *
     * @return response body as a String
     */
    String grabResponseBody();


    /**
     * Returns the response body field value converted to String.
     *
     * @param path path to field (example: {@code data.user.id})
     * @return String representation of the response body field value
     */
    String grabStringFromResponseByPath(String path);

    /**
     * Returns the response body field value as the specified type.
     *
     * @param path path to field (example: {@code data.user.id})
     * @return response body field value as an Object
     */
    Object grabDataFromResponseByPath(String path);


}
