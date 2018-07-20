package bootcamp.kcv2.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import bootcamp.kcv2.Question;
import bootcamp.kcv2.QuestionManager;
import bootcamp.kcv2.StudentAnswerSheet;
import bootcamp.kcv2.util.BaseConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Scanner;

@Controller
public class ServerController {

	private static final String BOOTSTRAP_CSS = "<link rel=\"stylesheet\" "
			+ "href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.1.2/css/bootstrap.min.css "
			+ " \" integrity=\"sha384-Smlep5jCw/wG7hdkwQ/Z5nLIefveQRIY9nfy6xoR1uRYBtpZgI6339F5dgvm/e9B\" "
			+ "crossorigin=\"anonymous\">\n";
	private static final String MULTI_FLAG = "_multi";

	// TODO: select one or another. Irrational implementation when parameter is returned to the calling method
	
	ArrayList<Question> alQuestions = new ArrayList<>();
	private QuestionManager qm = QuestionManager.getInstance();

	/**
	 * Method is used to parse and process received answers
	 *
	 * It does: 1. builds and ArrayList of answers 2. sends answers to the
	 * QuestionManager 3. receives score result from QuestionManager 4. displays
	 * result to the student
	 *
	 */
	@RequestMapping(value = "/sendAnswers", produces = "text/html;chearset=UTF-8", method = RequestMethod.POST)
	@ResponseBody
	// TODO redesign method as POST to hide answer string and allow variable
	// number of parameters
	public String sendAnswers(@RequestParam(value = "userCode", required = true) String userCode,
			HttpServletRequest request, HttpServletResponse response) {

		System.err.println(userCode);
		ArrayList<String> currentAnswers = new ArrayList<>();

		Enumeration<String> parNames = request.getParameterNames();

		while (parNames.hasMoreElements()) {
			String elementName = parNames.nextElement();
			if (elementName.contains(MULTI_FLAG)) {
				String[] multiArray = request.getParameterValues(elementName);
				String multiString = "";
				for (int i = 0; i < multiArray.length; i++) {
					if (i == multiArray.length - 1) {
						multiString = multiString + multiArray[i];
					} else {
						if (!multiArray[i].equals("")) {
							multiString = multiString + multiArray[i] + "; ";
						}
					}
				}
				currentAnswers.add(multiString);
			} else {
				String value = request.getParameter(elementName);
				currentAnswers.add(value);
			}
		}

		qm.submitResults(userCode, currentAnswers, alQuestions);

		// TODO: output result as HTML table
		return "<a href=\"/\"><input type=button class=\"btn btn-primary\" value=\"Results have been submitted\"</a>";
	}

	/**
	 * 
	 * Main Start page (Homepage). Asks for login or denies access if exam has
	 * not been started yet.
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	@ResponseBody
	public String homePage(HttpServletRequest request, HttpServletResponse response) throws IOException {
		StringBuilder sb = new StringBuilder();

		if (qm.isExamStarted()) {
			sb.append(BOOTSTRAP_CSS);
			sb.append("<center><p>Enter your student code:");
			sb.append("<p><form action=\"exam\">");
			sb.append("<input type=\"text\" name=\"userCode\">");
			sb.append("<p><input class=\"btn btn-primary\" type=\"submit\" value=\"Start Exam now!\">");
			sb.append("</center></form>");
		} else {
			sb.append("<p>Sorry, the exam has not started yet.");
			sb.append("<p>Please wait for the teacher to start exam and RELOAD this page!");
			sb.append("<p><p><a href=\"\">Reload...</a>");
		}

		return sb.toString();
	}

	/**
	 * 
	 * Exam web-page for students. Available during the exam. Loads the
	 * questions and displays a web form with HTML input elements and Submit
	 * button.
	 * 
	 * @param userCode
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/exam", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	@ResponseBody
	public String questionsPage(@RequestParam(value = "userCode", required = true) String userCode,
			HttpServletRequest request, HttpServletResponse response) throws IOException {

		System.err.println(request.getHeader("Referer"));
		StringBuilder sb = new StringBuilder();

		if (request.getHeader("Referer") != null) {
			// Good request, because coming from the home page
			sb.append(BOOTSTRAP_CSS);
            sb.append("<h1>" + qm.getCurrentQuestionBundle() + "</h1>\n");
            sb.append("<form method=\"post\" action=\"/sendAnswers\">\n");

			sb.append("<input name=\"userCode\" type=\"hidden\" value=\"" + userCode + "\">");

			alQuestions = qm.getQuestionBundle(userCode);
//			 DF: questionBundle = qm.getQuestionBundle(userCode);
			
			// TODO: add countdown timer
			// TODO: question types must be done as enum

			for (Question q : alQuestions) {
				// for each question make an html representation
				sb.append("<hr>");
				sb.append(q.getQuestionText() + "\n<p>");

				for (String answer : q.getAnswersVar()) {
					switch (q.getQuestionType()) {
					case "singlechoice":
						// make radio buttons
						sb.append("<input type=\"hidden\" name=\"q" + alQuestions.indexOf(q) + MULTI_FLAG
								+ "\" value=\"" + "" + "\">");
						 sb
                         .append("<input type=\"radio\" name=\"q" + alQuestions.indexOf(q)
                                 + "\" value=\"" + answer + "\">")
                         .append(answer)
                         .append("<br>\n");
						break;
					case "multichoice":
						// make check boxes
						sb.append("<input type=\"hidden\" name=\"q" + alQuestions.indexOf(q) + MULTI_FLAG + "\""
								+ " value=\"" + "" + "\">");
						sb.append("<input type=\"checkbox\" name=\"q" + alQuestions.indexOf(q) + MULTI_FLAG + "\""
								+ " value=\"" + answer + "\">").append(answer).append("<br>\n");
						break;
					case "open":
						sb.append("<input type=\"text\" name=\"q" + alQuestions.indexOf(q) + "\" value=\"" + answer
								+ "\">").append(answer).append("<br>\n");
						break;
					case "sequence":
						// TODO: solve concept of sequence question
						sb.append("<input type=\"text\" name=\"q" + alQuestions.indexOf(q) + MULTI_FLAG + "\" value=\""
								+ answer + "\">").append(answer).append("<br>\n");
						break;
					}
				}

			}
			sb.append("<hr>");
			sb.append("<input class=\"btn btn-primary\""
					+ " type=\"submit\" value=\"Submit answers and get results\">\n</form>");

		} else {
			// Bad request, because user went directly to /exam?AAAA
			sb.append("<p><p><p><p>ERROR: You don't have access to this page");
			sb.append("<p>");
			sb.append("<p><p><a href=\"/\">Go to the Home page</a>");
		}

		response.setStatus(HttpServletResponse.SC_OK);
		return sb.toString();
	}

	/**
	 * Admin page implementation with Start/Stop, Check results, Import, Export
	 * buttons.
	 * 
	 * 
	 * @param authKey
	 *            password for administrator
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/admin", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	@ResponseBody
	public String adminPage(@RequestParam(value = "authkey", required = false) String authKey,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		System.err.println("ServerController__adminPage() called");
		if (authKey == null || !authKey.equals(BaseConfiguration.AUTH_KEY)) {
			return "You don't have access rights to access this page.";
		}

		StringBuilder sb = new StringBuilder();
		sb.append(BOOTSTRAP_CSS);
		sb.append("<h1>Administrator Page</h1>");

		if (qm.isExamStarted()) {
			return "You can only stop the exam. Button will be here to stop exam.";
		}

		sb.append("<p><form>");

		// will redirect to the page with STOP EXAM button
		ArrayList<String> bundleNamesList = qm.pullBundleNames();
		sb.append("<select id=\"bundlename\">");
		for (String str : bundleNamesList) {
			sb.append("<option value=\"" + str + "\">" + str + "</option>");
		}
		sb.append("</select>");
		sb.append(
				"<p><input formmethod=\"get\" formaction=\"\" type=\"submit\" value=\"Start Exam\" class=\"btn btn-danger\"/>");
		System.err.println(sb);

		sb.append("<hr>");
		sb.append(
				"<p><input formmethod=\"\" formaction=\"/admin/results\" type=\"submit\" value=\"Check exam results\" class=\"btn btn-info\"/>");

		sb.append("<hr>");
		sb.append("<p><input formmethod=\"\" formaction=\"\" type=\"submit\" value=\"Export questions\" />");
		sb.append("<p><input formmethod=\"\" formaction=\"\" type=\"submit\" value=\"Import questions\" />");
		sb.append("</form>");
		return sb.toString();
	}
}
