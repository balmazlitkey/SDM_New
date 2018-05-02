package service;

import DAO.UserDataDAO;
import com.mongodb.MongoClient;
import model.UserData;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.util.List;

public class UserDataService {

    private static Morphia morphia;
    private static MongoClient mongoClient;
    private static Datastore datastore;
    private static UserDataDAO userDataDAO;


    public UserDataService() {
        mongoClient = new MongoClient();
        morphia = new Morphia();
    }

    public void saveUserData(UserData userData)
    {
        userDataDAO.save(userData);
    }

    public List<UserData> getAllUserDatas()
    {

        for (int i=0;i<userDataDAO.find().asList().size();++i){
            System.out.println(userDataDAO.find().asList().get(i).getInstrumentID());
        }
        return userDataDAO.find().asList();
    }

    public void deleteUserData(UserData userData)
    {
        userDataDAO.delete(userData);
    }

    public void initDB()
    {
        morphia.mapPackage("onlab.iot.datasailmanager.backend");
        morphia.map(UserData.class);

        datastore = morphia.createDatastore(mongoClient, "iotDB");
        datastore.ensureIndexes();

        userDataDAO = new UserDataDAO(UserData.class, datastore);
    }



}
