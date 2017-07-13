package tudelft.dds.irep.data;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class Database {

	private MongoClient mongo;
	
	public Database(String host, int port){
		mongo = new MongoClient( host , port );
	}
	
	public void close(){
		mongo.close();
	}

	
	
	public void test(){
//		mongo.
//		MongoDatabase db = mongo.getDatabase("test");
//		MongoCollection<> table = db.getCollection("user");
	}


}
