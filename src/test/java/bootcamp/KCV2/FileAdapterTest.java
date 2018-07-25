package bootcamp.KCV2;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.validation.constraints.AssertTrue;

import org.apache.log4j.Logger;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;

import bootcamp.kcv2.QuestionManager;
import bootcamp.kcv2.util.DBAdapter;
import bootcamp.kcv2.util.FileAdapter;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FileAdapterTest {
	
	final Logger log = Logger.getLogger(FileAdapterTest.class);
	public static FileAdapter adapter;

	@Test
	public final void Test01fileAdapterTest() {
		adapter = new FileAdapter();
		
		assertTrue(adapter.exportQuestions("testfile.txt"));
		DBAdapter.ResultTableAdapter.clearResultTable();
		try {
			assertTrue(adapter.importQuestion("testfile.txt"));
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public final void Test02fileAdapterTest() {
		
		try {
			assertFalse(adapter.importQuestion("blank.txt"));
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public final void Test03pullResultsBundle() {
		
		
		ArrayList<String> a = DBAdapter.ResultTableAdapter.pullResultsBundle("SQL");
		ArrayList<String> b = new ArrayList<String>();
		assertEquals(a,b);
			
	}
	
	
	

	}

