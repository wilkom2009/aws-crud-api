package com.wilkom.awscrudapi.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wilkom.awscrudapi.dao.CrudDAO;
import com.wilkom.awscrudapi.model.request.CreateItemRequest;
import com.wilkom.awscrudapi.model.response.ErrorMessage;
import com.wilkom.awscrudapi.model.response.GatewayResponse;

public class CreateItemHandler implements RequestStreamHandler {
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
        JsonNode eventBody = event.findValue("body");
        final CreateItemRequest request;
        try {
            request = mapper.treeToValue(
                    mapper.readTree(eventBody.asText()),
                    CreateItemRequest.class);
            logger.log("[Request : ]" + request);

        } catch (JsonParseException | JsonMappingException e) {
            mapper.writeValue(output,
                    new GatewayResponse<>(
                            mapper.writeValueAsString(
                                    new ErrorMessage("Invalid JSON in body: "
                                            + e.getMessage(), 400)),
                            headers, 400));
            return;
        }

        String id = dao.saveItem(request.getItem());

        mapper.writeValue(output,
                new GatewayResponse<>("[Success] : " + id,
                        headers, 200));

        return;
    }

}
