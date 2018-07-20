package bootcamp.kcv2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import bootcamp.kcv2.util.BaseConfiguration;

public class ResultManager {

	private static final String DATA_TABLE = "Result";
	private static final String ID_KEY = "id";
	private static final String USER_CODE_KEY = "userCode";
	private static final String QUESTION_BUNDULE_KEY = "questionBundule";
	private static final String QUESTION_ID_KEY = "questionId";
	private static final String ANSWER_KEY = "answer";
	private static final String IS_CORRECT_KEY = "isCorrect";

	protected Connection conn;

	public ResultManager() {

		try {
			Class.forName("com.mysql.jdbc.Driver");
			String connectionUrl = "jdbc:mysql://" + BaseConfiguration.DB_SERVER +":"+ BaseConfiguration.DB_PORT + "/" + BaseConfiguration.DB_NAME
					+ "?autoReconnect=true&useSSL=false";
			System.err.println(connectionUrl);
			conn = DriverManager.getConnection(connectionUrl, BaseConfiguration.DB_USER, BaseConfiguration.DB_PASSWORD);
			conn.setAutoCommit(false);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	// Inserts new Results in Result table
	public boolean insertQuestion(Result result) {
		boolean status = false;

		String query = "INSERT INTO " + DATA_TABLE + " (" + USER_CODE_KEY + ", " + QUESTION_BUNDULE_KEY + ", "
				+ QUESTION_ID_KEY + ", " + ANSWER_KEY + ", " + IS_CORRECT_KEY + ") VALUES (?,?,?,?,?)";

		for (int i = 0; i < result.getIsCorrect().size(); i++) {
			PreparedStatement preparedStatement;
			try {
				preparedStatement = conn.prepareStatement(query);
				preparedStatement.setString(1, result.getUserCode());
				preparedStatement.setString(2, result.getQuestionBundule());
				preparedStatement.setInt(3, i+1);
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

}
