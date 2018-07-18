package com.javawebtutor.service;
 
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
 
import com.javawebtutor.hibernate.util.HibernateUtil;
import com.javawebtutor.model.User;
 
public class LoginService {
 
    public boolean authenticateUser(String SURENAME, String CODE) {
        User user = getUserByUserId(SURENAME);         
        if(user!=null && user.getUserId().equals(SURENAME) && user.getPassword().equals(CODE)){
            return true;
        }else{
            return false;
        }
    }
 
    public User getUserByUserId(String lastName) {
        Session session = HibernateUtil.openSession();
        Transaction tx = null;
        User user = null;
        try {
            tx = session.getTransaction();
            tx.begin();
            Query query = session.createQuery("from User where SURENAME='"+lastName+"'");
            user = (User)query.uniqueResult();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return user;
    }
     
    public List<User> getListOfUsers(){
        List<User> list = new ArrayList<User>();
        Session session = HibernateUtil.openSession();
        Transaction tx = null;       
        try {
            tx = session.getTransaction();
            tx.begin();
            list = session.createQuery("from User").list();                       
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return list;
    }
}