package bootcamp.kcv2;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class can make object Question if called and can group, split and format sequence type answers.  
 */
public class Question {

	public static final String SEPARATOR = "===";
	private int id;
	private String set;
	private int setId;
	private String questionText;

	private QuestionTypes questionType;
	private ArrayList<String> answersVar;
	private ArrayList<String> correctAnswers;

	public Question(int id, String set, int setId, String questionText, String questiontype,
			ArrayList<String> answersVar, ArrayList<String> correctAnswers) {
		this.id = id;
		this.set = set;
		this.setId = setId;
		this.questionText = questionText;
		this.questionType = QuestionTypes.valueOf(questiontype);
		this.answersVar = answersVar;
		this.correctAnswers = correctAnswers;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the set
	 */
	public String getSet() {
		return set;
	}

	/**
	 * @param set
	 *            the set to set.
	 */
	public void setSet(String set) {
		this.set = set;
	}

	/**
	 * @return the setId
	 */
	public int getSetId() {
		return setId;
	}

	/**
	 * @param setId
	 *            the setId to set.
	 */
	public void setSetId(int setId) {
		this.setId = setId;
	}

	/**
	 * @return the questionText
	 */
	public String getQuestionText() {
		return questionText;
	}

	/**
	 * @param questionText
	 *            the questionText to set.
	 */
	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	/**
	 * @return the question type
	 */
	public QuestionTypes getQuestionType() {
		return questionType;
	}


	
	/**
	 * @param questiontype
	 *            the question type to set.
	 */
	public void setQuestionType(String questiontype) {
		this.questionType = QuestionTypes.valueOf(questiontype);
	}

	/**
	 * @return the answersVar
	 */
	public ArrayList<String> getAnswersVar() {
		return answersVar;
	}

	/**
	 * @param answersVar
	 *            the answersVar to set.
	 */
	public void setAnswersVar(ArrayList<String> answersVar) {
		this.answersVar = answersVar;
	}

	/**
	 * @return the correctAnswers
	 */
	public ArrayList<String> getCorrectAnswers() {
		return correctAnswers;
	}

	/**
	 * @param correctAnswers
	 *            the correctAnswers to set.
	 */
	public void setCorrectAnswers(ArrayList<String> correctAnswers) {
		this.correctAnswers = correctAnswers;
	}
	
	/**
	 * Grouping from array to String to store in database MySQL separating it with delimiter.
	 * @param answersList list with answer for questions. 
	 * @return Answers for question, null if answers list is empty. 
	 */
	public static String answersGrouping(ArrayList<String> answersList) {
		if (answersList == null) {
			return null;
		}
		StringBuilder answers = new StringBuilder();
		for (String a : answersList) {
			if (a == answersList.get(answersList.size() - 1)) {
				return answers.append(a).toString();
			}
			answers.append(a).append(SEPARATOR);
		}
		return answers.toString();
	}

	/**
	 * This method splits answers by selected delimiter for answers variants and correct answers.
	 * @param answers -Student answers. 
	 * @return ArrayList with answers. 
	 */
	public static ArrayList<String> answersSpliter(String answers) {
		ArrayList<String> answersList = new ArrayList<>();
		if (answers == null) {
			return answersList;
		}
		if (answers.contains(SEPARATOR)) {
			String[] parts = answers.split(SEPARATOR);
			for (int i = 0; i < parts.length; i++) {
				answersList.add(parts[i]);
			}
			return answersList;
		} else {
			answersList.add(answers);
			return answersList;
		}
	}
	/**
	 * This method formats answers for sequence type question.
	 * @param answer -student answers. 
	 * @return Sequence answers which was made by student in sequence type question.
	 */
	public static String sequenceAnswersFormatter(String answer){
		
		StringBuilder seqAnswer = new StringBuilder();
		
		char[] answerChars = answer.toCharArray();
		for (char c : answerChars) {
			String stringChar = Character.toString(c);
			Pattern pattern = Pattern.compile("([a-zA-Z])");
			 Matcher matcher = pattern.matcher(stringChar);
			if(matcher.find()){
				seqAnswer.append(stringChar);
			}
		}
		return seqAnswer.toString().toLowerCase();
	}
	
}