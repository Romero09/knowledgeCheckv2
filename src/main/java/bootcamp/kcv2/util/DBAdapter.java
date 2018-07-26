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

/**
 * This class helps to manage Database.
 */
public class DBAdapter {

	protected static Connection conn;
	public static final Logger log = Logger.getLogger(DBAdapter.class);
	public static final DBAdapter dbAdapter = new DBAdapter();

	/**
	 * Establishes a connection to MySQL database.
	 *
	 * @see ClassNotFoundException
	 * @see SQLException
	 */
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

	/**
	 * This class helps to manage Questions in Database.
	 */
	public static final class QuestionTableAdapter {

		private QuestionTableAdapter() {
		}

		/**
		 * This method returns question object searched by SET in MySQL
		 * database.
		 *
		 * @param currentQuestionBundle
		 *            - question bundle that currently is used
		 * @return null if no such question in Question table was found,
		 *         otherwise returns ArrayList of object Question if question
		 *         was found
		 * @see SQLException
		 */
		public static ArrayList<Question> pullQuestionBundle(String currentQuestionBundle) {

			ArrayList<Question> alq = new ArrayList<>();
			String query = "SELECT * FROM " + QuestionTable.DATA_TABLE + " WHERE `" + QuestionTable.QUESTION_BUNDLE_KEY
					+ "` LIKE ?";
			try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
				preparedStatement.setString(1, currentQuestionBundle);
				try (ResultSet rs = preparedStatement.executeQuery()) {
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
						ArrayList<String> answersVar = Question
								.answersSpliter(rs.getString(QuestionTable.ANSWERS_VAR_KEY));
						ArrayList<String> answersCor = Question
								.answersSpliter(rs.getString(QuestionTable.ANSWERS_COR_KEY));
						alq.add(new Question(idQ, setQ, setIdQ, questionTextQ, questionTypeQ, answersVar, answersCor));
					}
				}
			} catch (SQLException e) {
				log.error("pullQuestionBundle", e);
			}
			return alq;
		}

		/**
		 * This method pulls question theme from MySQL database.
		 *
		 * @return bundle names for question
		 * @see SQLException
		 */
		public static ArrayList<String> pullBundleNames() {
			String query = "SELECT DISTINCT (" + QuestionTable.QUESTION_BUNDLE_KEY + ") " + "FROM "
					+ QuestionTable.DATA_TABLE;
			ArrayList<String> result = new ArrayList<>();

			try (Statement st = conn.createStatement()) {
				try (ResultSet rs = st.executeQuery(query)) {
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

		/**
		 * This method inserts new question in database MySQL with new
		 * ID(objects ID is ignored).
		 *
		 * @param question
		 *            object with parameters
		 * @return true if question in database was added, false if wasn't
		 * @see SQLException
		 */
		public static boolean insertQuestion(Question question) {

			String query = "INSERT INTO " + QuestionTable.DATA_TABLE + " (" + QuestionTable.QUESTION_BUNDLE_KEY + ", "
					+ QuestionTable.QUESTION_ID_KEY + ", " + QuestionTable.QUESTION_TEXT_KEY + ", "
					+ QuestionTable.QUESTION_TYPE_KEY + ", " + "" + QuestionTable.ANSWERS_VAR_KEY + ", "
					+ QuestionTable.ANSWERS_COR_KEY + ") VALUES (?,?,?,?,?,?)";
			try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
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

		/**
		 * This method clears(deletes) Question Table in MySQL database.
		 *
		 * @return true if table was deleted, false if wasn't
		 * @see SQLException
		 */
		public static boolean clearQuestionTable() {
			String query = "DELETE FROM " + QuestionTable.DATA_TABLE;

			try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
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

	/**
	 * This class helps to manage Results in Database.
	 */
	public static final class ResultTableAdapter {

		private ResultTableAdapter() {

		}

		/**
		 * This method inserts students results in Result table.
		 *
		 * @param result
		 *            object with student results
		 * @return true if result was inserted , false if wasn't
		 * @see SQLException
		 */
		public static boolean insertResult(Result result) {
			boolean status = false;

			String query = "INSERT INTO " + ResultTable.DATA_TABLE + " (" + ResultTable.USER_CODE_KEY + ", "
					+ ResultTable.QUESTION_BUNDLE_KEY + ", " + ResultTable.QUESTION_ID_KEY + ", "
					+ ResultTable.ANSWER_KEY + ", " + ResultTable.IS_CORRECT_KEY + ") VALUES (?,?,?,?,?)";

			for (int i = 0; i < result.getIsCorrect().size(); i++) {
				try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
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

		/**
		 * This method clears(deletes) Results in MySQL Database.
		 *
		 * @return true if table was deleted, false if wasn't
		 * @see SQLException
		 */
		public static boolean clearResultTable() {
			String query = "DELETE FROM " + ResultTable.DATA_TABLE;

			try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
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

		/**
		 * This method pulls Result from MySQL Database.
		 *
		 * @param bundleName
		 *            -question set for exam.
		 * @return List with results
		 * @see SQLException
		 */
		public static ResultSet pullResultsBundle(String bundleName) {

			String query = "select " + "Result.userCode, " + "Result.questionId, " + "Question.questionText, "
					+ "Question.answersCor, " + "Result.answer, " + "Result.isCorrect "
					+ "from Result INNER JOIN Question \n" + "ON "
					+ "(Question.questionBundle=Result.questionBundle AND Question.questionId=Result.questionId) "
					+ "WHERE " + "Result.questionBundle='" + bundleName + "' ORDER BY "
					+ "Result.userCode, Result.questionBundle, Result.questionId ASC;";
			log.info(query);
			try {
				PreparedStatement preparedStatement = conn.prepareStatement(query);
				ResultSet rs = preparedStatement.executeQuery();
				conn.commit();
				return rs;
			} catch (SQLException e) {
				log.error("pullResultsBundle", e);
			}
			assert true;
			return null;
		}
	}
}