package bootcamp.kcv2.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import bootcamp.kcv2.Question;
import bootcamp.kcv2.QuestionManager;

/**
 * This class provides export from database
 * and input data from txt file into ArrayList.
 */

public class FileAdapter  {
	
	static final Logger log = Logger.getLogger(FileAdapter.class);
	
	private static final String ANSWER_VARIANT = "ANSWER VARIANTS:";
	private static final String ANSWER_CORRECT = "ANSWER CORRECT:";
	private static final String QUESTION_TYPE = "QUESTION TYPE:";
	private static final String QUESTION_TEXT = "QUESTION TEXT:";
	private static final String QUESTION_ID = "QUESTION ID:";
	private static final String QUESTION_BUNDLE= "QUESTION BUNDLE:";
	private static final String ID = "ID:";
	
	/**
	 * This method  exports content into txt file.
	 *  
	 * @param fileName File name where the content are saved.
	 * @return True if successful, false if error.
	 * @see IOException
	 */
	public boolean exportQuestions( String fileName) {
		ArrayList<Question> alq = QuestionManager.getInstance().pullQuestionBundle("%%");
		try {
			PrintWriter writer = new PrintWriter(fileName, "UTF-8");
			for (Question q : alq) {
				log.info("Readed line" + q);
				writer.println(ID+q.getId());
			    writer.println(QUESTION_BUNDLE+q.getSet());
			    writer.println(QUESTION_ID+q.getSetId());
			    writer.println(QUESTION_TEXT+q.getQuestionText());
			    writer.println(QUESTION_TYPE+q.getQuestionType());
			    writer.println(ANSWER_VARIANT+Question.answersGrouping(q.getAnswersVar()));
			    writer.println(ANSWER_CORRECT+Question.answersGrouping(q.getCorrectAnswers()));
			    writer.println("===========================================================================");
			}
			writer.close();
			return true;
		} catch (IOException e) {
			log.error("exportQuestions", e);
			return false;
		}
	}

	/**
	 * This method imports Questions into ArrayList which then proceed into database.
	 * 
	 * @param fileName File name from witch data are read.
	 * @see FileNotFoundException
	 * @see IOException
	 * @see SQLException
	 * @return true if file was imported, false if file is empty
	 */
	public boolean importQuestion(String fileName) throws FileNotFoundException, IOException, SQLException {
		log.info(fileName);
		
		ArrayList<Question> alq = new ArrayList<>();
		alq.clear();
		String tmp;
		
		try (BufferedReader fin = new BufferedReader(new FileReader(fileName))) {
			tmp = fin.readLine();
			
			if(tmp == null){
				return false;
			}
			
			while (tmp != null) {
				tmp = tmp.replace("\uFEFF", "");
					
				     int id=Integer.parseInt(tmp.substring(3));
					 //
					 tmp = fin.readLine();
					 String QBundle=tmp.substring(16);
				     //
					 tmp = fin.readLine();
					 int idQ=Integer.parseInt(tmp.substring(12));
				     //
					 tmp = fin.readLine();
					 String QText=tmp.substring(14);
				     // 
					 tmp = fin.readLine();
					 String QType=tmp.substring(14);
				     //
					 tmp = fin.readLine();
					 String str=tmp.substring(16);
					 ArrayList<String> answersVar=new ArrayList<String>();
					 answersVar.add(str);
				     //
					 tmp = fin.readLine();
					 String str1=tmp.substring(15);
					 ArrayList<String> correctAnswers=new ArrayList<String>();
					 correctAnswers.add(str1);
					 //
					 alq.add(new Question(id, QBundle, idQ, QText, QType, answersVar, correctAnswers));
					 tmp = fin.readLine();
					 tmp = fin.readLine();
			}
			DBAdapter.QuestionTableAdapter.clearQuestionTable();
		} catch (IOException e) {
			log.error("importQuestion", e);
		}
		for(int i=0;i<alq.size();i++) {
			DBAdapter.QuestionTableAdapter.insertQuestion(alq.get(i));
		}
		return true;
	}

//	public static void main (String[] args) throws FileNotFoundException, IOException, SQLException {
//		FileAdapter util = new FileAdapter();
//		//util.exportQuestions("question.txt");
//		util.importQuestion("all_questionsWORKING(1).txt");
//		
//	}
}
