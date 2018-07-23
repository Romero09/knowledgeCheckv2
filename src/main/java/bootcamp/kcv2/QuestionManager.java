package bootcamp.kcv2;

import java.util.ArrayList;

import bootcamp.kcv2.util.DBAdapter.QuestionTableAdapter;
import bootcamp.kcv2.util.DBAdapter.ResultTableAdapter;;

public class QuestionManager {

	private boolean isExamStarted = false;

	private static QuestionManager qmSingleton = new QuestionManager();
	private ArrayList<StudentAnswerSheet> answers = new ArrayList<>();
	private String currentQuestionBundle;

	public void setExamStarted(boolean examStarted) {
		isExamStarted = examStarted;
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

		return restultsCheck(userCode, answers, alQuestions);
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


	// Returns Question object searched by SET
	public ArrayList<Question> pullQuestionBundle(String currentQuestionBundle) {
		return QuestionTableAdapter.pullQuestionBundle(currentQuestionBundle);
	}

	public ArrayList<String> pullBundleNames() {
		return QuestionTableAdapter.pullBundleNames();
	}

	// Updates Question object in DB by ID
	public boolean updateQuestion(Question question) {
		return QuestionTableAdapter.updateQuestion(question);
	}

	// Deletes question form DB
	public boolean deleteQuestion(int id) {
		return QuestionTableAdapter.deleteQuestion(id);
	}

	// Inserts new Question in DB with new ID(objects ID is ignored)
	public boolean insertQuestion(Question question) {
		return QuestionTableAdapter.insertQuestion(question);
	}

	public String restultsCheck(String userCode, ArrayList<String> answers, ArrayList<Question> alQuestions) {

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
				if(sequenceAnswer.equals(sequenceCorrectAnswer)){
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
		if(totalQuestions>0){
		percantageResult = (int)(((float) correctAnswersCount / totalQuestions * 100));
		} else{
			percantageResult = 0;
			}
		
		StringBuilder totalResult = new StringBuilder();
		totalResult.append(correctAnswersCount+"/"+totalQuestions);
		totalResult.append(" " + String.valueOf(percantageResult +"%, "));
		totalResult.append("Wrong answers on questions: " + studentResults(correctAnswers) + ".");
		
		return totalResult.toString();
	}
	
	public String studentResults(ArrayList<Integer> correctAnswers){
		
		ArrayList<String> incorrectQuestions = new ArrayList<>();
		
		for (int i = 0; i < correctAnswers.size(); i++) {
			
			if(correctAnswers.get(i)==0)
			incorrectQuestions.add(String.valueOf(i+1));
		}
		
		String incorrectQuestionString = incorrectQuestions.toString();
		
		return incorrectQuestionString;
	}

}
