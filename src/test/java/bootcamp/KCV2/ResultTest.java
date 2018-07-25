package bootcamp.KCV2;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import bootcamp.kcv2.Result;
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ResultTest {
	final Logger log = Logger.getLogger(ResultTest.class);
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

	@Test
	public final void test02resultList() {
		ArrayList<ArrayList<String>> resultArray = new ArrayList<ArrayList<String>>() ;
		ArrayList<String> resultList1 = new ArrayList<>();
		resultList1.add("0");
		resultList1.add("1");
		resultList1.add("2");
		log.info(resultList1);
		ArrayList<String> resultList2 = new ArrayList<>();
		resultList2.add("0");
		resultList2.add("1");
		resultList2.add("2");
		log.info(resultList2);
		ArrayList<String> resultList3 = new ArrayList<>();
		resultList3.add("1");
		resultList3.add("1");
		resultList3.add("1");
		log.info(resultList3);
		ArrayList<String> resultList4= new ArrayList<>();
		resultList4.add("3");
		resultList4.add("3");
		resultList4.add("3");
		log.info(resultList4);
		resultArray.add(resultList1);
		resultArray.add(resultList2);
		resultArray.add(resultList3);
		resultArray.add(resultList4);
		log.info(resultArray.size());
		log.info(resultArray.get(3));
		Result.resultList(resultArray);
	}
}
