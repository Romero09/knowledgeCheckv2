package bootcamp.kcv2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;




public class FileAdapter  {
	private static final String ANSWER_VARIANT = "ANSWER VARIANTS:";
	private static final String ANSWER_CORRECT = "ANSWER CORRECT:";
	private static final String QUESTION_TYPE = "QUESTION TYPE:";
	private static final String QUESTION_TEXT = "QUESTION TEXT:";
	private static final String QUESTION_ID = "QUESTION ID:";
	private static final String QUESTION_BUNDLE= "QUESTION BUNDULE:";
	private static final String ID = "ID:";
	
	
	
	/**
	 * Export Question from Database
	 * 
	 * @return 0 if successful, 1 if error
	 * @param questionBundle name of question bundle to save
	 * @param fileName File name where the questions are saved
	 * 
	 */
	public ArrayList<Question> exportQuestions( String fileName ,ArrayList<Question> question) {
		ArrayList<Question> alq = new ArrayList<>();
		// TODO questionBundule hardcoded
		alq = QuestionManager.qmSingleton.pullQuestionBundle("SQL");

		try {
			PrintWriter writer = new PrintWriter(fileName, "UTF-8");
			File file = new File(fileName);

			for (Question q : alq) {
				System.err.println(q);
				writer.println(ID+q.getId());
			    writer.println(QUESTION_BUNDLE+q.getSet());
			    writer.println(QUESTION_ID+q.getSetId());
			    writer.println(QUESTION_TEXT+q.getQuestionText());
			    writer.println(QUESTION_TYPE+q.getQuestionType());
			    writer.println(ANSWER_VARIANT+q.getAnswersVar());
			    writer.println(ANSWER_CORRECT+q.getCorrectAnswers());
			    writer.println("===========================================================================");
			 
			}
			writer.close();
			String path = file.getAbsolutePath();
			return alq;

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}


	/**Import Question into Database
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws SQLException
	 */

	public ArrayList<Question> importQuestion(String fileName) throws FileNotFoundException, IOException, SQLException {
		ArrayList<String> alq = new ArrayList<String>();
		String tmp;
		String str;
		int i=0;
		try (BufferedReader fin = new BufferedReader(new FileReader(fileName))) {
			tmp = fin.readLine();
			
			while (tmp != null) {
				if(i==0) {
					 
					 str=tmp.substring(3);
					 System.out.println(str);
					 alq.add(str);
				}
				if(i==1) {
					 
					 str=tmp.substring(17);
					 System.out.println(str);
					 alq.add(str);
				}
				if(i==2) {
					
					str=tmp.substring(12);
					System.out.println(str);
					alq.add(str);
				}
				if(i==3) {
					
					str=tmp.substring(14);
					System.out.println(str);
					alq.add(str);
				}
				if(i==4) {
					
					str=tmp.substring(14);
					System.out.println(str);
					alq.add(str);
				}
				if(i==5) {
					 
					str=tmp.substring(14);
					System.out.println(str);
					alq.add(str);
				}
				if(i==6) {
					
					str=tmp.substring(15);
					System.out.println(str);
					alq.add(str);
				}
				if(i==7) {
					tmp = fin.readLine();
					
					i=0;
				}
				i++;
				tmp = fin.readLine();
			}
			

		} catch (IOException e) {
			System.err.println("Error I/O" + e);
			e.printStackTrace();
			
		}
		//System.err.println(alq);
		return alq;

	}

	public static void main (String[] args) throws FileNotFoundException, IOException, SQLException {
		FileAdapter util = new FileAdapter();
		util.importQuestion("question.txt");
	}
	
}
