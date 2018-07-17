package bootcamp.KCV2.Server;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

@Controller
public class ServerController {

    // TODO: tricky part - organize sessioned access per "logged" user
    // TODO: add checks if "exam" is open or not
    // TODO: restrict passing the exam again for the same session

    /**
     * This is mock-up class to simulate incoming set of questons
    * */
    class Question {
        int uniqueId;
        String questionText;
        String questionType;
        ArrayList<String> answerOptions;

        @Override
        public String toString() {
            return questionType + ": " + questionText + "\n\t" + answerOptions;
        }
    }

    // used to store question set for a particular exam
    ArrayList<Question> al = new ArrayList<>();

    {
        Question q1 = new Question();
        q1.questionText = "How many bytes are used to store the Double variable?";
        q1.questionType = "singlechoice";
        q1.answerOptions = new ArrayList<String>();
        q1.answerOptions.add("1 byte");
        q1.answerOptions.add("2 bytes");
        q1.answerOptions.add("I don't know");
        System.err.println(q1);
        al.add(q1);

        Question q2 = new Question();
        q2.questionText = "Which primitive type takes <b>2 bytes</b> in memory?";
        q2.questionType = "multichoice";
        q2.answerOptions = new ArrayList<String>();
        q2.answerOptions.add("byte");
        q2.answerOptions.add("float");
        q2.answerOptions.add("Short");
        q2.answerOptions.add("Int");
        q2.answerOptions.add("char");
        q2.answerOptions.add("Byte");
        System.err.println(q2);
        al.add(q2);

    }
    static String extractPostRequestBody(HttpServletRequest request) {
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            Scanner s = null;
            try {
                s = new Scanner(request.getInputStream(), "UTF-8").useDelimiter("\\A");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return s.hasNext() ? s.next() : "";
        }
        return "";
    }
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
    @RequestMapping(value = "/sendAnswers", produces = "text/html;charset=UTF-8", method = RequestMethod.POST)
    @ResponseBody
    // TODO redesign method as POST to hide answer string and allow variable number of parameters
    public String sendAnswers(@RequestParam(value = "q0", required = false) String a0,
                              @RequestParam(value = "q1", required = false) String a1,
                              HttpServletRequest request,
                              HttpServletResponse response) {

        // TODO: build HashMap<Integer, Integer> with answers

        // TODO: submit answers and get the result
//        result = submitResults(answers);

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
                           HttpServletResponse response) {
        StringBuilder sb = new StringBuilder();

        // TODO: add id's for html elements and make it "sexy" with BootStrap.css
        sb.append("<link rel=\"stylesheet\" " +
                "href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.1.2/css/bootstrap.min.css " +
                "\"integrity=\"sha384-Smlep5jCw/wG7hdkwQ/Z5nLIefveQRIY9nfy6xoR1uRYBtpZgI6339F5dgvm/e9B\" " +
                "crossorigin=\"anonymous\">\n");
        sb.append("<h1>KCv2 SERVER </h1>\n");
        sb.append("<form method=\"post\" action=\"/sendAnswers\">\n");

        // get questions and answer options from the ServerManager class
        // al = ServerManager.getExaminationSet();

        // TODO: add countdowntimer

        for (Question q : al) {
            // for each question make an html representation

            // TODO: case Types must be done as ENUM
            // TODO: refactor accessing Question with getters and setters



            sb.append("<hr>");
            sb.append(q.questionText+"\n<p>");
            switch (q.questionType) {
                case "singlechoice":
                    // make radio buttons
                    for (String answer: q.answerOptions) {
                        sb.append("<input type=\"radio\" name=\"q"+al.indexOf(q)
                                +" value=\"\">")
                                .append(answer)
                                .append("<br>\n");
                    }
                    break;
                case "multichoice":
                    // make checkboxes
                    for (String answer: q.answerOptions) {
                        sb.append("<input type=\"checkbox\" name=\"q"+al.indexOf(q)
                                +"\" value=\"\">")
                                .append(answer)
                                .append("<br>\n");
                    }
                    break;
                case "open":
//                     <input type="text" name="usrname"><br>
                    break;
                case "sequence":
//                     <input type="text" name="usrname"><br>
                    break;
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