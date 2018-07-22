package bootcamp.kcv2.util;

public class DBContract {

	public static final class QuestionTable {

		public static final String DATA_TABLE = "Question";
		public static final String ID_KEY = "id";
		public static final String QUESTION_BUNDULE_KEY = "questionBundule";
		public static final String QUESTION_ID_KEY = "questionId";
		public static final String QUESTION_TEXT_KEY = "questionText";
		public static final String QUESTION_TYPE_KEY = "questionType";
		public static final String ANSWERS_VAR_KEY = "answersVar";
		public static final String ANSWERS_COR_KEY = "answersCor";

	}

	public static final class ResultTable {

		public static final String DATA_TABLE = "Result";
		public static final String ID_KEY = "id";
		public static final String USER_CODE_KEY = "userCode";
		public static final String QUESTION_BUNDULE_KEY = "questionBundule";
		public static final String QUESTION_ID_KEY = "questionId";
		public static final String ANSWER_KEY = "answer";
		public static final String IS_CORRECT_KEY = "isCorrect";

	}
	
	public static final class StudentTable {
		public static final String DATA_BASE = "Student";
		public static final String ID_KEY = "id";
		public static final String NAME_KEY = "name";
		public static final String SURNAME_KEY = "surname";
		public static final String CODE_KEY = "code";
	}

}
