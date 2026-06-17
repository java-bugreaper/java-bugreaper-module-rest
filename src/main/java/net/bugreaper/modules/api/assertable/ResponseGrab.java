package net.bugreaper.modules.api.assertable;


public interface ResponseGrab {

    /**
     * Return response header data
     *
     * @param header header name
     * @return String with response header value
     */
    String grabResponseHeader(String header);

    /**
     * Return response body
     *
     * @return String with response body
     */
    String grabResponseBody();


    /**
     * Return response body field data (converted to String)
     *
     * @param path path to field (example: {@code data.user.id})
     * @return String with response body field data
     */
    String grabStringFromResponseByPath(String path);

    /**
     * Return response body field data (any type)
     *
     * @param path path to field (example: {@code data.user.id})
     * @return Object with response body field data
     */
    Object grabDataFromResponseByPath(String path);


}
