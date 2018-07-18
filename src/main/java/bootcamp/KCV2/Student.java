package bootcamp.KCV2;

public class Student {
	
	private int id;
	private String code;
	private String name;
	private String surname;

	public Student(int id, String code, String name, String surname) {
		
		this.id = id;
		this.code = code;
		this.name = name;
		this.surname = surname;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the surname
	 */
	public String getSurename() {
		return surname;
	}

	/**
	 * @param surename the surname to set
	 */
	public void setSurename(String surename) {
		this.surname = surename;
	}
}
