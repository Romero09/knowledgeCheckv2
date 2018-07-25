package bootcamp.KCV2;

import static org.junit.Assert.assertEquals;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import bootcamp.kcv2.QuestionManager;
import bootcamp.kcv2.server.ServerApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ServerApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@DirtiesContext
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ServerControllerTest extends Mockito {

	static ResponseEntity<String> entity;
	static String now;
	static QuestionManager manager = QuestionManager.getInstance();

	@Value("${local.server.port}")
	private int port;

	@Rule
	public Timeout globalTimeout = Timeout.seconds(4);

	@Test
	public final void test01WebHome() throws Exception {
		entity = new TestRestTemplate().getForEntity("http://localhost:" + port, String.class);
		assertEquals("Wrong response status value. Check that web app is running.", HttpStatus.OK,
				entity.getStatusCode());
		manager.setExamStarted(true);
		entity = new TestRestTemplate().getForEntity("http://localhost:" + port, String.class);
		assertEquals("Exam not started, check isExamStarted.", true,
				entity.getBody().contains("Enter your student code:"));
	}

	@Test
	public final void test02QuestionPage() throws Exception {
		manager.setCurrentQuestionBundle(manager.pullBundleNames().get(0));
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("Referer", "http://localhost:8080/");
		entity = new TestRestTemplate().getForEntity("http://localhost:" + port + "/exam?userCode=JUni", String.class);
		assertEquals("No referer should be bad request.", true,
				entity.getBody().contains("ERROR"));
		entity = new TestRestTemplate().exchange("http://localhost:" + port + "/exam?userCode=JUni", HttpMethod.GET,
				new HttpEntity<Object>(headers), String.class);
		assertEquals("Wrong response status value. Check that exam is running.", HttpStatus.OK, entity.getStatusCode());
		assertEquals("Question list is not presented.", true,
				entity.getBody().contains("Submit answers and get results"));
	}

	@Test
	public final void test03AdminPage() throws Exception {
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("Referer", "http://localhost:8080/");
		headers.add("Cookie", "KCV2Admin=KCV2AdminYES");
		entity = new TestRestTemplate().exchange("http://localhost:" + port + "/admin?authkey=1234", HttpMethod.GET,
				new HttpEntity<Object>(headers), String.class);
		assertEquals("Acces not granted or exam not started.", true,
				entity.getBody().contains("Exam is in progress. Press Stop button when the time is over."));

		manager.setExamStarted(false);
		entity = new TestRestTemplate().exchange("http://localhost:" + port + "/admin?authkey=1234", HttpMethod.GET,
				new HttpEntity<Object>(headers), String.class);
		assertEquals("Acces not granted or exam not started.", true, entity.getBody()
				.contains("<h1>Administrator Page</h1><p><form method=\"get\" action=\"/admin/startExam\">"));

	}

	@Test
	public final void test04SendAnswers() throws Exception {
		manager.setExamStarted(true);
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("Referer", "http://localhost:8080/");
		entity = new TestRestTemplate().exchange("http://localhost:" + port + "/sendAnswers?userCode=JUni",
				HttpMethod.POST, new HttpEntity<Object>(headers), String.class);
		// System.out.println(entity.getBody());
		assertEquals("Results should be submitted.", true, entity.getBody().contains("Results have been submitted"));
		manager.setExamStarted(false);

	}
	
	@Test
	public final void test05StartExam() throws Exception {
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("Referer", "http://localhost:8080/");
		headers.add("Cookie", "KCV2Admin=KCV2AdminYES");
		entity = new TestRestTemplate().exchange("http://localhost:" + port 
				+ "/admin/startExam?bundlename="+manager.getCurrentQuestionBundle(), HttpMethod.GET,
				new HttpEntity<Object>(headers), String.class);
		
		assertEquals("Acces not granted or exam not started.", true,
				entity.getBody().contains("Exam is in progress. Press Stop button when the time is over."));
		
		entity = new TestRestTemplate().getForEntity("http://localhost:" + port 
				+ "/admin/startExam?bundlename="+manager.getCurrentQuestionBundle(), String.class);
		System.out.println(entity.getBody());
		assertEquals("Acces shouldnt be granted.", true,
				entity.getBody().contains("Please provide password and authorize as Test Administrator"));
	}
	
	@Test
	public final void test06StartExam() throws Exception {
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("Referer", "http://localhost:8080/");
		headers.add("Cookie", "KCV2Admin=KCV2AdminYES");
		entity = new TestRestTemplate().exchange("http://localhost:" + port 
				+ "/admin/stopExam", HttpMethod.GET,
				new HttpEntity<Object>(headers), String.class);
		
		assertEquals("Acces not granted or exam not started.", true,
				entity.getBody().contains("<h1>Administrator Page</h1><p><form method=\"get\" action=\"/admin/startExam\">"));
		
		entity = new TestRestTemplate().getForEntity("http://localhost:" + port 
				+ "/admin/stopExam", String.class);
		System.out.println(entity.getBody());
		assertEquals("Acces shouldnt be granted.", true,
				entity.getBody().contains("Please provide password and authorize as Test Administrator"));
	}
	
	@Test
	public final void test07ShowResult() throws Exception {
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("Referer", "http://localhost:8080/");
		headers.add("Cookie", "KCV2Admin=KCV2AdminYES");
		entity = new TestRestTemplate().exchange("http://localhost:" + port 
				+ "/admin/showResults", HttpMethod.GET,
				new HttpEntity<Object>(headers), String.class);
		assertEquals("Result show not working.", true,
				entity.getBody().contains("Return to the Admin page"));
		
		entity = new TestRestTemplate().getForEntity("http://localhost:" + port 
				+ "/admin/showResults", String.class);
		System.out.println(entity.getBody());
		assertEquals("Acces shouldnt be granted.", true,
				entity.getBody().contains("Please provide password and authorize as Test Administrator"));
		
	}	
}
