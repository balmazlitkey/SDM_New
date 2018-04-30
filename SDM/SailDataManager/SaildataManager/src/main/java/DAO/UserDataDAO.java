package DAO;


import model.UserData;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;

 public class UserDataDAO extends BasicDAO<UserData, String> {

    public UserDataDAO(Class<UserData> entityClass, Datastore ds) {
        super(entityClass, ds);
    }
}
