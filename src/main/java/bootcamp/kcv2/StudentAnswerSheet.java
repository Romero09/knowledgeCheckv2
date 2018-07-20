package bootcamp.kcv2;

import java.util.ArrayList;

public class StudentAnswerSheet {
	
	String studentCode;
	String questionBundleName;
	ArrayList<String> answers;
	
	
	public String getStudentCode() {
		return studentCode;
	}
	public void setStudentCode(String studentCode) {
		this.studentCode = studentCode;
	}
	public String getQuestionBundleName() {
		return questionBundleName;
	}
	public void setQuestionBundleName(String setName) {
		this.questionBundleName = setName;
	}
	public ArrayList<String> getAnswers() {
		return answers;
	}
	public void setAnswers(ArrayList<String> answers) {
		this.answers = answers;
	}
	
	

}
