package bootcamp.kcv2.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import bootcamp.kcv2.util.DBContract.QuestionTable;
import bootcamp.kcv2.util.DBContract.ResultTable;
import bootcamp.kcv2.util.DBContract.StudentTable;
import bootcamp.kcv2.Question;
import bootcamp.kcv2.Result;
import bootcamp.kcv2.Student;

public class DBAdapter {

	protected static Connection conn;
	public static final DBAdapter dbAdapter = new DBAdapter();
	final Logger log = Logger.getLogger(DBAdapter.class);

	private DBAdapter() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String connectionUrl = "jdbc:mysql://" + BaseConfiguration.DB_SERVER + ":" + BaseConfiguration.DB_PORT + "/"
					+ BaseConfiguration.DB_NAME + "?autoReconnect=true&useSSL=false";
			log.info(connectionUrl);
			conn = DriverManager.getConnection(connectionUrl, BaseConfiguration.DB_USER, BaseConfiguration.DB_PASSWORD);
			conn.setAutoCommit(false);
		} catch (ClassNotFoundException | SQLException e) {
			log.error("DB connection", e);
		}
	}

	public static final class QuestionTableAdapter {
		
		final Logger log = Logger.getLogger(DBAdapter.class);
		
		private QuestionTableAdapter(){
			
		}

		// Returns Question object searched by SET
		public static ArrayList<Question> pullQuestionBundle(String currentQuestionBundle) {

			ArrayList<Question> alq = new ArrayList<>();
			String query = "SELECT * FROM " + QuestionTable.DATA_TABLE + " WHERE `" + QuestionTable.QUESTION_BUNDULE_KEY
					+ "` LIKE ?";
			try(PreparedStatement preparedStatement = conn.prepareStatement(query)) {
				preparedStatement.setString(1, currentQuestionBundle);
				try (ResultSet rs = preparedStatement.executeQuery()){
				conn.commit();
				// if no such question
				if (!rs.isBeforeFirst()) {
					return alq;
				}
				while (rs.next()) {
					int idQ = rs.getInt(QuestionTable.ID_KEY);
					String setQ = rs.getString(QuestionTable.QUESTION_BUNDULE_KEY);
					int setIdQ = rs.getInt(QuestionTable.QUESTION_ID_KEY);
					String questionTextQ = rs.getString(QuestionTable.QUESTION_TEXT_KEY);
					String questionTypeQ = rs.getString(QuestionTable.QUESTION_TYPE_KEY);
					ArrayList<String> answersVar = Question.answersSpliter(rs.getString(QuestionTable.ANSWERS_VAR_KEY));
					ArrayList<String> answersCor = Question.answersSpliter(rs.getString(QuestionTable.ANSWERS_COR_KEY));
					alq.add(new Question(idQ, setQ, setIdQ, questionTextQ, questionTypeQ, answersVar, answersCor));
				}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return alq;
		}

		public static ArrayList<String> pullBundleNames() {
			String query = "SELECT DISTINCT (" + QuestionTable.QUESTION_BUNDULE_KEY + ") " + "FROM "
					+ QuestionTable.DATA_TABLE;
			ArrayList<String> result = new ArrayList<>();

			try(Statement st = conn.createStatement()) {
				try(ResultSet rs = st.executeQuery(query)){
				conn.commit();
				while (rs.next()) {
					String bundleName = rs.getString(QuestionTable.QUESTION_BUNDULE_KEY);
					if (bundleName != null) {
						result.add(rs.getString(QuestionTable.QUESTION_BUNDULE_KEY));
					}
				}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return result;
		}

		// Updates Question object in DB by ID
		public static boolean updateQuestion(Question question) {
			boolean status = false;
			String query = "UPDATE " + QuestionTable.DATA_TABLE + " SET `" + QuestionTable.QUESTION_BUNDULE_KEY
					+ "` = ?, `" + QuestionTable.QUESTION_ID_KEY + "` = ?," + " `" + QuestionTable.QUESTION_TEXT_KEY
					+ "` = ?, `" + QuestionTable.QUESTION_TYPE_KEY + "` = ?," + " `" + QuestionTable.ANSWERS_VAR_KEY
					+ "` = ?, `" + QuestionTable.ANSWERS_COR_KEY + "` = ? WHERE `" + QuestionTable.ID_KEY + "` = ?";

			try(PreparedStatement preparedStatement = conn.prepareStatement(query)) {
				preparedStatement.setString(1, question.getSet());
				preparedStatement.setInt(2, question.getSetId());
				preparedStatement.setString(3, question.getQuestionText());
				preparedStatement.setString(4, question.getQuestionType().toString());
				preparedStatement.setString(5, Question.answersGrouping(question.getAnswersVar()));
				preparedStatement.setString(6, Question.answersGrouping(question.getCorrectAnswers()));
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

		// Deletes question form DB
		public static boolean deleteQuestion(int id) {
			String query = "DELETE FROM " + QuestionTable.DATA_TABLE + " WHERE `" + QuestionTable.ID_KEY + "` = ?";

			try(PreparedStatement preparedStatement = conn.prepareStatement(query)) {
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

		// Inserts new Question in DB with new ID(objects ID is ignored)
		public static boolean insertQuestion(Question question) {

			String query = "INSERT INTO " + QuestionTable.DATA_TABLE + " (" + QuestionTable.QUESTION_BUNDULE_KEY + ", "
					+ QuestionTable.QUESTION_ID_KEY + ", " + QuestionTable.QUESTION_TEXT_KEY + ", "
					+ QuestionTable.QUESTION_TYPE_KEY + ", " + "" + QuestionTable.ANSWERS_VAR_KEY + ", "
					+ QuestionTable.ANSWERS_COR_KEY + ") VALUES (?,?,?,?,?,?)";
			try(PreparedStatement preparedStatement = conn.prepareStatement(query)) {
				preparedStatement.setString(1, question.getSet());
				preparedStatement.setInt(2, question.getSetId());
				preparedStatement.setString(3, question.getQuestionText());
				preparedStatement.setString(4, question.getQuestionType().toString());
				preparedStatement.setString(5, Question.answersGrouping(question.getAnswersVar()));
				preparedStatement.setString(6, Question.answersGrouping(question.getCorrectAnswers()));
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
		
		// Deletes results form DB
		public static boolean clearQuestionTable() {
			String query = "DELETE FROM " + QuestionTable.DATA_TABLE;

			try(PreparedStatement preparedStatement = conn.prepareStatement(query)) {
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
		
	}

	public static final class ResultTableAdapter {
		
		private ResultTableAdapter(){
			
		}

		// Inserts new Results in Result table
		public static boolean insertQuestion(Result result) {
			boolean status = false;

			String query = "INSERT INTO " + ResultTable.DATA_TABLE + " (" + ResultTable.USER_CODE_KEY + ", "
					+ ResultTable.QUESTION_BUNDULE_KEY + ", " + ResultTable.QUESTION_ID_KEY + ", "
					+ ResultTable.ANSWER_KEY + ", " + ResultTable.IS_CORRECT_KEY + ") VALUES (?,?,?,?,?)";

			for (int i = 0; i < result.getIsCorrect().size(); i++) {
				try(PreparedStatement preparedStatement = conn.prepareStatement(query)) {
					preparedStatement.setString(1, result.getUserCode());
					preparedStatement.setString(2, result.getQuestionBundule());
					preparedStatement.setInt(3, i + 1);
					preparedStatement.setString(4, result.getAnswer().get(i));
					preparedStatement.setInt(5, result.getIsCorrect().get(i));
					int insertedRow = preparedStatement.executeUpdate();
					conn.commit();
					if (insertedRow > 0) {
						status = true;
					}
				} catch (SQLException e) {
					// Auto-generated catch block
					e.printStackTrace();
				}
			}
			return status;
		}
		
		// Deletes results form DB
		public static boolean clearResultTable() {
			String query = "DELETE FROM " + ResultTable.DATA_TABLE;

			try(PreparedStatement preparedStatement = conn.prepareStatement(query)) {
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
		
		
		public static ArrayList<String> pullResultsBundle(String bundleName){
			
			ArrayList<ArrayList<String>> resultList = new ArrayList<>();
			
			String query = "SELECT * FROM " + ResultTable.DATA_TABLE + " WHERE `" + ResultTable.QUESTION_BUNDULE_KEY + "` = ?";
			
			try(PreparedStatement preparedStatement = conn.prepareStatement(query)) {
				preparedStatement.setString(1, bundleName);
				
				try (ResultSet rs = preparedStatement.executeQuery()){
					conn.commit();
					// if no such question
					if (!rs.isBeforeFirst()) {
						return Result.resultList(resultList);
					}
					while (rs.next()) {
						ArrayList<String> oneResult = new ArrayList<>();
						oneResult.add(rs.getString(ResultTable.USER_CODE_KEY));
						oneResult.add(String.valueOf(rs.getInt(ResultTable.IS_CORRECT_KEY)));
						resultList.add(oneResult);
					}
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return Result.resultList(resultList);
				
		}

		public static void main(String[] args){
			System.out.println(pullResultsBundle("Knowledge Check 4"));
		}
		
	}

	public static final class StudentTableAdapter {
		
		private StudentTableAdapter(){
			
		}

		public static Student findStudent(int id) {
			// find student by id
			String query = "SELECT * FROM " + StudentTable.DATA_TABLE + " WHERE `" + StudentTable.ID_KEY + "` = ?";
			try(PreparedStatement preparedStatement = conn.prepareStatement(query)) {
				preparedStatement.setInt(1, id);
				try (ResultSet rs = preparedStatement.executeQuery()){
				conn.commit();
				if (!rs.isBeforeFirst()) {
					return new Student(0, null, null, null);
				}
				while (rs.next()) {
					String firstName = rs.getString(StudentTable.NAME_KEY);
					String lastName = rs.getString(StudentTable.SURNAME_KEY);
					String code = rs.getString("code");
					return new Student(id, code, firstName, lastName);
				}
				}
			} catch (SQLException e) {
				// Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		public static List<Student> findStudent(String firstName, String lastName) {
			try {
				conn.setAutoCommit(false);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			firstName = "%" + firstName + "%";
			lastName = "%" + lastName + "%";

			List<Student> studentList = new ArrayList<>();

			String query = "SELECT * FROM " + StudentTable.DATA_TABLE + " WHERE `" + StudentTable.NAME_KEY
					+ "` LIKE ? AND `" + StudentTable.SURNAME_KEY + "` LIKE ?";

			try(PreparedStatement preparedStatement = conn.prepareStatement(query)) {
				preparedStatement.setString(1, firstName);
				preparedStatement.setString(2, lastName);
				try(ResultSet rs = preparedStatement.executeQuery()){
				conn.commit();

				if (!rs.isBeforeFirst()) {
					return studentList;
				}
				while (rs.next()) {
					String first = rs.getString(StudentTable.NAME_KEY);
					String last = rs.getString(StudentTable.SURNAME_KEY);
					String code = rs.getString(StudentTable.CODE_KEY);
					int id = rs.getInt(StudentTable.ID_KEY);

					studentList.add(new Student(id, code, first, last));
				}
				}

			} catch (SQLException e) {
				// Auto-generated catch block
				e.printStackTrace();
			}
			return studentList;
		}

		public static boolean insertStudent(String firstName, String lastName, String code) {

			String query = "SELECT * FROM " + StudentTable.DATA_TABLE + " WHERE `" + StudentTable.CODE_KEY + "` = ?";
			try(PreparedStatement preparedStatement = conn.prepareStatement(query)) {
				preparedStatement.setString(1, code);
				try(ResultSet rs = preparedStatement.executeQuery()){
				conn.commit();
				if (rs.isBeforeFirst()) {
					System.out.println("Such code already exists");

					return false;
				}
				}

			} catch (SQLException e) {
				// Auto-generated catch block
				e.printStackTrace();
			}

			if (code.length() > 4)
				return false;
			if (lastName.length() > 45)
				return false;
			if (firstName.length() > 45)
				return false;

			query = "INSERT INTO " + StudentTable.DATA_TABLE + " (" + StudentTable.NAME_KEY + ", "
					+ StudentTable.SURNAME_KEY + ", " + StudentTable.CODE_KEY + ") VALUES (?,?,?)";

			try(PreparedStatement preparedStatement = conn.prepareStatement(query)) {
				preparedStatement.setString(1, firstName);
				preparedStatement.setString(2, lastName);
				preparedStatement.setString(3, code);
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

		public static boolean updateStudent(Student student) {
			boolean status = false;

			String query = "UPDATE " + StudentTable.DATA_TABLE + " SET `" + StudentTable.NAME_KEY + "` = ?, `"
					+ StudentTable.SURNAME_KEY + "` = ?, `" + StudentTable.CODE_KEY + "` = ? WHERE `"
					+ StudentTable.ID_KEY + "` = ?";

			try(PreparedStatement preparedStatement = conn.prepareStatement(query)) {
				preparedStatement.setString(1, student.getName());
				preparedStatement.setString(2, student.getSurename());
				preparedStatement.setString(3, student.getCode());
				preparedStatement.setInt(4, student.getId());

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

		public static boolean deleteStudent(int id) {
			String query = "DELETE FROM " + StudentTable.DATA_TABLE + " WHERE `" + StudentTable.ID_KEY + "` = ?";

			try(PreparedStatement preparedStatement = conn.prepareStatement(query)) {
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

	}

}
