package Scanner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class Scanner {
	private MongoClient mongoClient;
	private MongoClient mongoClientWS;//192.168.1.250
	private MongoDatabase mongoDatabase;
	private MongoDatabase mongoDatabaseWS;
	private static MongoCollection<Document> mongoColl;
	private static MongoCollection<Document> DataFac;
	private static MongoCollection<Document> mongoCollWS;
	private static Logger logger = Logger.getLogger(Scanner.class.getName());

	public static void main(String[] args) {
		
		Scanner scanner = new Scanner();
		scanner.connMongo();
//		scanner.connWorkS();
		
		
//		OperateMonImg.forkAllData(mongoColl, DataFac);
//		for(int i = 0; i < 20; i++){
//			OperateMonImg.insertData(mongoColl);
//		}
		
//		OperateMonImg.deleteData(mongoColl);
//		System.exit(0);
		
		int countUpdateUrl;
		int timeScanner = 0;
		
		while(true){
			countUpdateUrl = OperateMonImg.findImgUrl(mongoColl, logger);
			logger.info("update num" + countUpdateUrl + "\n");
			try {
				Thread.sleep(Config.SACNNER_CYCLE);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			System.out.println("µÚ" + ++timeScanner + "´ÎÉ¨Ãè");
		}
		
		
		
		
	}

	private void connMongo() {
		try {
			mongoClient = new MongoClient(Config.DATABASE_HOST,
					Config.DATABASE_PORT);

			mongoDatabase = mongoClient.getDatabase(Config.DATABASE_NAME);
//			mongoDatabase.createCollection("dataFac");
			DataFac = mongoDatabase.getCollection("dataFac");
			mongoColl = mongoDatabase.getCollection(Config.DATA_COLL_NAME);

			logger.info("connect to mongo " + Config.DATABASE_HOST + ": " + Config.DATABASE_PORT);
		} catch (Exception e) {
			logger.error("connect to mongo fail");
		}

	}
	
	private void connWorkS(){
		try {
			mongoClientWS = new MongoClient("192.168.1.250",
				27017);

			mongoDatabaseWS = mongoClientWS.getDatabase("price_forecast");
			mongoCollWS = mongoDatabaseWS.getCollection(Config.DATA_COLL_NAME);
//			System.out.println(mongoCollWS.count());

			logger.info("connect to mongo " + "192.168.1.250" + ": " + 27017);
		} catch (Exception e) {
			logger.error("connect to mongo" + "192" + "fail");
		}
	}
	
}
