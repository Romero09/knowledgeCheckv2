package bootcamp.KCV2;

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
import bootcamp.kcv2.StudentAnswerSheet;

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
			Assert.assertEquals(manager.isExamStarted(), true);
			manager.setExamStarted(false);
			Assert.assertEquals(manager.isExamStarted(), false);
		} catch (Exception e) {
		}
	}

	@Test
	public final void test03getCurrentQuestionBundle() {
		try {
			manager = QuestionManager.getInstance();
			manager.getCurrentQuestionBundle();
			Assert.assertEquals(manager.getCurrentQuestionBundle(), null);
		} catch (Exception e) {
		}
	}

	@Test
	public final void test04setCurrentQuestionBundle() {
		try {
			manager = QuestionManager.getInstance();
			manager.setCurrentQuestionBundle("test4");
			Assert.assertEquals(manager.getCurrentQuestionBundle(), "test4");
		} catch (Exception e) {
		}
	}

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
			manager.setCurrentQuestionBundle("test4");
			ArrayList<Question> questions = manager.pullQuestionBundle(manager.getCurrentQuestionBundle());

		} catch (Exception e) {
		}
	}

	@Test
	public final void test071insertQuestion() {
		try {
			manager = QuestionManager.getInstance();

			ArrayList<Question> questions = manager.pullQuestionBundle(manager.getCurrentQuestionBundle());

		} catch (Exception e) {
		}
	}

	@Test
	public final void test08resultsCheck() {
		try {
			ArrayList<String> answers = new ArrayList<String>();
			ArrayList<Question> alQuestions = manager.pullQuestionBundle(manager.pullBundleNames().get(0));
			String userCode = "AE58";
			manager.resultsCheck(userCode, answers, alQuestions);
			for (int i = 0; i < alQuestions.size(); i++) {
				if(i == 0){
					answers.add(Question.answersGrouping(alQuestions.get(i).getCorrectAnswers())+"-WRONG");
					continue;
				}
				if(i == alQuestions.size()-1){
					answers.add(Question.answersGrouping(alQuestions.get(i).getCorrectAnswers())+"===");
					continue;
				}
				answers.add(Question.answersGrouping(alQuestions.get(i).getCorrectAnswers()));
			}
			Assert.assertEquals(manager.resultsCheck(userCode, answers, alQuestions), "2/3 66%, Wrong answers on questions: [1].");
		} catch (Exception e) {
		}
	}

	@Test
	public final void test09setExamDuration() {
		try {
			manager = QuestionManager.getInstance();
			manager.setExamDuration(20);
			Assert.assertEquals(manager.getExamDuration(), 20);
		} catch (Exception e) {
		}
	}

	@Test
	public final void test10getQuestionBundle() {
		try {
			manager = QuestionManager.getInstance();
			manager.getQuestionBundle("AE58");
			manager.getQuestionBundle("AE58");
		} catch (Exception e) {
		}
	}

	@Test
	public final void test11QuestionTypes() {

		QuestionTypes[] a = QuestionTypes.values();
		assertTrue(a != null);
	}

}
