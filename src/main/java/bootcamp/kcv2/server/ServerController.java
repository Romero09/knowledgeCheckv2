package bootcamp.kcv2.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import bootcamp.kcv2.Question;
import bootcamp.kcv2.QuestionManager;
import bootcamp.kcv2.util.BaseConfiguration;
import bootcamp.kcv2.util.DBAdapter;
import bootcamp.kcv2.util.DBContract;
import bootcamp.kcv2.util.FileAdapter;

// TO-DO add auth check for every /admin page and subpage (cookie check)

/**
 * 
 * This class helps to control server.
 * <p>'Abandon hope all ye who enter here'  
 * <p>-Dante's Divine Comedy.
 *
 */
@Controller
public class ServerController {

	public static final Logger log = Logger.getLogger(ServerController.class);
    //    private static final String BOOTSTRAP_CSS = "";
    private static final String BOOTSTRAP_CSS = "<!doctype html><html><head>" +
            "<meta charset=\"utf-8\">\n" +
            "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\">" +
            "<link rel=\"stylesheet\" "
            + "href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.1.2/css/bootstrap.min.css"
            + "\" integrity=\"sha384-Smlep5jCw/wG7hdkwQ/Z5nLIefveQRIY9nfy6xoR1uRYBtpZgI6339F5dgvm/e9B\" "
            + "crossorigin=\"anonymous\">\n" +
            "<style> \n" +
            "#logostyle {\n" +
            "    height: 408px;\n" +
            "    width: 612px;\n" +
            "    background-color: #cccccc;\n" +
            "    background-image: url(\"logo.png\");\n" +
            "    display: table-cell;" +
            "    vertical-align: middle;" +
            "}\n" +
            "</style>" +
            "</head><body>\n" +
            "<div class=\"container\">";
    private static final String MULTI_FLAG = "_multi";
    public static final String COOKIE_NAME = "KCV2Admin";
    public static final String COOKIE_VALUE = "KCV2AdminYES";
    private static final String IMPORT_EXPORT_FILENAME = "all_questions_temp.txt";
    public static final int ADMIN_COOKIE_EXPIRE_SECONDS = 1800;
    public static final int DEFAULT_EXAM_DURATION = 20;

    ArrayList<Question> alQuestions = new ArrayList<>();
    private QuestionManager qm = QuestionManager.getInstance();

    /**
     * Method is used to parse and process received answers
     * <p>
     * It does: 
     * <p>1. Builds and ArrayList of answers. 
     * <p>2. Sends answers to the QuestionManager. 
     * <p>3. Receives score result from QuestionManager. 
     * <p>4. Displays result to the student.
     */
    @RequestMapping(value = "/sendAnswers", produces = "text/html;chearset=UTF-8", method = RequestMethod.POST)
    @ResponseBody
    // TO-DO redesign method as POST to hide answer string and allow variable
    // number of parameters
    public String sendAnswers(@RequestParam(value = "userCode", required = true) String userCode,
                              HttpServletRequest request, HttpServletResponse response) {

        log.info(userCode);
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

        // TO-DO: make sure QuestionManager method processes currentAnswers==null as all wrong
        String ratio = qm.submitResults(userCode, currentAnswers, alQuestions);
        // TO-DO: output result for a student as HTML table
        return "<h3>Results have been submitted</h3><p>" +
                "\n<h3>Here is your result:</h3><p>" + ratio +
                "<p>You may now close this page.";
    }

    /**
     * This method provides main Start page (Homepage). Asks for login or denies access if exam has
     * not been started yet.
     *
     * @param request user sent data
     * @param response server response to user data
     * @return page in which users are redirected according to situation.
     * @see IOException
     */
    @RequestMapping(value = "/", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
    @ResponseBody
    public String homePage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuilder sb = new StringBuilder();

        sb.append(BOOTSTRAP_CSS);
        sb.append("<center><div id=\"logostyle\" class=\"align-middle\">\n");
        if (qm.isExamStarted()) {
            sb.append("<p>Enter your <b>4-character</b> student code:\n");
            sb.append("<p><form action=\"exam\">");
            sb.append("<input type=\"text\" name=\"userCode\" maxlength=\"4\">");
            sb.append("<p><p><p><input class=\"btn btn-primary\" type=\"submit\" value=\"Start Exam now!\">\n");
            sb.append("</form>\n");
            sb.append("</div>\n</center>\n</div>");
        } else {
            sb.append("<b><p>Sorry, the exam has not started yet.");
            sb.append("<p>Please wait for the teacher to start exam and RELOAD this page!");
            sb.append("<p><p><a href=\"\">Reload...</a></b>");
        }

        return sb.toString();
    }

    /**
     * This method shows exam web-page for students.That is available during the exam. Loads the
     * questions and displays a web form with HTML input elements and Submit
     * button.
     *
     * @param userCode user unique code
     * @param request user sent data
     * @param response server response to user data
     * @return page in which users are redirected according to situation.
     * @see IOException
     */
    @RequestMapping(value = "/exam", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
    @ResponseBody
    public String exam(@RequestParam(value = "userCode", required = true) String userCode,
                       HttpServletRequest request, HttpServletResponse response) throws IOException {

        log.info(request.getHeader("Referer"));
        StringBuilder sb = new StringBuilder();

        if (request.getHeader("Referer") != null) {
            // Good request, because coming from the home page
            sb.append(BOOTSTRAP_CSS);
            sb.append("<h1>Test topic: " + qm.getCurrentQuestionBundle() + "</h1>\n");
            sb.append("<form method=\"post\" action=\"/sendAnswers\">\n");

            sb.append("<input name=\"userCode\" type=\"hidden\" value=\"" + userCode + "\">");

            alQuestions = qm.getQuestionBundle(userCode);
//			 DF: questionBundle = qm.getQuestionBundle(userCode);

            // check if bundle is null (someone has already logged in
            if (alQuestions == null || userCode.length() != 4) {
                return "<h1>Error!</h1><p>User has already participated or incorrect user name.";
            }

            // TO-DO: add countdown timer (or this will remain manual using "yellow screen" scenario
            // TO-DO: question types must be done as enum
            sb.append("<hr><h2 class=\"text-warning\">");
            sb.append(qm.getExamEnds());
            sb.append("</h2><p>");


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
     * <p> Admin pages:
     * <p>/admin (and set's cookie if it's absent)
     * <p>/admin/startExam
     * <p>/admin/stopExam
     * <p>/admin/showResults
     * <p>/admin/exportQuestions
     * <p>/admin/importQuestions
     * @param request user sent data
     * @param response server response to user data
     * @return true if user is admin, false if isn't
     */
    private boolean authorizeAdmin(HttpServletRequest request, HttpServletResponse response) {
        // check if admin is logged in
        boolean isAdmin = false;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                log.info("Cookie:" + c + "--" + c.getName() + "---" + c.getValue());
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
        log.info("ServerController__adminPage() called");

        StringBuilder sb = new StringBuilder();
        sb.append(BOOTSTRAP_CSS);
        sb.append("<h1 align=\"center\">Administrator Panel</h1>");

        // check if admin is logged in
        boolean isAdmin = authorizeAdmin(request, response);
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
                c.setMaxAge(ADMIN_COOKIE_EXPIRE_SECONDS); //set expire time to 30 min
                response.addCookie(c); //put cookie in response
            }
        }

        if (qm.isExamStarted()) {
            sb.append("<hr><h2 class=\"text-warning\">");
            sb.append(qm.getExamEnds());
            sb.append("</h2><p>");
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
        sb.append("<hr>");
        sb.append("<h2>I. Exam Administration</h2>");
        ArrayList<String> bundleNamesList = qm.pullBundleNames();

        sb.append("<table class=\"table align-bottom\"><tr><td>");
        sb.append("<p><form method=\"get\" action=\"/admin/startExam\">");
        sb.append("<p>Select test duration (minutes)<p>");
        sb.append("<input type=\"text\" name=\"examduration\" value=\"20\" size=\"5\">" +
                "");
        sb.append("<p>Select exam topic:<p>");
        sb.append("<select id=\"bundlename\" name=\"bundlename\">");
        for (String str : bundleNamesList) {
            sb.append("<option value=\"" + str + "\">" + str + "</option>");
        }
        sb.append("</select>");

        sb.append("<p><input type=\"submit\" value=\"Start Exam\" class=\"btn btn-danger\"/>");
        sb.append("</form>");
        sb.append("</td><td class=\"align-bottom\">");

        // Check Results form
        sb.append("<p><form method=\"get\" action=\"/admin/showResults\">");
        sb.append("<p>Select results topic:<p>");
        sb.append("<select id=\"resultbundle\" name=\"resultbundle\">");
        for (String str : bundleNamesList) {
            sb.append("<option value=\"" + str + "\">" + str + "</option>");
        }
        sb.append("</select>");
        sb.append("<p><input type=\"submit\" value=\"Check exam results\" class=\"btn btn-info\"/>");
        sb.append("</form>");

        sb.append("</td></tr></table></div>");


        sb.append("<div class=\"container\"><hr><h2>II. Database Maintenance</h2>");
        // Clear answers
        sb.append("<hr>");
        sb.append("<h3>Clear answers history</h3>" +
                "<p>Warning! The table with answers will be erased completely!");
        sb.append("<p><form method=\"get\" action=\"/admin/clearAnswers\">");
        sb.append("<p><input type=\"submit\" value=\"Clear Answers\" />");
        sb.append("</form>");
        sb.append("</tr></td>");

        // Export Questions
        sb.append("<hr>");
        sb.append("<h3>Export questions</h3>");
        sb.append("<p><form method=\"get\" action=\"/admin/exportQuestions\">");
        sb.append("<p><input type=\"submit\" value=\"Export questions\" />");
        sb.append("</form>");

        // Import Questions
        sb.append("<hr>");
        sb.append("<h3>Import questions</h3>");
        sb.append("<p>Warning! All existing questions in the database will be destroyed!!!");
        sb.append("<p><form enctype=\"multipart/form-data\" method=\"post\" action=\"/admin/importQuestions\">");
        sb.append("<input type=\"file\" name=\"txtfile\">");
        sb.append("<input type=\"submit\" value=\"Import file with questions\"><br>");
        sb.append("</form>");

        return sb.toString();
    }

    /**
     * This method if called redirects to {@link bootcamp.kcv2.util.DBAdapter.ResultTableAdapter#clearResultTable  clearTable}.
     * @param request user sent data
     * @param response server response to user data
     * @return notification if table was cleared or not
     * @see IOException
     */
    @RequestMapping(value = "/admin/clearAnswers", method = RequestMethod.GET)
    @ResponseBody
    public String clearAnswers(
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("ServerController__exportQuestions() called");

        if (!authorizeAdmin(request, response)) {
            log.info("NOT AUTHORIZED");
            response.sendRedirect("/admin");
            return "";
        } else {
            if (DBAdapter.ResultTableAdapter.clearResultTable()) {
                return "All answers have been deleted.<p>" +
                        "<a href=\"/admin\">Click here to return to Admin page.</a>";
            } else {
                return "Problem occurred while clearing answers.<p>" +
                        "<a href=\"/admin\">Click here to return to Admin page.</a>";
            }
        }
    }

    /**
     * This method export Question from admin panel.
     * @param request user sent data
     * @param response server response to user data
     * @see IOException
     */
    @RequestMapping(value = "/admin/exportQuestions", method = RequestMethod.GET)
    public void exportQuestions(
            HttpServletRequest request, HttpServletResponse response) throws IOException {
       log.info("ServerController__exportQuestions() called");

        if (!authorizeAdmin(request, response)) {
            log.info("NOT AUTHORIZED");
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
        br.close();
        out.flush();
        out.close();
    }

    /**
     * This method import file into data base from admin panel.
     * @param request user sent data
     * @param response server response to user data
     * @see IOException
     * @see ServletException
     */
    @RequestMapping(value = "/admin/importQuestions", produces = "text/html;charset=UTF-8", method = RequestMethod.POST)
    public void importQuestions(
            HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        log.info("ServerController__importQuestions() called");
        log.info("___Received input file:");

        if (!authorizeAdmin(request, response)) {
            log.info("NOT AUTHORIZED");
            response.sendRedirect("/admin");
            return;
        }

        Part p = request.getPart("txtfile");
        InputStream ir = p.getInputStream();

        // delete existing temp file on server
        File f = new File(IMPORT_EXPORT_FILENAME);
        f.delete();

        FileWriter fr = new FileWriter(f);

        int i;
        while ((i = ir.read()) != -1) {
            fr.write(i);
        }
        fr.close();

        FileAdapter fa = new FileAdapter();
        try {
            fa.importQuestion(IMPORT_EXPORT_FILENAME);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        response.sendRedirect("/admin");
    }

    /**
     * This method provides start exam function in admin panel.
     * @param bundleName question set for exam.
     * @param examDuration time for completing exam
     * @param request user sent data
     * @param response server response to user data
     * @see IOException
     */
    @RequestMapping(value = "/admin/startExam", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
    @ResponseBody
    public void startExam(
            @RequestParam(value = "bundlename", required = false) String bundleName,
            @RequestParam(value = "examduration", required = false) String examDuration,
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("ServerController__startExam() called");

        if (!authorizeAdmin(request, response)) {
        	log.info("NOT AUTHORIZED");
            response.sendRedirect("/admin");
            return;
        }

        int intDuration = DEFAULT_EXAM_DURATION;
        try {
            intDuration = Integer.valueOf(examDuration);
        } catch (NumberFormatException e) {
            response.sendRedirect("/admin");
        }

        // if all parameters correct - start exam
        qm.setCurrentQuestionBundle(bundleName);
        qm.setExamDuration(intDuration); // TO-DO request value from Admin
        qm.setExamStarted(true);

        log.info("Selected bundle:" + bundleName);
        response.sendRedirect("/admin");
    }

    @RequestMapping(value = "/admin/stopExam", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
    @ResponseBody
    public void stopExam(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("ServerController__stopExam() called");

        if (!authorizeAdmin(request, response)) {
           log.info("NOT AUTHORIZED");
            response.sendRedirect("/admin");
            return;
        }

        if (qm.isExamStarted()) {
            qm.setExamStarted(false);
        }
        response.sendRedirect("/admin");
    }

    /**
     * This method provides function in admin panel which can show result.
     * @param bundleName question set for exam.
     * @param request user sent data
     * @param response server response to user data
     * @return page in which users are redirected according to situation.
     * @see IOException
     */
    @RequestMapping(value = "/admin/showResults", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
    @ResponseBody
    public String showResults(
            @RequestParam(value = "resultbundle", required = true) String bundleName,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
       log.info("ServerController__showResults() called");

        if (!authorizeAdmin(request, response)) {
        	log.info("NOT AUTHORIZED");
            response.sendRedirect("/admin");
        }

        StringBuilder sbFinal = new StringBuilder(); // for finalpage
        StringBuilder sb = new StringBuilder(); // for detailed table
        StringBuilder sb2 = new StringBuilder(); // for student summary
        HashMap<String, Integer> studentSummary = new HashMap<>();

        sbFinal.append(BOOTSTRAP_CSS);
        sbFinal.append("<a href=\"/admin\">Return to the Admin page</a>");
        sbFinal.append("<h1>Exam results</h1>");

        // TO-DO display results of all students
        sb.append("<table class=\"table table-hover\"><tr>");
        sb.append("<thead class=\"thead-light\">");
        sb.append("<th>Student</th>");
        sb.append("<th>Topic</th>");
        sb.append("<th>Question #</th>");
        sb.append("<th>Question Text</th>");
        sb.append("<th>Student Answer</th>");
        sb.append("<th>Correct Answer Was</th>");
        sb.append("<th>Answer Correct?</th>");
        sb.append("</tr>");
        sb.append("</thead>");

        int questionCount = 0;
        String firstUser = "";

        ResultSet rs = DBAdapter.ResultTableAdapter.pullResultsBundle(bundleName);
        try {
            while (rs.next()) {
                sb.append("<tr>");
                String userCode = rs.getString(DBContract.ResultTable.USER_CODE_KEY);
                if (firstUser.equals("")) {
                    firstUser = userCode;
                    questionCount++;
                } else if (firstUser.equals(userCode)) {
                    questionCount++;
                }
                sb.append("<td>" + userCode + "</td>");
                sb.append("<td>" + bundleName + "</td>");
                sb.append("<td>" + rs.getInt(DBContract.ResultTable.QUESTION_ID_KEY) + "</td>");
                sb.append("<td>" + rs.getString(DBContract.QuestionTable.QUESTION_TEXT_KEY) + "</td>");


                sb.append("<td>");
                ArrayList<String> studentAnswers = Question.answersSpliter(rs.getString(DBContract.ResultTable.ANSWER_KEY));
                for (String str : studentAnswers) {
                    sb.append(str + "<br>");
                }
                sb.append("</td>");

                sb.append("<td>");
                ArrayList<String> answers = Question.answersSpliter(rs.getString(DBContract.QuestionTable.ANSWERS_COR_KEY));
                for (String str : answers) {
                    sb.append(str + "<br>");
                }
                sb.append("</td>");

                String correctText;
                int correctNum = rs.getInt(DBContract.ResultTable.IS_CORRECT_KEY);
                if (correctNum == 1) {
                    correctText = "<p class=\"text-success\">yes</p>";
                } else {
                    correctText = "<p class=\"text-danger\">NO</p>";
                }
                sb.append("<td>" + correctText + "</td>");
                sb.append("</tr>");

                if (studentSummary.get(userCode) != null) {
                    // increment correct answers
                    studentSummary.put(userCode, studentSummary.get(userCode) + correctNum);
                } else {
                    // add new object and it's counter of right answers
                    studentSummary.put(userCode, correctNum);
                }
            }
            log.info(studentSummary);
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sb.append("</table>");

        // forming second table with student summary
        sb2.append("<table class=\"table table-hover\"><tr>");
        sb2.append("<thead class=\"thead-light\">");
        sb2.append("<th>Student</th>");
        sb2.append("<th>Correct Answers</th>");
        sb2.append("<th>Total Answers in the test</th>");
        sb2.append("<th>Ratio, %</th>");
        sb2.append("</tr>");
        sb2.append("</thead>");

        for (String str : studentSummary.keySet()) {
            sb2.append("<tr>");
            sb2.append("<td>" + str + "</td>");
            sb2.append("<td>" + studentSummary.get(str) + "</td>");
            sb2.append("<td>" + questionCount + "</td>");
            sb2.append("<td>" + String.format("%3.0f", 100f * studentSummary.get(str) / questionCount) + "</td>");
            sb2.append("</tr>");
        }

        sb2.append("</table>");


        sbFinal.append("<ul class=\"nav nav-pills\">\n");
        sbFinal.append("       <li class=\"nav-item\"><a class=\"nav-link active\" data-toggle=\"pill\" href=\"#summary\">Summary</a></li>\n");
        sbFinal.append("       <li class=\"nav-item\"><a class=\"nav-link\" data-toggle=\"pill\" href=\"#details\">Details</a></li>\n");
        sbFinal.append("</ul><p>\n\n");
            sbFinal.append("<div class=\"tab-content\">\n");
                sbFinal.append("<div id=\"summary\" class=\"tab-pane fade show active\">\n");
                    sbFinal.append("<h3>Summary</h3>");
                    sbFinal.append("<p>"+sb2.toString()+"</p>");
                sbFinal.append("</div>\n");
                sbFinal.append("<div id=\"details\" class=\"tab-pane fade\">");
                    sbFinal.append("<h3>Details</h3>");
                    sbFinal.append("<p>"+sb.toString()+"</p>");
                sbFinal.append("</div>\n");
            sbFinal.append("</div>");


        sbFinal.append("<p><a href=\"/admin\">Return to the Admin page</a>");
        sbFinal.append("<!-- Optional JavaScript -->");
        sbFinal.append("<!-- jQuery first, then Popper.js, then Bootstrap JS -->");
        sbFinal.append("<script src=\"https://code.jquery.com/jquery-3.3.1.slim.min.js\" integrity=\"sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo\" crossorigin=\"anonymous\"></script>");
        sbFinal.append("<script src=\"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js\" integrity=\"sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49\" crossorigin=\"anonymous\"></script>");
        sbFinal.append("<script src=\"https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js\" integrity=\"sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy\" crossorigin=\"anonymous\"></script>");

        return sbFinal.toString();
    }
}
