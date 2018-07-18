package com.javawebtutor.model;
 
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
 
@Entity
@Table(name="USER")
public class User implements Serializable {
     
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id @GeneratedValue
    private Long id;
    private String NAME;
    //private String middleName;
    private String SURENAME;
    //private String email;
    private String ID;
    private String CODE;
 
    public User() {
    }
 
     
    public User(String firstName,  String lastName,  String userId, String password) {
        this.NAME = firstName;
        //this.middleName = middleName;
        this.SURENAME = lastName;
        //this.email = email;
       // this.ID = userId;
        this.CODE = password;
    }
 
 
    public Long getId() {
        return id;
    }
 
     public void setId(Long id) {
        this.id = id;
    }
 
    public String getFirstName() {
        return NAME;
    }
 
    public void setFirstName(String firstName) {
        this.NAME = firstName;
    }
 
   
 
    public String getLastName() {
        return SURENAME;
    }
 
    public void setLastName(String lastName) {
        this.SURENAME = lastName;
    }
 
   
 
    public String getUserId() {
        return SURENAME;
    }
 
    //public void setUserId(String userId) {
       // this.ID = userId;
   // }
 
    public String getPassword() {
        return CODE;
    }
 
    public void setPassword(String password) {
        this.CODE = password;
    }       
}