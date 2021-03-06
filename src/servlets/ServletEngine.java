package servlets;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.awt.image.BufferedImage;

import Server.Server;
import data.Constants;
import data.Result;


/**
 * Servlet implementation class ServletEngine
 */
@WebServlet("/ServletEngine")
public class ServletEngine extends HttpServlet {
	private static final long serialVersionUID = 1L;

       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletEngine() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				
		/* Check if request is for keyword */
		// get keyword parameter from HttpServletRequest
		String keyword = request.getParameter(Constants.KEYWORD_PARAMETER);
		String shape = request.getParameter(Constants.SHAPE_PARAMETER);
		String widthString = request.getParameter(Constants.WIDTH_PARAMETER);
		int width = Integer.parseInt(widthString);
		String heightString =request.getParameter(Constants.HEIGHT_PARAMETER);
		int height = Integer.parseInt(heightString);
		String filterString = request.getParameter(Constants.FILTER_PARAMETER);
		String rotationString = request.getParameter(Constants.ROTATION_PARAMETER);
		boolean rotation = (rotationString.equals("On"));
		String borderString = request.getParameter(Constants.BORDER_PARAMETER);
		boolean border = (borderString.equals("On"));
		
		if (keyword != null && shape != null) {
			Result result = sendKeywordToServer(keyword, shape, width, height, filterString, rotation, border);
			BufferedImage bImage = result.getCollageImage();

			if (bImage != null) {
				String base64 = Constants.getImage(bImage);
				String title = result.getKeyword();
				String json = "";
				
				Map<String, String> values = new HashMap<String, String>();
				values.put("src", base64);
				values.put("title", title);
				values.put("success", "success");
				
				ObjectMapper mapper = new ObjectMapper();
				try {
					json = mapper.writeValueAsString(values);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}

				if (response.getWriter() == null) {return;}
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write(json);
				setSessionAttributes(request, result);
				Timestamp finishTime = new Timestamp(System.currentTimeMillis());
				System.out.println("Request Finished " + finishTime);
				return;
			}
			// setSessionAttributes(request, result);
			Timestamp finishTime = new Timestamp(System.currentTimeMillis());
			System.out.println("Request Finished " + finishTime);
		}
		String json = "";
		Map<String, String> values = new HashMap<String, String>();
		values.put("success", "fail");
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			json = mapper.writeValueAsString(values);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		if (response.getWriter() == null) {return;}
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}

	// Sends keyword to Server and returns the Result received from Server
	private Result sendKeywordToServer(String keyword, String shape, int width, int height, String filterString,
			boolean rotation, boolean border) {
		Result result = Server.getInstance().getResultForKeyword(keyword, shape, width, height, filterString, rotation, border);
		return result;
	}
	
	// Sets result as currently displayed result
	private void setSessionAttributes(HttpServletRequest request, Result result) {
		HttpSession session = request.getSession();		
		Result currentResult  = (Result) session.getAttribute(Constants.SESSION_CURRENT_RESULT);
		
		// if currently displaying collage, save collage first before
		//   setting new one
		if (currentResult != null && currentResult.isSuccess()) {
			saveCurrentCollage(session);
		}
		
		// sets new collage as current collage
		session.setAttribute(Constants.SESSION_CURRENT_RESULT, result);
	}
	
	// if collage is currently being displayed, save collage to savedCollages
	private void saveCurrentCollage(HttpSession session) {
		@SuppressWarnings("unchecked")
		List<Result> savedCollages = (List<Result>) session.getAttribute(Constants.SESSION_SAVED_COLLAGES);
		
		// if no collages have been saved, initialize list of Results
		if (savedCollages == null) {
			savedCollages = new ArrayList<Result>();
		}
		Result currentResult = (Result) session.getAttribute(Constants.SESSION_CURRENT_RESULT);
		if (currentResult.isSuccess()) {
			savedCollages.add(currentResult); // add current collage to saved collages
			
			// save savedCollages as SESSION_SAVED_COLLAGES
			session.setAttribute(Constants.SESSION_SAVED_COLLAGES, savedCollages);
		}
	}
}