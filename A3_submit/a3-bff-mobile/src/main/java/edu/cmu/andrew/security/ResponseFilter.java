package edu.cmu.andrew.security;

import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.io.CharStreams;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;


import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.POST_TYPE;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * <h1>Response Filter</h1>
 * 
 * This class is an interceptor that executes at each response from a previous client request, it intents to transform the message given to the web client.
 * 
 * @author Juan Carlos Villegas Montiel
 * @version 1.0
 * @since 2024-03-29
 */
@Component
public class ResponseFilter extends ZuulFilter {
	
	private final String PATH_BOOKS_ISBN = "/books/isbn/";
	private final String PATH_BOOKS = "/books/";
	private final String PATH_CUSTOMERS_ID = "/customers";
	private final String PATH_CUSTOMERS_USER_ID = "/customers?userId";
	
	
    /**
     * Method that sets the type of interceptor of this class
     * In this case POST_TYPE configure this interceptor to execute when the http response is send back to the user.
     * @return String the type of Interceptor
     */
    @Override
    public String filterType() {
        return POST_TYPE;
    }

    /**
     * Method that sets the order in which this interceptor will be executed in the case there are more than one
     * @return the int number representing the order of execution
     */
    @Override
    public int filterOrder() {
        return 0;
    }

    /**
     * Method that active the filter to be executed in the application
     * @return true if the filter is activated, false otherwise
     */
    @Override
    public boolean shouldFilter() {
        return true;
    }

    /**
     * This method executes when the http request is about to provide the response to the web client. 
     * It will intercept the response message and transform the message as per the business rules defined in the requirements.
     * @throws ZuulException in the case of any error at processing the http response object.
     */
    @Override
    public Object run() throws ZuulException {
    	String responseData = null;
    	String responseDataFiltered = null;    	
    	
        RequestContext context = RequestContext.getCurrentContext();
        try (final InputStream responseDataStream = context.getResponseDataStream()) {
        	//getting the request and response objects
        	HttpServletRequest request=RequestContext.getCurrentContext().getRequest(); 
        	HttpServletResponse response=RequestContext.getCurrentContext().getResponse();         	
        	responseData = CharStreams.toString(new InputStreamReader(responseDataStream, "UTF-8"));        	
        	responseDataFiltered = responseData;//clone reference of stream reader to avoid exception on closing reader
        	
        	//If the response comes from a request to the books service then validate the method response and apply business rules
        	if((request.getRequestURI().matches(PATH_BOOKS_ISBN+"(.*)")||request.getRequestURI().matches(PATH_BOOKS+"(.*)")) 
        			&& request.getMethod().equals(HttpMethod.GET.name())
        			&& response.getStatus() == HttpServletResponse.SC_OK) {
        		ObjectMapper objectMapper = new ObjectMapper();
        		JsonNode jsonNode = objectMapper.readTree(responseDataFiltered);
        		ObjectNode object = (ObjectNode) jsonNode;

        		if(object.get("genre").asText().equals("non-fiction")) {
        			object.put("genre", 3);
        		}        		
        		String updatedJsonResponse = objectMapper.writeValueAsString(object);         		
         		context.setResponseBody(updatedJsonResponse);
        		
        	}else 
        	//if the response comes from a request to the customers service then validate the method response and apply business rules	
        	if((request.getRequestURI().matches(PATH_CUSTOMERS_ID+"(.*)")||request.getRequestURI().matches(PATH_CUSTOMERS_USER_ID+"(.*)")) 
        			&& request.getMethod().equals(HttpMethod.GET.name())
        			&& response.getStatus() == HttpServletResponse.SC_OK) {

        		ObjectMapper objectMapper = new ObjectMapper();
        		JsonNode jsonNode = objectMapper.readTree(responseDataFiltered);
        		ObjectNode object = (ObjectNode) jsonNode;
        	    object.remove("address");
        	    object.remove("address2");
        	    object.remove("city");
        	    object.remove("state");
        	    object.remove("zipcode");
        	    String updatedJsonResponse = objectMapper.writeValueAsString(object);
        	    System.out.println("data filtered:"+ updatedJsonResponse);
        		context.setResponseBody(updatedJsonResponse);
        		
        	} 
        	//If the response does not match with any of the cases above do nothing and send the message response without transform
        	else {            
	            context.setResponseBody(responseDataFiltered);
        	}
        }
        catch (Exception e) {
            throw new ZuulException(e, INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }

        return null;
    }
}