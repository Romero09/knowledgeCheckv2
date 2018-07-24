package bootcamp.kcv2;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import bootcamp.kcv2.util.DBAdapter.QuestionTableAdapter;
import bootcamp.kcv2.util.DBAdapter.ResultTableAdapter;;

public class QuestionManager {

	private boolean isExamStarted = false;

	private static QuestionManager qmSingleton = new QuestionManager();
	private ArrayList<StudentAnswerSheet> answers = new ArrayList<>();
	private String currentQuestionBundle;
	private int examDuration = 20; // Default value
	private Timer timer;

	public void setExamStarted(boolean examStarted) {
		isExamStarted = examStarted;
		if (examStarted == false) {
			timer.cancel();
		}
		if (examStarted == true) {
			examTimer();
		}
	}

	/**
	 * Returns true if exam session has been started by administrator
	 *
	 */
	public boolean isExamStarted() {
		return isExamStarted;
	}

	/**
	 * @return the currentQuestionBundle
	 */
	public String getCurrentQuestionBundle() {
		return currentQuestionBundle;
	}

	public void setCurrentQuestionBundle(String set) {
		this.currentQuestionBundle = set;
	}

	private QuestionManager() {
	}

	public static QuestionManager getInstance() {
		return qmSingleton;
	}

	public int getExamDuration() {
		return examDuration;
	}

	public void setExamDuration(int examDuration) {
		this.examDuration = examDuration;
	}

	/**
	 * 
	 * @param userCode
	 * @param answers
	 *            - Answers list that was made by student
	 * @return
	 */
	public String submitResults(String userCode, ArrayList<String> answers, ArrayList<Question> alQuestions) {

		answers.remove(0);

		System.err.println("QuestionManager: Submitted results:\n\t" + "User=" + userCode + " Answers=" + answers);

		return resultsCheck(userCode, answers, alQuestions);
	}

	/**
	 * Generates a list of Questions to be displayed by ServerController (parsed
	 * and later HTML is made)
	 * 
	 * @param userCode
	 * @return
	 */
	public ArrayList<Question> getQuestionBundle(String userCode) {
		for (StudentAnswerSheet studentAnswerSheet : answers) {
			if (studentAnswerSheet.getStudentCode().equals(userCode)) {
				System.err.println("User has already participated.");
				return null;
			}
		}
		StudentAnswerSheet as = new StudentAnswerSheet();
		as.setStudentCode(userCode);
		as.setQuestionBundleName(qmSingleton.currentQuestionBundle);
		answers.add(as);

		return pullQuestionBundle(qmSingleton.currentQuestionBundle);
	}

	public static String examTimer() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		cal.add(Calendar.MINUTE, qmSingleton.examDuration);

		Date time = cal.getTime();
		qmSingleton.timer = new Timer();
		qmSingleton.timer.schedule(qmSingleton.new RemindTask(), time);
		String examEnds = "Exam will ends at: " + sdf.format(cal.getTime());
		System.out.println(examEnds);
		return examEnds;
	}

	class RemindTask extends TimerTask {
		public void run() {
			isExamStarted = false;
			System.out.println("Time's up!");
			qmSingleton.timer.cancel(); // Terminate the timer thread
		}
	}

	public static void main(String args[]) {
		examTimer();
	}

	// Returns Question object searched by SET
	public ArrayList<Question> pullQuestionBundle(String currentQuestionBundle) {
		return QuestionTableAdapter.pullQuestionBundle(currentQuestionBundle);
	}

	public ArrayList<String> pullBundleNames() {
		return QuestionTableAdapter.pullBundleNames();
	}

	// TODO DELETE// Updates Question object in DB by ID
	// public boolean updateQuestion(Question question) {
	// return QuestionTableAdapter.updateQuestion(question);
	// }

	// TODO DELETE// Deletes question form DB
	// public boolean deleteQuestion(int id) {
	// return QuestionTableAdapter.deleteQuestion(id);
	// }

	// Inserts new Question in DB with new ID(objects ID is ignored)
	public boolean insertQuestion(Question question) {
		return QuestionTableAdapter.insertQuestion(question);
	}

	public String resultsCheck(String userCode, ArrayList<String> answers, ArrayList<Question> alQuestions) {

		ArrayList<Integer> correctAnswers = new ArrayList<>();
		int totalQuestions = 0;
		int correctAnswersCount = 0;
		String currentAnswer = "";
		String correctAnswer = "";

		if (answers.size() != alQuestions.size()) {
			System.err.println("Something went wrong Answers array size doesent match Question array size");
		}
		for (int i = 0; i < answers.size(); i++) {
			correctAnswer = Question.answersGrouping(alQuestions.get(i).getCorrectAnswers());
			totalQuestions++;
			if (answers.get(i).replaceAll(".*?(.?.?.?)?$", "$1").contains(Question.SEPARATOR)) {
				currentAnswer = answers.get(i).substring(0, answers.get(i).length() - 3);
			} else {
				currentAnswer = answers.get(i);
			}
			if (alQuestions.get(i).getQuestionType().equals(QuestionTypes.SEQUENCE)) {
				String sequenceAnswer = Question.sequenceAnswersFormatter(currentAnswer);
				String sequenceCorrectAnswer = Question.sequenceAnswersFormatter(correctAnswer);
				if (sequenceAnswer.equals(sequenceCorrectAnswer)) {
					correctAnswers.add(1); // Correct - True
					correctAnswersCount++;
				} else {
					correctAnswers.add(0); // Incorrect - False
				}
				continue;
			}
			if (currentAnswer.equals(correctAnswer)) {
				correctAnswers.add(1); // Correct - True
				correctAnswersCount++;
			} else {
				correctAnswers.add(0); // Incorrect - False
			}
		}
		Result result = new Result(userCode, currentQuestionBundle, answers, correctAnswers);
		ResultTableAdapter.insertQuestion(result);

		int percantageResult = 0;
		if (totalQuestions > 0) {
			percantageResult = (int) (((float) correctAnswersCount / totalQuestions * 100));
		} else {
			percantageResult = 0;
		}
		StringBuilder totalResult = new StringBuilder();
		totalResult.append(correctAnswersCount + "/" + totalQuestions);
		totalResult.append(" " + String.valueOf(percantageResult + "%, "));
		totalResult.append("Wrong answers on questions: " + studentResults(correctAnswers) + ".");
		return totalResult.toString();
	}

	public String studentResults(ArrayList<Integer> correctAnswers) {
		ArrayList<String> incorrectQuestions = new ArrayList<>();
		for (int i = 0; i < correctAnswers.size(); i++) {
			if (correctAnswers.get(i) == 0)
				incorrectQuestions.add(String.valueOf(i + 1));
		}
		String incorrectQuestionString = incorrectQuestions.toString();
		return incorrectQuestionString;
	}

}
