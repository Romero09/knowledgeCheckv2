package bootcamp.kcv2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class QuestionManager {

	private static final String DATA_TABLE = "kcv2.Question";
	private static final String ID_KEY = "id";
	private static final String QUESTION_BUNDULE_KEY = "questionBundule";
	private static final String QUESTION_ID_KEY = "questionId";
	private static final String QUESTION_TEXT_KEY = "questionText";
	private static final String QUESTION_TYPE_KEY = "questionType";
	private static final String ANSWERS_VAR_KEY = "answersVar";
	private static final String ANSWERS_COR_KEY = "answersCor";
	private static final String SEPARATOR = "; ";

	protected Connection conn;

	public static QuestionManager qmSingleton = new QuestionManager();

	private ArrayList<StudentAnswerSheet> answers = new ArrayList<>();
	private String currentQuestionBundle = "SQL";

	/**
	 * @return the currentQuestionBundle
	 */
	public String getCurrentQuestionBundle() {
		return currentQuestionBundle;
	}

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

	public static QuestionManager getInstance() {
		return qmSingleton;
	}

	/**
	 * 
	 * @param userCode
	 * @param answers
	 *            - Answers list that was made by student
	 * @return
	 */
	public void submitResults(String userCode, ArrayList<String> answers, ArrayList<Question> alQuestions) {

		answers.remove(0);

		System.err.println("QuestionManager: Submitted results:\n\t" + "User=" + userCode + " Answers=" + answers);

		restultsCheck(userCode, answers, alQuestions);

		// answers.get(userCode)

		// parse this sheet
		// return percentange;
	}

	/**
	 * Generates a list of Questions to be displayed by ServerController (parsed
	 * and later HTML is made)
	 * 
	 * @param userCode
	 * @return
	 */
	public ArrayList<Question> getQuestionBundle(String userCode) {
		// TODO: add check if testing for this users already started
		// for (StudentAnswerSheet studentAnswerSheet : answers) {
		// if (studentAnswerSheet.getStudentCode().equals(userCode)) {
		// System.err.println("User has already participated.");
		// return null;
		// }
		// }
		StudentAnswerSheet as = new StudentAnswerSheet();
		as.setStudentCode(userCode);
		as.setQuestionBundleName(qmSingleton.currentQuestionBundle);
		answers.add(as);

		ArrayList<Question> alq = new ArrayList<>();
		alq = pullQuestionBundle(qmSingleton.currentQuestionBundle);

		return alq;
	}


	// Returns Question object searched by SET
	public ArrayList<Question> pullQuestionBundle(String currentQuestionBundle) {

		ArrayList<Question> alq = new ArrayList<>();
		String query = "SELECT * FROM " + DATA_TABLE + " WHERE `" + QUESTION_BUNDULE_KEY + "` = ?";
		PreparedStatement preparedStatement;
		try {
			preparedStatement = conn.prepareStatement(query);
			preparedStatement.setString(1, currentQuestionBundle);
			ResultSet rs = preparedStatement.executeQuery();
			conn.commit();
			// if no such question
			if (!rs.isBeforeFirst()) {
				return null;
			}
			while (rs.next()) {
				int idQ = rs.getInt(ID_KEY);
				String setQ = rs.getString(QUESTION_BUNDULE_KEY);
				int setIdQ = rs.getInt(QUESTION_ID_KEY);
				String questionTextQ = rs.getString(QUESTION_TEXT_KEY);
				String questionTypeQ = rs.getString(QUESTION_TYPE_KEY);
				ArrayList<String> answersVar = answersSpliter(rs.getString(ANSWERS_VAR_KEY));
				ArrayList<String> answersCor = answersSpliter(rs.getString(ANSWERS_COR_KEY));
				alq.add(new Question(idQ, setQ, setIdQ, questionTextQ, questionTypeQ, answersVar, answersCor));
			}
		} catch (SQLException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		return alq;
	}

	// Updates Question object in DB by ID
	public boolean updateQuestion(Question question) {
		boolean status = false;
		String query = "UPDATE " + DATA_TABLE + " SET `" + QUESTION_BUNDULE_KEY + "` = ?, `" + QUESTION_ID_KEY
				+ "` = ?," + " `" + QUESTION_TEXT_KEY + "` = ?, `" + QUESTION_TYPE_KEY + "` = ?," + " `"
				+ ANSWERS_VAR_KEY + "` = ?, `" + ANSWERS_COR_KEY + "` = ? WHERE `" + ID_KEY + "` = ?";

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

	// Deletes question form DB
	public boolean deleteQuestion(int id) {
		String query = "DELETE FROM " + DATA_TABLE + " WHERE `" + ID_KEY + "` = ?";

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

	// Inserts new Question in DB with new ID(objects ID is ignored)
	public boolean insertQuestion(Question question) {

		String query = "INSERT INTO " + DATA_TABLE + " (" + QUESTION_BUNDULE_KEY + ", " + QUESTION_ID_KEY + ", "
				+ QUESTION_TEXT_KEY + ", " + QUESTION_TYPE_KEY + ", " + "" + ANSWERS_VAR_KEY + ", " + ANSWERS_COR_KEY
				+ ") VALUES (?,?,?,?,?,?)";
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

	// Grouping from array to String to store in DB separating it with delimiter
	public String answersGrouping(ArrayList<String> answersList) {
		if (answersList == null) {
			return null;
		}
		String answers = "";
		for (String a : answersList) {
			if (a == answersList.get(answersList.size() - 1)) {
				answers = answers + a;
				return answers;
			}
			answers = answers + a + SEPARATOR;
		}
		return answers;
	}

	// Splits answers by selected delimiter for answers var and correct answers.
	public ArrayList<String> answersSpliter(String answers) {
		ArrayList<String> answersList = new ArrayList<String>();
		if (answers == null) {
			return null;
		}
		if (answers.contains(";")) {
			String[] parts = answers.split(SEPARATOR);
			for (int i = 0; i < parts.length; i++) {
				answersList.add(parts[i]);
			}
			return answersList;
		} else {
			answersList.add(answers);
			return answersList;
		}
	}

	public float restultsCheck(String userCode, ArrayList<String> answers, ArrayList<Question> alQuestions) {

		ArrayList<Integer> correctAnswers = new ArrayList<>();
		int totalQuestions = 0;
		int correctAnswersCount = 0;

		if (answers.size() != alQuestions.size()) {
			System.err.println("Something went wrong Answers array size doesent match Question array size");
		}

		for (int i = 0; i < answers.size(); i++) {
			totalQuestions++;

			if (answers.get(i).equals(answersGrouping(alQuestions.get(i).getCorrectAnswers()))) {
				correctAnswers.add(1); // Correct - True
				correctAnswersCount++;
			} else {
				correctAnswers.add(0); // Incorrect - False
			}
		}
		
		ResultManager rm = new ResultManager();
		Result result = new Result(userCode, currentQuestionBundle, answers, correctAnswers);
		rm.insertQuestion(result);
		
		return correctAnswersCount / totalQuestions;
	}
}
