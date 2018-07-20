package bootcamp.kcv2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class StudentManager {
	
	private static final String DATA_BASE = "kcv2.Student";
	private static final String ID_KEY = "id";
	private static final String NAME_KEY = "name";
	private static final String SURNAME_KEY = "surname";
	private static final String CODE_KEY = "code";
	
	
	protected Connection conn;

	public StudentManager() {

		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/?autoReconnect=true&useSSL=false", "root",
					"abcd1234");
			conn.setAutoCommit(false);
		} catch (ClassNotFoundException | SQLException e) {
			//  Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Student findStudent(int id) {
		// find student by id
		String query = "SELECT * FROM " + DATA_BASE + " WHERE `"+ID_KEY+"` = ?";
		PreparedStatement preparedStatement;
		try {
			preparedStatement = conn.prepareStatement(query);
			preparedStatement.setInt(1, id);
			ResultSet rs = preparedStatement.executeQuery();
			conn.commit();
			if (!rs.isBeforeFirst()) {
				return new Student(0, null, null, null);
			}
			while (rs.next()) {
				String firstName = rs.getString(NAME_KEY);
				String lastName = rs.getString(SURNAME_KEY);
				String code = rs.getString("code");
				return new Student(id, code, firstName, lastName);
			}
		} catch (SQLException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	

	
	public List<Student> findStudent(String firstName, String lastName) {
		try {
			conn.setAutoCommit(false);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		firstName = "%" + firstName + "%";
		lastName = "%" + lastName + "%";

		List<Student> studentList = new ArrayList<Student>();

		String query = "SELECT * FROM " + DATA_BASE + " WHERE `"+NAME_KEY+"` LIKE ? AND `"+SURNAME_KEY+"` LIKE ?";

		PreparedStatement preparedStatement;
		try {
			preparedStatement = conn.prepareStatement(query);
			preparedStatement.setString(1, firstName);
			preparedStatement.setString(2, lastName);
			ResultSet rs = preparedStatement.executeQuery();
			conn.commit();

			if (!rs.isBeforeFirst()) {
				return studentList;
			}
			while (rs.next()) {
				String first = rs.getString(NAME_KEY);
				String last = rs.getString(SURNAME_KEY);
				String code = rs.getString(CODE_KEY);
				int id = rs.getInt(ID_KEY);
				
				studentList.add(new Student(id, code, first, last));
			}

		} catch (SQLException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		return studentList;
	}
	
	public boolean insertStudent(String firstName, String lastName, String code) {

		String query = "SELECT * FROM " + DATA_BASE + " WHERE `"+CODE_KEY+"` = ?";
		PreparedStatement preparedStatement;
		try {
			preparedStatement = conn.prepareStatement(query);
			preparedStatement.setString(1, code);
			ResultSet rs = preparedStatement.executeQuery();
			conn.commit();
			if (rs.isBeforeFirst()) {
				System.out.println("Such code already exists");
			
				return false;
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

		query = "INSERT INTO " + DATA_BASE + " ("+NAME_KEY+", "+SURNAME_KEY+", "+CODE_KEY+") VALUES (?,?,?)";

		try {
			preparedStatement = conn.prepareStatement(query);
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
	
	
	public boolean updateStudent(Student student) {
		boolean status = false;

		String query = "UPDATE " + DATA_BASE + " SET `"+NAME_KEY+"` = ?, `"+SURNAME_KEY+"` = ?, `"+CODE_KEY+"` = ? WHERE `"+ID_KEY+"` = ?";

		PreparedStatement preparedStatement;
		try {
			preparedStatement = conn.prepareStatement(query);
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
	
	public boolean deleteStudent(int id) {
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
	
	
	public static void main(String args[]){
		
		StudentManager manager = new StudentManager();
		Student gap = new Student(2, "asdf", "Denissss", "Gap");
//		List students = new ArrayList();
//		students = manager.findStudent("Pa", "ve");
//		Student  student = (Student) students.get(0);
//		System.out.print(student.getName());
		
		manager.deleteStudent(2);
	}

}
