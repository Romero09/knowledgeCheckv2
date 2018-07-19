package bootcamp.KCV2;

import java.util.ArrayList;

public class Result {

	private int id;
	private String userCode;
	private String questionBundule;
	private int questionId;
	private ArrayList<String> answer;
	private ArrayList<Integer> isCorrect;
	
	public Result(String code, String questionBundule, ArrayList<String> answer,
			ArrayList<Integer> isCorrect) {
		this(0, code, questionBundule, 0, answer, isCorrect);

	}
	
	public Result(int id, String code, String questionBundule, int questionId, ArrayList<String> answer,
			ArrayList<Integer> isCorrect) {
		super();
		this.id = id;
		this.userCode = code;
		this.questionBundule = questionBundule;
		this.questionId = questionId;
		this.answer = answer;
		this.isCorrect = isCorrect;
	}
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String code) {
		this.userCode = code;
	}

	public String getQuestionBundule() {
		return questionBundule;
	}

	public void setQuestionBundule(String questionBundule) {
		this.questionBundule = questionBundule;
	}

	public int getQuestionId() {
		return questionId;
	}

	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}

	public ArrayList<String> getAnswer() {
		return answer;
	}

	public void setAnswer(ArrayList<String> answer) {
		this.answer = answer;
	}

	public ArrayList<Integer> getIsCorrect() {
		return isCorrect;
	}

	public void setIsCorrect(ArrayList<Integer> isCorrect) {
		this.isCorrect = isCorrect;
	}
	
}
