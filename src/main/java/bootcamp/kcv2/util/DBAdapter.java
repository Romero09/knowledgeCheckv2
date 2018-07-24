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
import bootcamp.kcv2.Question;
import bootcamp.kcv2.Result;

public class DBAdapter {

	protected static Connection conn;
	public static final Logger log = Logger.getLogger(DBAdapter.class);
	public static final DBAdapter dbAdapter = new DBAdapter();
	

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
		
		
		private QuestionTableAdapter(){
			
		}

		// Returns Question object searched by SET
		public static ArrayList<Question> pullQuestionBundle(String currentQuestionBundle) {

			ArrayList<Question> alq = new ArrayList<>();
			String query = "SELECT * FROM " + QuestionTable.DATA_TABLE + " WHERE `" + QuestionTable.QUESTION_BUNDLE_KEY
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
					String setQ = rs.getString(QuestionTable.QUESTION_BUNDLE_KEY);
					int setIdQ = rs.getInt(QuestionTable.QUESTION_ID_KEY);
					String questionTextQ = rs.getString(QuestionTable.QUESTION_TEXT_KEY);
					String questionTypeQ = rs.getString(QuestionTable.QUESTION_TYPE_KEY);
					ArrayList<String> answersVar = Question.answersSpliter(rs.getString(QuestionTable.ANSWERS_VAR_KEY));
					ArrayList<String> answersCor = Question.answersSpliter(rs.getString(QuestionTable.ANSWERS_COR_KEY));
					alq.add(new Question(idQ, setQ, setIdQ, questionTextQ, questionTypeQ, answersVar, answersCor));
				}
				}
			} catch (SQLException e) {
				log.error("pullQuestionBundle", e);
			}
			return alq;
		}

		public static ArrayList<String> pullBundleNames() {
			String query = "SELECT DISTINCT (" + QuestionTable.QUESTION_BUNDLE_KEY + ") " + "FROM "
					+ QuestionTable.DATA_TABLE;
			ArrayList<String> result = new ArrayList<>();

			try(Statement st = conn.createStatement()) {
				try(ResultSet rs = st.executeQuery(query)){
				conn.commit();
				while (rs.next()) {
					String bundleName = rs.getString(QuestionTable.QUESTION_BUNDLE_KEY);
					if (bundleName != null) {
						result.add(rs.getString(QuestionTable.QUESTION_BUNDLE_KEY));
					}
				}
				}
			} catch (SQLException e) {
				log.error("pullBundleNames", e);
			}
			return result;
		}

		// Inserts new Question in DB with new ID(objects ID is ignored)
		public static boolean insertQuestion(Question question) {

			String query = "INSERT INTO " + QuestionTable.DATA_TABLE + " (" + QuestionTable.QUESTION_BUNDLE_KEY + ", "
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
				log.error("insertQuestion", e);
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
				log.error("clearQuestionTable", e);
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
					+ ResultTable.QUESTION_BUNDLE_KEY + ", " + ResultTable.QUESTION_ID_KEY + ", "
					+ ResultTable.ANSWER_KEY + ", " + ResultTable.IS_CORRECT_KEY + ") VALUES (?,?,?,?,?)";

			for (int i = 0; i < result.getIsCorrect().size(); i++) {
				try(PreparedStatement preparedStatement = conn.prepareStatement(query)) {
					preparedStatement.setString(1, result.getUserCode());
					preparedStatement.setString(2, result.getQuestionBundle());
					preparedStatement.setInt(3, i + 1);
					preparedStatement.setString(4, result.getAnswer().get(i));
					preparedStatement.setInt(5, result.getIsCorrect().get(i));
					int insertedRow = preparedStatement.executeUpdate();
					conn.commit();
					if (insertedRow > 0) {
						status = true;
					}
				} catch (SQLException e) {
					log.error("insertQuestion", e);
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
				log.error("clearResultTable", e);
			}
			return false;
		}
		
		
		public static ArrayList<String> pullResultsBundle(String bundleName){
			
			ArrayList<ArrayList<String>> resultList = new ArrayList<>();
			
			String query = "SELECT * FROM " + ResultTable.DATA_TABLE + " WHERE `" + ResultTable.QUESTION_BUNDLE_KEY + "` = ?";
			
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
					log.error("pullResultsBundle", e);
				}
				return Result.resultList(resultList);
				
		}

	}
}
