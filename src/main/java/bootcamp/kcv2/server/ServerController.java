package bootcamp.kcv2.server;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import bootcamp.kcv2.util.FileAdapter;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import bootcamp.kcv2.Question;
import bootcamp.kcv2.QuestionManager;
import bootcamp.kcv2.StudentAnswerSheet;
import bootcamp.kcv2.util.BaseConfiguration;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

// TO-DO add auth check for every /admin page and subpage (cookie check)

@Controller
public class ServerController {

    private static final String BOOTSTRAP_CSS = "<link rel=\"stylesheet\" "
            + "href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.1.2/css/bootstrap.min.css "
            + " \" integrity=\"sha384-Smlep5jCw/wG7hdkwQ/Z5nLIefveQRIY9nfy6xoR1uRYBtpZgI6339F5dgvm/e9B\" "
            + "crossorigin=\"anonymous\">\n";
    private static final String MULTI_FLAG = "_multi";
    public static final String COOKIE_NAME = "KCV2Admin";
    public static final String COOKIE_VALUE = "KCV2AdminYES";
    private static final String IMPORT_EXPORT_FILENAME = "df_kcv2_questions.txt";

    // TODO: select one or another. Irrational implementation when parameter is returned to the calling method

    ArrayList<Question> alQuestions = new ArrayList<>();
    private QuestionManager qm = QuestionManager.getInstance();

    /**
     * Method is used to parse and process received answers
     * <p>
     * It does: 1. builds and ArrayList of answers 2. sends answers to the
     * QuestionManager 3. receives score result from QuestionManager 4. displays
     * result to the student
     */
    @RequestMapping(value = "/sendAnswers", produces = "text/html;chearset=UTF-8", method = RequestMethod.POST)
    @ResponseBody
    // TO-DO redesign method as POST to hide answer string and allow variable
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
                            multiString = multiString + multiArray[i] + Question.SEPARATOR;
                        }
                    }
                }
                currentAnswers.add(multiString);
            } else {
                String value = request.getParameter(elementName);
                currentAnswers.add(value);
            }
        }

        if (!qm.isExamStarted()) {
            // TO-DO: if exam is stopped, submit empty results
            currentAnswers = null;
        }
        // TODO: make sure method processes currentAnswers==null as all wrong
        float ratio = qm.submitResults(userCode, currentAnswers, alQuestions);

        // TO-DO: output result for a student as HTML table
        return "<a href=\"/\"><input type=button class=\"btn btn-primary\" value=\"Results have been submitted\"" +
                "<p>Correct answers: " + ratio + "</a>";
    }

    /**
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

            // TODO: add countdown timer (or this will remain manual using "yellow screen" scenario
            // TO-DO: question types must be done as enum

            for (Question q : alQuestions) {
                // for each question make an html representation
                sb.append("<hr>");
                sb.append(q.getQuestionText() + "\n<p>");

                for (String answer : q.getAnswersVar()) {
                    switch (q.getQuestionType()) {
                        case SINGLE_CHOICE:
                            // make radio buttons
                            sb.append("<input type=\"hidden\" name=\"q" + alQuestions.indexOf(q) + MULTI_FLAG
                                    + "\" value=\"" + "" + "\">");
                            sb
                                    .append("<input type=\"radio\" name=\"q" + alQuestions.indexOf(q) + MULTI_FLAG
                                            + "\" value=\"" + answer + "\">")
                                    .append(answer)
                                    .append("<br>\n");
                            break;
                        case MULTI_CHOICE:
                            // make check boxes
                            sb.append("<input type=\"hidden\" name=\"q" + alQuestions.indexOf(q) + MULTI_FLAG + "\""
                                    + " value=\"" + "" + "\">");
                            sb.append("<input type=\"checkbox\" name=\"q" + alQuestions.indexOf(q) + MULTI_FLAG + "\""
                                    + " value=\"" + answer + "\">").append(answer).append("<br>\n");
                            break;
                        case OPEN:
                            sb.append("<input type=\"text\" name=\"q" + alQuestions.indexOf(q) + "\" value=\"" + answer
                                    + "\">").append(answer).append("<br>\n");
                            break;
                        case SEQUENCE:
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
     * Pages to be protected by requiring the authorization:
     *
     * /admin (and set's cookie if it's absent)
     * /admin/startExam
     * /admin/stopExam
     * /admin/showResults
     * /admin/exportQuestions
     * /admin/importQuestions
     *
    * */
    private boolean authorizeAdmin(HttpServletRequest request, HttpServletResponse response) {
        // check if admin is logged in
        boolean isAdmin = false;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                System.err.println("Cookie:" + c + "--" + c.getName() + "---" + c.getValue());
                if (c.getName().equals(COOKIE_NAME) && c.getValue().equals(COOKIE_VALUE)) {
                    isAdmin = true;
                }
            }
        }
        return isAdmin;
    }

    /**
     * Admin page implementation with Start/Stop, Check results, Import, Export
     * buttons.
     *
     * @param authKey  password for administrator
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

        StringBuilder sb = new StringBuilder();
        sb.append(BOOTSTRAP_CSS);
        sb.append("<h1>Administrator Page</h1>");

        // check if admin is logged in
        boolean isAdmin = authorizeAdmin(request, response);
//        boolean isAdmin = false;
//        Cookie[] cookies = request.getCookies();
//        if (cookies != null) {
//            for (Cookie c : cookies) {
//                System.err.println("Cookie:" + c + "--" + c.getName() + "---" + c.getValue());
//                if (c.getName().equals(COOKIE_NAME) && c.getValue().equals(COOKIE_VALUE)) {
//                    isAdmin = true;
//                }
//            }
//        }

		 /*
		 If not admin - check if the GET request has "admin" AUTH_KEY.
		 If provided - set authorized cookie and proceed to the forms
		 If not provided - tell admin to login using correct procedure
		 (i.e. opening /admin?authkey=......)
		 */
        if (!isAdmin) {
            if (authKey == null || !authKey.equals(BaseConfiguration.AUTH_KEY)) {
                return sb.toString() + "\n\nPlease provide password and authorize as Test Administrator";
            } else {
                Cookie c = new Cookie(COOKIE_NAME, COOKIE_VALUE); //bake cookie
                // TODO set production cookie timeout
//                c.setMaxAge(1800); //set expire time to 30 min
                c.setMaxAge(60); //set expire time to 60 seconds for debug purposes
                response.addCookie(c); //put cookie in response
                response.sendRedirect("/admin"); // and return to the main admin page
            }
        }

        if (qm.isExamStarted()) {
            sb.append("<p> Exam is in progress. Press Stop button when the time is over.");
            sb.append("<form method=\"get\" action=\"/admin/stopExam\">");
            sb.append("<input class=\"btn btn-warning\" type=\"submit\" value=\"Stop Exam\" />");
            sb.append("</form>");
            return sb.toString();
        }

        /*
         * If exam is not started then make 4 forms on the web page
         *
         * Start
         * Check results
         * Import
         * Export
         *
         * */
        // Start Exam form
        ArrayList<String> bundleNamesList = qm.pullBundleNames();
        sb.append("<p><form method=\"get\" action=\"/admin/startExam\">");
        sb.append("<select id=\"bundlename\" name=\"bundlename\">");
        for (String str : bundleNamesList) {
            sb.append("<option value=\"" + str + "\">" + str + "</option>");
        }
        sb.append("</select>");
        sb.append("<p><input type=\"submit\" value=\"Start Exam\" class=\"btn btn-danger\"/>");
        sb.append("</form>");

        // Check Results form
        sb.append("<hr>");
        sb.append("<p><form method=\"get\" action=\"/admin/showResults\">");
        sb.append("<p><input type=\"submit\" value=\"Check exam results\" class=\"btn btn-info\"/>");
        sb.append("</form>");

        // Export Questions
        sb.append("<hr>");
        sb.append("<p><form method=\"get\" action=\"/admin/exportQuestions\">");
        sb.append("<p><input type=\"submit\" value=\"Export questions\" />");
        sb.append("</form>");

        // Import Questions
        sb.append("<hr>");
        sb.append("<h3>Import questions</h3>");
        sb.append("<p>Warning! All existing questions in the database will be destroyed!!!");
        sb.append("<p><form enctype=\"multipart/form-data\" method=\"post\" action=\"/admin/importQuestions\">");
        sb.append("<input type=\"file\" name=\"txtfile\">");
        sb.append("<input type=\"submit\" value=\"Import file with questions\"></p><br>");
        sb.append("</form>");

        return sb.toString();
    }

    @RequestMapping(value = "/admin/exportQuestions", method = RequestMethod.GET)
    public void exportQuestions(
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.err.println("ServerController__exportQuestions() called");

        if (!authorizeAdmin(request, response)) {
            System.err.println("NOT AUTHORIZED");
            response.sendRedirect("/admin");
            return;
        }

        // generate file on server
        FileAdapter fa = new FileAdapter();
        fa.exportQuestions(IMPORT_EXPORT_FILENAME);

        response.setContentType("application/octet-stream; charset=UTF-8");
        response.setHeader("Content-Disposition", "Content-Disposition: attachment; filename=\"all_questions.txt\" \t");
        PrintWriter out = response.getWriter();

        // copy from file on server to the browser response
        File f = new File(IMPORT_EXPORT_FILENAME);
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
        String str;
        while ((str = br.readLine()) != null) {
            out.println(str);
        }

        out.flush();
        out.close();
    }

    @RequestMapping(value = "/admin/importQuestions", produces = "text/html;charset=UTF-8", method = RequestMethod.POST)
    public void importQuestions(
            HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        System.err.println("ServerController__importQuestions() called");
        System.err.println("___Received input file:");

        if (!authorizeAdmin(request, response)) {
            System.err.println("NOT AUTHORIZED");
            response.sendRedirect("/admin");
            return;
        }

        Part p = request.getPart("txtfile");
        InputStream ir = p.getInputStream();

        // delete existing temp file on server
        File f = new File(IMPORT_EXPORT_FILENAME);
        f.delete();

        FileWriter fr = new FileWriter(f);

        // TO-DO 1: process POST body and generate .txt file on the server side
        int i;
        while ((i = ir.read()) != -1) {
//            System.err.print((char) i);
            fr.write(i);
        }
        fr.close();

        // TO-DO 2: call FileAdapter method to load the information into DB
        FileAdapter fa = new FileAdapter();
        try {
            fa.importQuestion(IMPORT_EXPORT_FILENAME);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // TODO 3: even better: pass InputStream variable as a parameter to ImportAllQuestions() method

        response.sendRedirect("/admin");
    }

    @RequestMapping(value = "/admin/startExam", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
    @ResponseBody
    public void startExam(
            @RequestParam(value = "bundlename", required = false) String bundleName,
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.err.println("ServerController__startExam() called");

        if (!authorizeAdmin(request, response)) {
            System.err.println("NOT AUTHORIZED");
            response.sendRedirect("/admin");
            return;
        }

        // TODO create startExam() or refine login in setExamStarted();
        // TO-DO pass the selected topic
        qm.setCurrentQuestionBundle(bundleName);
        qm.setExamStarted(true);

        System.err.println("Selected bundle:" + bundleName);
        response.sendRedirect("/admin");
    }

    @RequestMapping(value = "/admin/stopExam", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
    @ResponseBody
    public void stopExam(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.err.println("ServerController__stopExam() called");

        if (!authorizeAdmin(request, response)) {
            System.err.println("NOT AUTHORIZED");
            response.sendRedirect("/admin");
            return;
        }

        // TODO create stopExam() or refine login in setExamStarted();
//		qm.stopExam();
        qm.setExamStarted(false);

        response.sendRedirect("/admin");
    }

    @RequestMapping(value = "/admin/showResults", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
    @ResponseBody
    public String showResults(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.err.println("ServerController__showResults() called");

        if (!authorizeAdmin(request, response)) {
            System.err.println("NOT AUTHORIZED");
            response.sendRedirect("/admin");
        }

        StringBuilder sb = new StringBuilder();
        sb.append(BOOTSTRAP_CSS);
        sb.append("<a href=\"/admin\">Return to the Admin page</a>");
        sb.append("<h1>Exam results</h1>");

        // TODO display results of all students

        sb.append("<a href=\"/admin\">Return to the Admin page</a>");
        return sb.toString();
    }
}
