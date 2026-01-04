package net.bugreaper.modules.api.assertable;


public interface ResponseGrab {

    /**
     * Grab response header data
     *
     * @param header header name
     * @return String with response header value
     */
    String grabResponseHeader(String header);

    /**
     * Grab response body
     *
     * @return String with response body
     */
    String grabResponseBody();


    /**
     * Grab response body field data (converted to String)
     *
     * @param path path to field (data.user.id)
     * @return String with response body field data
     */
    Object grabStringFromResponseByPath(String path);

    /**
     * Grab response body field data
     *
     * @param path path to field (data.user.id)
     * @return Object with response body field data
     */
    Object grabDataFromResponseByPath(String path);


}
