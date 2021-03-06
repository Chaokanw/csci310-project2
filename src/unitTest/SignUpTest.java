package unitTest;

import static org.junit.Assert.assertTrue;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

import servlets.SignUpServlet;

import java.io.PrintWriter;
import java.io.StringWriter;

public class SignUpTest extends Mockito {
	
	private SignUpServlet servlet;
	private static final String NEW_USERNAME = "new_user";
	private static final String NEW_PASSWORD = "new_user";
	private static final String EXISTING_USERNAME = "TEST";
	private static final String EXISTING_PASSWORD = "TEST";
	private static final String EMPTY_STRING = "";
    
    @Test
    // User should be able to sign up given that the user provides a username that hasn't already exists
    // and an acceptable password
    public void testSignUpSuccessfully() throws Exception {
    	HttpServletRequest request = mock(HttpServletRequest.class);
    	HttpServletResponse response = mock(HttpServletResponse.class);
    	
    	when (request.getParameter("username")).thenReturn(NEW_USERNAME);
    	when (request.getParameter("password")).thenReturn(NEW_PASSWORD);
    	when (request.getSession()).thenReturn(mock(HttpSession.class));
    	
    	servlet = new SignUpServlet();
    	servlet.doPost(request, response);
    	
    	assertTrue(response != null);
    	String result = servlet.getResult();
    	assertTrue(result.equals("success"));
    	
    	servlet.deleteAfterTest(); // delete newly registered test user
    }
    
    @Test
    // User should not be able to sign up if the user provides a username that already exists
    public void testSignUpFail() throws Exception {
    	HttpServletRequest request = mock(HttpServletRequest.class);
    	HttpServletResponse response = mock(HttpServletResponse.class);
    	
    	when (request.getParameter("username")).thenReturn(EXISTING_USERNAME);
    	when (request.getParameter("password")).thenReturn(EXISTING_PASSWORD);
    	when (request.getSession()).thenReturn(mock(HttpSession.class));
    	
    	servlet = new SignUpServlet();
    	servlet.doPost(request, response);
    	
    	assertTrue(response != null);
    	String result = servlet.getResult();
    	assertTrue(result.equals("fail"));
    }
    
    @Test
    // User should not be able to sign up if at least one field is missing
    public void testEmptyParameters() throws Exception {
    	HttpServletRequest request = mock(HttpServletRequest.class);
    	HttpServletResponse response = mock(HttpServletResponse.class);
    	
    	when (request.getParameter("username")).thenReturn(EMPTY_STRING);
    	when (request.getParameter("password")).thenReturn(EMPTY_STRING);
    	
    	servlet = new SignUpServlet();
    	servlet.doPost(request, response);
    	
    	assertTrue(response != null);
    	String result = servlet.getResult();
    	assertTrue(result.equals("fail"));
    }
    
    @Test
    // User should not be able to sign up if at least one field is missing
    public void testEmptyUsername() throws Exception {
    	HttpServletRequest request = mock(HttpServletRequest.class);
    	HttpServletResponse response = mock(HttpServletResponse.class);
    	
    	when (request.getParameter("username")).thenReturn(EMPTY_STRING);
    	when (request.getParameter("password")).thenReturn(NEW_PASSWORD);
    	
    	servlet = new SignUpServlet();
    	servlet.doPost(request, response);
    	
    	assertTrue(response != null);
    	String result = servlet.getResult();
    	assertTrue(result.equals("fail"));
    }
    
    @Test
    // User should not be able to sign up if at least one field is missing    
    public void testEmptyPassword() throws Exception {
    	HttpServletRequest request = mock(HttpServletRequest.class);
    	HttpServletResponse response = mock(HttpServletResponse.class);
    	
    	when (request.getParameter("username")).thenReturn(NEW_USERNAME);
    	when (request.getParameter("password")).thenReturn(EMPTY_STRING);
    	
    	servlet = new SignUpServlet();
    	servlet.doPost(request, response);
    	
    	assertTrue(response != null);
    	String result = servlet.getResult();
    	assertTrue(result.equals("fail"));
    }
}