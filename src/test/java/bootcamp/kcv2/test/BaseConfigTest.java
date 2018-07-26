package bootcamp.kcv2.test;

import static org.junit.Assert.*;

import org.junit.Test;

import bootcamp.kcv2.util.BaseConfiguration;

/**
 * 
 * Testing connection parameters to database.
 *
 */
public class BaseConfigTest {
	/**
	 * This method checks connection parameters.
	 */
	@Test
	public final void test01() {
		String AUTH_KEY = BaseConfiguration.AUTH_KEY;
		String DB_SERVER = BaseConfiguration.DB_SERVER;
		String DB_PORT = BaseConfiguration.DB_PORT;
		String DB_NAME = BaseConfiguration.DB_NAME;
		String DB_USER = BaseConfiguration.DB_USER;
		String DB_PASSWORD = BaseConfiguration.DB_PASSWORD;

		assertEquals("1234", AUTH_KEY);
		assertEquals("sql2.freemysqlhosting.net", DB_SERVER);
		assertEquals("3306", DB_PORT);
		assertEquals("sql2248369", DB_NAME);
		assertEquals("sql2248369", DB_USER);
		assertEquals("xC8!kW1!", DB_PASSWORD);

	}

}
