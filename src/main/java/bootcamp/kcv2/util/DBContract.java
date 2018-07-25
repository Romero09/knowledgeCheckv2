package bootcamp.kcv2.util;
/**
 * This class contains constants which are used in project to get 
 * information about question, results and student.
 */

public class DBContract {
	
	private DBContract(){
		
	}

    /**
     * 
     * Constants for Question Table.
     *
     */
	public static final class QuestionTable {
		
		private QuestionTable(){
			
		}

		public static final String DATA_TABLE = "Question";
		public static final String ID_KEY = "id";
		public static final String QUESTION_BUNDLE_KEY = "questionBundle";
		public static final String QUESTION_ID_KEY = "questionId";
		public static final String QUESTION_TEXT_KEY = "questionText";
		public static final String QUESTION_TYPE_KEY = "questionType";
		public static final String ANSWERS_VAR_KEY = "answersVar";
		public static final String ANSWERS_COR_KEY = "answersCor";

	}

    /**
     * 
     * Constants for Result Table.
     *
     */
	public static final class ResultTable {
		
		private ResultTable(){
			
		}

		public static final String DATA_TABLE = "Result";
		public static final String ID_KEY = "id";
		public static final String USER_CODE_KEY = "userCode";
		public static final String QUESTION_BUNDLE_KEY = "questionBundle";
		public static final String QUESTION_ID_KEY = "questionId";
		public static final String ANSWER_KEY = "answer";
		public static final String IS_CORRECT_KEY = "isCorrect";

	}
}
