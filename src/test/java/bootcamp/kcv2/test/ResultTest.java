package bootcamp.kcv2.test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import bootcamp.kcv2.Result;

/**
 * 
 * 
 * This class provides Testing for class {@link bootcamp.kcv2.Result  Result}.
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ResultTest {
	final Logger log = Logger.getLogger(ResultTest.class);
	
	/**
	 * This method tries to get set results.
	 */
	@Test
	public final void test01resultSetGet() {
		Result result = new Result(0, null, null, 0, null, null);
		ArrayList<String> answer = new ArrayList<String>();
		ArrayList<Integer> isCorrect = new ArrayList<Integer>();
		answer.add("A");
		answer.add("B");
		isCorrect.add(0);
		result.setId(1);
		assertEquals(1,result.getId());
		result.setUserCode("AE");
		assertEquals("AE",result.getUserCode());
		result.setQuestionBundule("test 1");
		assertEquals("test 1",result.getQuestionBundle());
		result.setQuestionId(1);
		assertEquals(1,result.getQuestionId());
		result.setAnswer(answer);
		assertEquals(answer,result.getAnswer() );
		result.setIsCorrect(isCorrect);
		assertEquals(isCorrect,result.getIsCorrect());
	}

}
