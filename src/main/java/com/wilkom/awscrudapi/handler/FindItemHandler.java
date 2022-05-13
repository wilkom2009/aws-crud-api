package com.wilkom.awscrudapi.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wilkom.awscrudapi.dao.CrudDAO;
import com.wilkom.awscrudapi.model.Item;
import com.wilkom.awscrudapi.model.response.ErrorMessage;
import com.wilkom.awscrudapi.model.response.GatewayResponse;
import com.amazonaws.services.lambda.runtime.Context;

public class FindItemHandler implements RequestStreamHandler {
    CrudDAO dao = new CrudDAO();

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        LambdaLogger logger = context.getLogger();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "text/html");
        JsonNode event = null;
        try {
            event = mapper.readTree(input);
            logger.log("[Event : ]" + event);
        } catch (JsonMappingException e) {
            mapper.writeValue(output,
                    new GatewayResponse<>(
                            mapper.writeValueAsString(
                                    new ErrorMessage("Invalid JSON in body: "
                                            + e.getMessage(), 400)),
                            headers, 400));
            return;
        }
        final JsonNode queryParameterMap = event.findValue("pathParameters");
        if (queryParameterMap == null) {
            mapper.writeValue(output,
                    new GatewayResponse<>(
                            mapper.writeValueAsString(
                                    new ErrorMessage("Empty parameters ", 400)),
                            headers, 400));
            return;
        }
        final String requestId = Optional.ofNullable(queryParameterMap)
                .map(mapNode -> mapNode.get("id").asText())
                .orElse(null);
        logger.log("[queryParameterMap : ]" + queryParameterMap);
        logger.log("[ID : ]" + requestId);
        Item item = dao.getItemById(requestId);

        mapper.writeValue(output, new GatewayResponse<>(
                mapper.writeValueAsString(item),
                headers, 200));

        return;
    }

}
