package ai.yda.framework.rag.channels;

import ai.yda.framework.rag.shared.model.Request;
import ai.yda.framework.rag.shared.model.Response;

/**
 * <p> The {@code CommunicationChannel} interface provides an abstraction for a communication
 * channel that accepts requests from various external sources. </p>
 *
 * <p> This interface defines a method for sending requests to a core processing unit which then
 * returns processed responses. Using this interface allows for uniform handling of communications
 * across different platforms. </p>
 */
public interface CommunicationChannel {

    /**
     * Sends a communication request to the core processing system and retrieves a response.
     *
     * @param request The communication request containing the necessary information
     *                to be processed, such as message content and sender details.
     * @return a {@link Response} object containing the response
     *         from the core processing system. The response includes the outcome of
     *         the processing.
     */
    Response sendRequest(Request request);
}
