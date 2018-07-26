package bootcamp.kcv2;

import static org.junit.Assert.*;
import java.io.IOException;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import bootcamp.kcv2.util.FileAdapter;

/**
 * 
 * This class provides Testing for class {@link bootcamp.kcv2.util.FileAdapter
 * FileAdapter}.
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FileAdapterTest {

	final Logger log = Logger.getLogger(FileAdapterTest.class);
	public static FileAdapter adapter;

	/**
	 * This method tries to create new file where data are stored.
	 */
	@Test
	public final void Test01fileAdapterTest() {
		adapter = new FileAdapter();

		assertTrue("Export faild", adapter.exportQuestions("testfile.txt"));
		try {
			assertTrue(adapter.importQuestion("testfile.txt"));
		} catch (IOException | SQLException e) {
		}
	}

	/**
	 * This method tries to import data from txt file.
	 */
	@Test
	public final void Test02fileAdapterTest() {

		try {
			assertTrue("Import failed", adapter.importQuestion("testfile.txt"));
		} catch (IOException | SQLException e) {
		}
	}
}
