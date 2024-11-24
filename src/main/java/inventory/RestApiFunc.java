package inventory;

import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

import java.util.logging.Logger;
import java.util.List;
import java.util.Optional;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RestApiFunc {
    @FunctionName("RestApiFunction")
    public HttpResponseMessage getHello(
        @HttpTrigger(name = "req",
                     methods = {HttpMethod.GET},
                     authLevel = AuthorizationLevel.ANONYMOUS,
                     route = "get") HttpRequestMessage<Optional<String>> request,
        final ExecutionContext context) {


        Logger logger = context.getLogger();
        logger.info("HTTP trigger function processed a request.");


        return request.createResponseBuilder(HttpStatus.OK)
                      .header("Content-Type", "application/json")
                      .body("Hello, Azure Functions!")
                      .build();
    }

    @FunctionName("OptionsHandler")
    public HttpResponseMessage options(
    @HttpTrigger(name = "req",
                 methods = {HttpMethod.OPTIONS},
                 authLevel = AuthorizationLevel.ANONYMOUS,
                 route = "{*any}") HttpRequestMessage<Optional<String>> request,
    final ExecutionContext context) {

    return request.createResponseBuilder(HttpStatus.NO_CONTENT)
                  .header("Access-Control-Allow-Origin", "*")
          
          
               .header("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
                  .header("Access-Control-Allow-Headers", "Content-Type")
                  .build();
    }

    @FunctionName("GetStatesFunction")
    public HttpResponseMessage getStates(
        @HttpTrigger(name = "req",
                     methods = {HttpMethod.GET},
                     authLevel = AuthorizationLevel.ANONYMOUS,
                     route = "getStates") HttpRequestMessage<Optional<String>> request,
        final ExecutionContext context) {

        Logger logger = context.getLogger();
        logger.info("HTTP trigger function processed a request to get states.");

        try {
            List<String> states = DatabaseOperations.getDistinctStates(logger);
            ObjectMapper mapper = new ObjectMapper();
            String jsonResponse = mapper.writeValueAsString(states);

            return request.createResponseBuilder(HttpStatus.OK)
                          .header("Content-Type", "application/json")
                          .body(jsonResponse)
                          .build();
        } catch (Exception e) {
            logger.severe("Error fetching states: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                          .body("Error fetching states")
                          .build();
        }
    }
    
}
