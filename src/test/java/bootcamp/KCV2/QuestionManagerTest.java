package bootcamp.KCV2;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.maven.plugin.logging.Log;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import bootcamp.kcv2.Question;
import bootcamp.kcv2.QuestionManager;
import bootcamp.kcv2.util.DBAdapter;
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class QuestionManagerTest {
	final Logger log = Logger.getLogger(QuestionManagerTest.class);
	public static QuestionManager manager;
	
	@Test
	public final void test01isExamStarted() {
		try {
			manager = QuestionManager.getInstance();
			assertFalse(manager.isExamStarted());
		} catch (Exception e) {
		}
	}

	@Test
	public final void test02setExamStarted() {
		try {
			manager = QuestionManager.getInstance();
			manager.setExamStarted(true);
			Assert.assertEquals(manager.isExamStarted(),true);
		} catch (Exception e) {
		}
	}
	@Test
	public final void test03getCurrentQuestionBundle() {
		try {
			manager = QuestionManager.getInstance();
			manager.getCurrentQuestionBundle();
			Assert.assertEquals(manager.getCurrentQuestionBundle(),null);
		} catch (Exception e) {
		}
	}
	@Test
	public final void test04setCurrentQuestionBundle() {
		try {
			manager = QuestionManager.getInstance();
			manager.setCurrentQuestionBundle("test4");
			Assert.assertEquals(manager.getCurrentQuestionBundle(),"test4");
		} catch (Exception e) {
		}
	}
	
	@Test
	public final void test05submitResults() {
		try {
			ArrayList<String> answers = new ArrayList<String>();
			answers.add("A");
			ArrayList<Question> alQuestions=manager.pullQuestionBundle(manager.getCurrentQuestionBundle());
			
			String userCode = "AE58";
			manager = QuestionManager.getInstance();
			manager.submitResults(userCode, answers, alQuestions);

		} catch (Exception e) {
		}
	}
	
	@Test
	public final void test06pullBundleNames() {
		try {		
			manager = QuestionManager.getInstance();
			ArrayList<String> bundleNames = manager.pullBundleNames();
			System.err.println(bundleNames);
		} catch (Exception e) {
		}
	}

}
