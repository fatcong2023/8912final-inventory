package inventory;

import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        logger.info("HTTP trigger function processed a request to get states and cities.");

        try {
            Map<String, List<String>> statesAndCities = DatabaseOperations.getStatesAndCities(logger);
            List<Map<String, Object>> responseList = new ArrayList<>();

            for (Map.Entry<String, List<String>> entry : statesAndCities.entrySet()) {
                Map<String, Object> stateCityMap = new HashMap<>();
                stateCityMap.put("state", entry.getKey());
                stateCityMap.put("city", entry.getValue());
                responseList.add(stateCityMap);
            }

            ObjectMapper mapper = new ObjectMapper();
            String jsonResponse = mapper.writeValueAsString(responseList);

            return request.createResponseBuilder(HttpStatus.OK)
                          .header("Content-Type", "application/json")
                          .body(jsonResponse)
                          .build();
        } catch (Exception e) {
            logger.severe("Error fetching states and cities: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                          .body("Error fetching states and cities")
                          .build();
        }
    }

    @FunctionName("GetDataByStateAndCityFunction")
    public HttpResponseMessage getDataByStateAndCity(
        @HttpTrigger(name = "req",
                     methods = {HttpMethod.POST},
                     authLevel = AuthorizationLevel.ANONYMOUS,
                     route = "getData") HttpRequestMessage<Optional<String>> request,
        final ExecutionContext context) {

        Logger logger = context.getLogger();
        logger.info("HTTP trigger function processed a request to get data by state and city.");

        String requestBody = request.getBody().orElse("");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode;
        String state;
        String city;

        try {
            jsonNode = mapper.readTree(requestBody);
            state = jsonNode.get("state").asText();
            city = jsonNode.get("city").asText();
        } catch (Exception e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                          .body("Invalid request body")
                          .build();
        }

        if (state == null || city == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                          .body("Please provide both state and city in the request body")
                          .build();
        }

        try {
            List<Map<String, Object>> dataList = DatabaseOperations.getDataByStateAndCity(state, city, logger);
            String jsonResponse = mapper.writeValueAsString(dataList);

            return request.createResponseBuilder(HttpStatus.OK)
                          .header("Content-Type", "application/json")
                          .body(jsonResponse)
                          .build();
        } catch (Exception e) {
            logger.severe("Error fetching data by state and city: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                          .body("Error fetching data by state and city")
                          .build();
        }
    }

    @FunctionName("UpdateHundredccCountFunction")
    public HttpResponseMessage updateHundredccCount(
        @HttpTrigger(name = "req",
                     methods = {HttpMethod.PUT},
                     authLevel = AuthorizationLevel.ANONYMOUS,
                     route = "updateCount") HttpRequestMessage<Optional<String>> request,
        final ExecutionContext context) {

        Logger logger = context.getLogger();
        logger.info("HTTP trigger function processed a request to update hundredccCount.");

        String requestBody = request.getBody().orElse("");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode;
        long universalBankNumber;
        String ABO;
        String Rh;
        long hundredccCount;

        try {
            jsonNode = mapper.readTree(requestBody);
            universalBankNumber = jsonNode.get("universalBankNumber").asLong();
            ABO = jsonNode.get("ABO").asText();
            Rh = jsonNode.get("Rh").asText();
            hundredccCount = jsonNode.get("hundredccCount").asLong();
        } catch (Exception e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                          .body("Invalid request body")
                          .build();
        }


        try {
            DatabaseOperations.updateHundredccCount(ABO, Rh, hundredccCount, universalBankNumber, logger);
            return request.createResponseBuilder(HttpStatus.OK)
                          .body("hundredccCount updated successfully")
                          .build();
        } catch (Exception e) {
            logger.severe("Error updating hundredccCount: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                          .body("Error updating hundredccCount")
                          .build();
        }
    }
    
}
