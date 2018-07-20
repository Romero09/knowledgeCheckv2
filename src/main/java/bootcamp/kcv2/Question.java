package bootcamp.kcv2;

import java.util.ArrayList;

public class Question {

	private int id;
	private String set;
	private int setId;
	private String questionText;

	// TODO must me replaced with enum
	// private QuestionTypes questionType;
	private String questionType;
	private ArrayList<String> answersVar;
	private ArrayList<String> correctAnswers;

	public Question(int id, String set, int setId, String questionText, String questiontype,
			ArrayList<String> answersVar, ArrayList<String> correctAnswers) {
		this.id = id;
		this.set = set;
		this.setId = setId;
		this.questionText = questionText;
		this.questionType = questiontype;
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
	 *            the id to set
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
	 *            the set to set
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
	 *            the setId to set
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
	 *            the questionText to set
	 */
	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	/**
	 * @return the questiontype
	 */
	public String getQuestionType() {
		return questionType;
	}

	/**
	 * @param questiontype
	 *            the questiontype to set
	 */
	public void setQuestionType(String questiontype) {
		this.questionType = questiontype;
	}

	/**
	 * @return the answersVar
	 */
	public ArrayList<String> getAnswersVar() {
		return answersVar;
	}

	/**
	 * @param answersVar
	 *            the answersVar to set
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
	 *            the correctAnswers to set
	 */
	public void setCorrectAnswers(ArrayList<String> correctAnswers) {
		this.correctAnswers = correctAnswers;
	}

}
