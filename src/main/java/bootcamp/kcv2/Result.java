package bootcamp.kcv2;

import java.util.ArrayList;
import java.util.Iterator;

public class Result {

	private int id;
	private String userCode;
	private String questionBundule;
	private int questionId;
	private ArrayList<String> answer;
	private ArrayList<Integer> isCorrect;

	public Result(String code, String questionBundule, ArrayList<String> answer, ArrayList<Integer> isCorrect) {
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

	public static ArrayList<String> resultList(ArrayList<ArrayList<String>> resultArray) {

		ArrayList<String> resultList = new ArrayList<>();

		String currentCode = "";
		int currentScore = 0;
		String oneResult = "";
		String resultAnswers = "";

		for (int i = 0; i < resultArray.size(); i++) {
			
			if (currentCode.equals("")) {
				currentCode = resultArray.get(i).get(0);
				currentScore = Integer.valueOf(resultArray.get(i).get(1));
				resultAnswers = resultArray.get(i).get(1);
			} else {
				if (currentCode.equals(resultArray.get(i).get(0))) {
					resultAnswers = resultAnswers + "|" + resultArray.get(i).get(1);
					currentScore = currentScore + Integer.valueOf(resultArray.get(i).get(1));
					if (i == resultArray.size() - 1) {
						oneResult = "Student: " + currentCode + " Result: " + resultAnswers + " Total: "+ String.valueOf(currentScore);
						resultList.add(oneResult);
						currentCode = resultArray.get(i).get(0);
						currentScore = Integer.valueOf(resultArray.get(i).get(1));
						resultAnswers = resultArray.get(i).get(1);
					}
				} else {
					oneResult = "Student: " + currentCode + " Result: " + resultAnswers + " Total: "+ String.valueOf(currentScore);
					resultList.add(oneResult);
					currentCode = resultArray.get(i).get(0);
					currentScore = Integer.valueOf(resultArray.get(i).get(1));
					resultAnswers = resultArray.get(i).get(1);
				}
			}
		}

		return resultList;

	}

}
