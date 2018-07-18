package bootcamp.KCV2.Server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import bootcamp.KCV2.Question;
import bootcamp.KCV2.QuestionManager;
import bootcamp.KCV2.StudentAnswerSheet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Scanner;

@Controller
public class ServerController {
	
	

    private static final String MULTI_FLAG = "_multi";

	// TODO: tricky part - organize sessioned access per "logged" user
    // TODO: add checks if "exam" is open or not
    // TODO: restrict passing the exam again for the same session

    String userCode = "AAAA"; // TODO: for testing only. remove in production
	
    ArrayList<Question> al = new ArrayList<>();

    /**
     * Method is used to parse and process received answers
     *
     * It does:
     * 1. builds and ArrayList of answers
     * 2. sends answers to the QuestionManager
     * 3. receives score result from QuestionManager
     * 4. displays result to the student
     *
     * */
    @RequestMapping(value = "/sendAnswers", produces = "text/html;chearset=UTF-8", method = RequestMethod.POST)
    @ResponseBody
    // TODO redesign method as POST to hide answer string and allow variable number of parameters
    public String sendAnswers(@RequestParam(value = "userCode", required = true) String userCode,
					    		HttpServletRequest request,
                                HttpServletResponse response) {

    	System.err.println(userCode);
    	ArrayList<String> currentAnswers = new ArrayList<>();
    	
    	// TODO: build HashMap<Integer, Integer> with answers
    	QuestionManager qm = QuestionManager.getInstance();
    	Enumeration<String> parNames = request.getParameterNames();
    	 
    	while(parNames.hasMoreElements()) {
    		String elementName = parNames.nextElement();
    		if(elementName.contains(MULTI_FLAG)){
    			String[] multiArray = request.getParameterValues(elementName);
    			String multiString = "";
    			for (int i = 0; i < multiArray.length; i++) {
					if(i == multiArray.length - 1){
						multiString = multiString + multiArray[i];
					} else {
					multiString = multiString + multiArray[i] +"; ";
					}
				}
    			currentAnswers.add(multiString);
    		} else {
    		String value = request.getParameter(elementName);
    		currentAnswers.add(value);
    		}
    	}
    	
    	
        // TODO: submit answers and get the result
    	qm.submitResults(userCode, currentAnswers);
    	

        // TODO: output result as HTML table

        return "<a href=\"/\"><input type=button class=\"btn btn-primary\" value=\"Results has been submitted\"</a>";
    }
    
    
    
    @RequestMapping(value = "/", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
    @ResponseBody
    // This method should work without declared name parameter, request and
    // response objects,
    // but it shows, how passed request and returned response can be used inside
    // method
    public String homePage(@RequestParam(value = "name", required = false) String name, HttpServletRequest request,
                           HttpServletResponse response) throws IOException {
        StringBuilder sb = new StringBuilder();

       // response.sendRedirect("/test");
        QuestionManager qm = QuestionManager.getInstance();
        
        
        // TODO: add id's for html elements and make it "sexy" with BootStrap.css
        sb.append("<link rel=\"stylesheet\" " +
                "href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.1.2/css/bootstrap.min.css " +
                "\"integrity=\"sha384-Smlep5jCw/wG7hdkwQ/Z5nLIefveQRIY9nfy6xoR1uRYBtpZgI6339F5dgvm/e9B\" " +
                "crossorigin=\"anonymous\">\n");
        sb.append("<h1>"+qm.getCurrentQuestionBundle()+"</h1>\n");
        sb.append("<form method=\"post\" action=\"/sendAnswers\">\n");

        // add userCode tag to the answers
        sb.append("<input name=\"userCode\" type=\"hidden\" value=\""+userCode+ "\">");
        
        // get questions and answer options from the ServerManager class
        al = qm.getQuestionBundle(userCode);

        // TODO: add countdowntimer

        for (Question q : al) {
            // for each question make an html representation

            // TODO: case Types must be done as ENUM
            // TODO: refactor accessing Question with getters and setters



            sb.append("<hr>");
            sb.append(q.getQuestionText()+"\n<p>");
            for (String answer: q.getAnswersVar()) {
	            switch (q.getQuestionType()) {
	                case "singlechoice":
	                    // make radio buttons
	                        sb.append("<input type=\"radio\" name=\"q"+al.indexOf(q)
	                                +"\" value=\""+answer+"\">")
	                                .append(answer)
	                                .append("<br>\n");
	                    break;
	                case "multichoice":
	                    // make check boxes
	                        sb.append("<input type=\"checkbox\" name=\"q"+al.indexOf(q)
	                        			+MULTI_FLAG+"\""+" value=\""+answer+"\">")
	                                .append(answer)
	                                .append("<br>\n");
	                    break;
	                case "open":
	//                     <input type="text" name="usrname"><br>
	                    break;
	                case "sequence":
	//                     <input type="text" name="usrname"><br>
	                    break;
	            }
            }

        }
        sb.append("<hr>");
        sb.append("<input class=\"btn btn-primary\"" +
                " type=\"submit\" value=\"Submit answers and get results\">\n</form>");

        //EXAMPLE:
        //sb.append("<a href='/findTeacher'>Find teacher<a><br/>\n");
        //sb.append("<a href='/deleteTeacher'>Delete teacher<a><br/>\n");
        // Following is also redundant because status is OK by default:

        response.setStatus(HttpServletResponse.SC_OK);
        return sb.toString();
    }
}
