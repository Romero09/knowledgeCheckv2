package bootcamp.KCV2;

import static org.junit.Assert.assertFalse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;
import org.springframework.web.bind.annotation.RequestParam;

import bootcamp.kcv2.QuestionManager;
import bootcamp.kcv2.server.ServerController;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ServerControllerTest extends Mockito{

	final Logger log = Logger.getLogger(ServerControllerTest.class);
	public static ServerController controller ;
	
	@Test
	public final void test01homePage() {
		 HttpServletRequest request = mock(HttpServletRequest.class);       
	        HttpServletResponse response = mock(HttpServletResponse.class);
		
		
	}
	
	@Test
	public final void test01isExamStarted() {
		 HttpServletRequest request = mock(HttpServletRequest.class);       
	        HttpServletResponse response = mock(HttpServletResponse.class);
	        
	        when(request.getParameter("userCode")).thenReturn("pxzX");
	        
		try {
			controller = new ServerController();
			controller.questionsPage("userCode", request, response);
		} catch (Exception e) {
		}
	}

}
