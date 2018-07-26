package bootcamp.kcv2;

import java.util.ArrayList;
/**
 * This class provides results for student knowledge check session.
 */
public class Result {

	private int id;
	private String userCode;
	private String questionBundle;
	private int questionId;
	private ArrayList<String> answer;
	private ArrayList<Integer> isCorrect;

	public Result(String code, String questionBundle, ArrayList<String> answer, ArrayList<Integer> isCorrect) {
		this(0, code, questionBundle, 0, answer, isCorrect);

	}

	public Result(int id, String code, String questionBundle, int questionId, ArrayList<String> answer,
			ArrayList<Integer> isCorrect) {
		super();
		this.id = id;
		this.userCode = code;
		this.questionBundle = questionBundle;
		this.questionId = questionId;
		this.answer = answer;
		this.isCorrect = isCorrect;
	}
    /**   
     * @return the id
     */
	public int getId() {
		return id;
	}
    /**
     * @param id -the id to set.
     */
	public void setId(int id) {
		this.id = id;
	}
    /**
     * @return user code
     */
	public String getUserCode() {
		return userCode;
	}
    /**
     * @param code -unique user code.
     */
	public void setUserCode(String code) {
		this.userCode = code;
	}
    /**
     * @return question bundle for test
     */
	public String getQuestionBundle() {
		return questionBundle;
	}
    /**
     * @param questionBundle -question bundle for test session
     */
	public void setQuestionBundule(String questionBundle) {
		this.questionBundle = questionBundle;
	}
    /**
     * @return question id 
     */
	public int getQuestionId() {
		return questionId;
	}
    /**
     * @param questionId -question serial number
     */
	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}
    /**
     * @return answer which was made by student
     */
	public ArrayList<String> getAnswer() {
		return answer;
	}
    /**
     * @param answer student answers
     */
	public void setAnswer(ArrayList<String> answer) {
		this.answer = answer;
	}
     /**
      * @return 1 if correct, 0 if incorrect 
      */
	public ArrayList<Integer> getIsCorrect() {
		return isCorrect;
	}
     /**
      * @param isCorrect contains correct answer about question
      */
	public void setIsCorrect(ArrayList<Integer> isCorrect) {
		this.isCorrect = isCorrect;
	}

}