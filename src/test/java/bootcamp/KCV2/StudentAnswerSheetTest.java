package bootcamp.KCV2;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import bootcamp.kcv2.StudentAnswerSheet;
import junit.framework.Assert;

public class StudentAnswerSheetTest {

	@Test
	public final void test01getQuestionBundleName() {
		StudentAnswerSheet test = new StudentAnswerSheet();
		
		test.setQuestionBundleName("test1");
		String qBundleName=test.getQuestionBundleName();
		Assert.assertEquals("test1", qBundleName);
		
		test.setStudentCode("AE");
		String studentCode=test.getStudentCode();
		Assert.assertEquals("AE", studentCode);
		
		ArrayList<String> answers = new ArrayList<String>();
		answers.add("abc");
		test.setAnswers(answers);
		ArrayList<String> results=test.getAnswers();
		assertEquals(answers,results);
		
	}

}
