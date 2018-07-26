package bootcamp.kcv2.test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import bootcamp.kcv2.Question;
import bootcamp.kcv2.QuestionManager;
import bootcamp.kcv2.QuestionTypes;


/**
 * 
 * This class provides Testing for class {@link bootcamp.kcv2.QuestionManager
 * QuestionManager}.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class QuestionManagerTest {
	final Logger log = Logger.getLogger(QuestionManagerTest.class);
	public static QuestionManager manager;

	/**
	 * This method tests if exam is started.
	 */
	@Test
	public final void test01isExamStarted() {
		try {
			manager = QuestionManager.getInstance();
			manager.setExamStarted(false);
			assertFalse(manager.isExamStarted());
		} catch (Exception e) {
		}
	}

	/**
	 * This method try to start exam.
	 */
	@Test
	public final void test02setExamStarted() {
		try {
			manager = QuestionManager.getInstance();
			manager.setExamStarted(true);
			Assert.assertEquals( true, manager.isExamStarted());
			manager.setExamStarted(false);
			Assert.assertEquals(false, manager.isExamStarted());
		} catch (Exception e) {
		}
	}
	
	/**
	 * This method tries to set current question bundle.
	 */
	@Test
	public final void test03setCurrentQuestionBundle() {
		try {
			manager = QuestionManager.getInstance();
			manager.setCurrentQuestionBundle("JUnitTest");
			Assert.assertEquals("JUnitTest", manager.getCurrentQuestionBundle());
		} catch (Exception e) {
		}
	}
	
	/**
	 * This method tries to get current question bundle.
	 */
	@Test
	public final void test04getCurrentQuestionBundle() {
		try {
			manager = QuestionManager.getInstance();
			Assert.assertNotNull(manager.getCurrentQuestionBundle());
		} catch (Exception e) {
		}
	}

	/**
	 * This method tests submitting.
	 */
	@Test
	public final void test05submitResults() {
		try {
			ArrayList<String> answers = new ArrayList<String>();
			answers.add("A");
			ArrayList<Question> alQuestions = manager.pullQuestionBundle(manager.getCurrentQuestionBundle());

			String userCode = "AE58";
			manager = QuestionManager.getInstance();
			manager.submitResults(userCode, answers, alQuestions);
			log.info(userCode);
			log.info(answers);
			log.info(alQuestions);
		} catch (Exception e) {
		}
	}

	/**
	 * This method tries to get bundle names.
	 */
	@Test
	public final void test06pullBundleNames() {
		try {
			manager = QuestionManager.getInstance();
			ArrayList<String> bundleNames = manager.pullBundleNames();
			log.info(bundleNames);
		} catch (Exception e) {
		}
	}

	@Test
	public final void test07insertQuestion() {
		try {
			manager = QuestionManager.getInstance();
			manager.setCurrentQuestionBundle("JUnitTest");
			ArrayList<Question> questions = manager.pullQuestionBundle(manager.getCurrentQuestionBundle());
			assertNotNull(questions);

		} catch (Exception e) {
		}
	}

	/**
	 * This method tries to insert question.
	 */
	@Test
	public final void test071insertQuestion() {
		try {
			manager = QuestionManager.getInstance();

			ArrayList<Question> questions = manager.pullQuestionBundle(manager.getCurrentQuestionBundle());
			assertNotNull(questions);


		} catch (Exception e) {
		}
	}

	/**
	 * This method checks results.
	 */
	@Test
	public final void test08resultsCheck() {
		try {
			ArrayList<String> answers = new ArrayList<String>();
			ArrayList<Question> alQuestions = manager.pullQuestionBundle(manager.pullBundleNames().get(0));
			String userCode = "AE58";
			manager.resultsCheck(userCode, answers, alQuestions);
			for (int i = 0; i < alQuestions.size(); i++) {
				if (i == 0) {
					answers.add(Question.answersGrouping(alQuestions.get(i).getCorrectAnswers()) + "-WRONG");
					continue;
				}
				if (i == alQuestions.size() - 1) {
					answers.add(Question.answersGrouping(alQuestions.get(i).getCorrectAnswers()) + "===");
					continue;
				}
				answers.add(Question.answersGrouping(alQuestions.get(i).getCorrectAnswers()));
			}
			Assert.assertNotNull(manager.resultsCheck(userCode, answers, alQuestions));
		} catch (Exception e) {
		}
	}

	/**
	 * This method sets exam duration.
	 */
	@Test
	public final void test09setExamDuration() {
		try {
			manager = QuestionManager.getInstance();
			manager.setExamDuration(20);
			Assert.assertEquals(20,manager.getExamDuration());
		} catch (Exception e) {
		}
	}

	/**
	 * This method tries to get question bundle.
	 */
	@Test
	public final void test10getQuestionBundle() {
		try {
			manager = QuestionManager.getInstance();
			manager.getQuestionBundle("AE58");
			manager.getQuestionBundle("AE58");
		} catch (Exception e) {
		}
	}

	/**
	 * This method test questions types.
	 */
	@Test
	public final void test11QuestionTypes() {

		QuestionTypes[] a = QuestionTypes.values();
		assertTrue(a != null);
	}

	/**
	 * Same as {@link #test11QuestionTypes() testQuestionTypes}
	 */
	@Test
	public final void test12QuestionTypes() {
		Question test = new Question(0, null, 0, null, "MULTI_CHOICE", null, null);
		ArrayList<String> answersVar = new ArrayList<String>();
		ArrayList<String> correctAnswers = new ArrayList<String>();
		answersVar.add("A");
		correctAnswers.add("A");
		test.setId(1);
		test.setSet("test 1");
		test.setSetId(1);
		test.setQuestionText("A?");
		test.setQuestionType("SINGLE_CHOICE");
		test.setAnswersVar(answersVar);
		test.setCorrectAnswers(correctAnswers);

	}

}
