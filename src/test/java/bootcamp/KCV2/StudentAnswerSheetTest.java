package bootcamp.KCV2;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import bootcamp.kcv2.StudentAnswerSheet;
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StudentAnswerSheetTest {

	@Test
	public final void test01getQuestionBundleName() {
		StudentAnswerSheet test = new StudentAnswerSheet();
		
		test.setQuestionBundleName("test1");
		String qBundleName=test.getQuestionBundleName();
		assertEquals("test1", qBundleName);
		
		test.setStudentCode("AE");
		String studentCode=test.getStudentCode();
		assertEquals("AE", studentCode);
		
		ArrayList<String> answers = new ArrayList<String>();
		answers.add("abc");
		test.setAnswers(answers);
		ArrayList<String> results=test.getAnswers();
		assertEquals(answers,results);
		
	}

}
