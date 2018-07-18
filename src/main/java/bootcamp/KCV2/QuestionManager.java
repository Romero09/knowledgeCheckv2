package bootcamp.KCV2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class QuestionManager {

	private static final String DATA_BASE = "kcv2.Question";
	private static final String ID_KEY = "id";
	private static final String SET_KEY = "qSet";
	private static final String SETID_KEY = "setId";
	private static final String QUESTIONTEXT_KEY = "questionText";
	private static final String QUESTIONTYPE_KEY = "questionType";
	private static final String ANSWERSVAR_KEY = "answersVar";
	private static final String ANSWERSCOR_KEY = "answersCor";

	protected Connection conn;

	public QuestionManager() {

		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/?autoReconnect=true&useSSL=false", "root",
					"abcd1234");
			conn.setAutoCommit(false);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	//Returns Question object searched by SET and SETID
	public Question findQuestion(String set, int setID) {
		// find student by id
		String query = "SELECT * FROM " + DATA_BASE + " WHERE `" + SET_KEY + "` = ? AND `" + SETID_KEY + "` = ?";
		PreparedStatement preparedStatement;
		try {
			preparedStatement = conn.prepareStatement(query);
			preparedStatement.setString(1, set);
			preparedStatement.setInt(2, setID);
			ResultSet rs = preparedStatement.executeQuery();
			conn.commit();
			// if no such question
			if (!rs.isBeforeFirst()) {
				return null;
			}
			while (rs.next()) {
				int idQ = rs.getInt(ID_KEY);
				String setQ = rs.getString(SET_KEY);
				int setIdQ = rs.getInt(SETID_KEY);
				String questionTextQ = rs.getString(QUESTIONTEXT_KEY);
				String questionTypeQ = rs.getString(QUESTIONTYPE_KEY);
				ArrayList<String> answersVar = answersSpliter(rs.getString(ANSWERSVAR_KEY));
				ArrayList<String> answersCor = answersSpliter(rs.getString(ANSWERSCOR_KEY));
				return new Question(idQ, setQ, setIdQ, questionTextQ, questionTypeQ, answersVar, answersCor);
			}
		} catch (SQLException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	//Updates Question object in DB by ID
	public boolean updateQuestion(Question question) {
		boolean status = false;
		String query = "UPDATE " + DATA_BASE + " SET `"+SET_KEY+"` = ?, `"+SETID_KEY+"` = ?,"
				+ " `"+QUESTIONTEXT_KEY+"` = ?, `"+QUESTIONTYPE_KEY+"` = ?,"
						+ " `"+ANSWERSVAR_KEY+"` = ?, `"+ANSWERSCOR_KEY+"` = ? WHERE `"+ID_KEY+"` = ?";

		PreparedStatement preparedStatement;
		try {
			preparedStatement = conn.prepareStatement(query);
			preparedStatement.setString(1, question.getSet());
			preparedStatement.setInt(2, question.getSetId());
			preparedStatement.setString(3, question.getQuestionText());
			preparedStatement.setString(4, question.getQuestionType());
			preparedStatement.setString(5, answersGrouping(question.getAnswersVar()));
			preparedStatement.setString(6, answersGrouping(question.getCorrectAnswers()));
			preparedStatement.setInt(7, question.getId());

			int updatedRows = preparedStatement.executeUpdate();
			conn.commit();
			if (updatedRows > 0)
				status = true;
		} catch (SQLException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		return status;
	}
	
	//Deletes question form DB
	public boolean deleteQuestion(int id) {
		String query = "DELETE FROM " + DATA_BASE + " WHERE `"+ID_KEY+"` = ?";
		
		try {
			PreparedStatement preparedStatement;
			preparedStatement = conn.prepareStatement(query);
			preparedStatement.setInt(1, id);
			int deletedRows = preparedStatement.executeUpdate();
			conn.commit();

			if (deletedRows > 0) {
				return true;
			}
		} catch (SQLException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	//Inserts new Question in DB with new ID(objects ID is ignored)
	public boolean insertQuestion(Question question) {

		String query = "INSERT INTO " + DATA_BASE + " ("+SET_KEY+", "+SETID_KEY+", "+QUESTIONTEXT_KEY+", "+QUESTIONTYPE_KEY+", "
				+ ""+ANSWERSVAR_KEY+", "+ANSWERSCOR_KEY+") VALUES (?,?,?,?,?,?)";
		PreparedStatement preparedStatement;
		try {
			preparedStatement = conn.prepareStatement(query);
			preparedStatement.setString(1, question.getSet());
			preparedStatement.setInt(2, question.getSetId());
			preparedStatement.setString(3, question.getQuestionText());
			preparedStatement.setString(4, question.getQuestionType());
			preparedStatement.setString(5, answersGrouping(question.getAnswersVar()));
			preparedStatement.setString(6, answersGrouping(question.getCorrectAnswers()));
			int insertedRow = preparedStatement.executeUpdate();
			conn.commit();
			if (insertedRow > 0) {
				return true;
			}
		} catch (SQLException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	
	
	//Grouping from array to String to store in DB separating it with delimiter
	public String answersGrouping(ArrayList<String> answersList ){
		if(answersList == null){
			return null;
		}
		String answers = "";
		for(String a : answersList){
			if(a == answersList.get(answersList.size() - 1)) {
				answers = answers + a;
				return answers;
			  }
			answers = answers + a + "; ";
		}
		return answers;
	}
	
	//Splits answers by selected delimiter for answers var and correct answers.
	public ArrayList<String> answersSpliter(String answers) {
		ArrayList<String> answersList = new ArrayList<String>();
		if(answers == null){
			return null;
		}
		if (answers.contains(";")) {
			String[] parts = answers.split("; ");
			for (int i = 0; i < parts.length; i++) {
				answersList.add(parts[i]);
			}
			return answersList;
		} else {
			answersList.add(answers);
			return answersList;
		}
	}

	
	
	
	public static void main(String args[]) {

		QuestionManager manager = new QuestionManager();

		Question question = manager.findQuestion("SQL", 1);

		manager.deleteQuestion(15);


			System.out.println(question.getAnswersVar());
			
			System.out.println(manager.answersGrouping(question.getAnswersVar()));	
		
	}
}
