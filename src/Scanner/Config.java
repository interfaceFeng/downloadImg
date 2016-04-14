package Scanner;

public class Config {
	public static final String IMG_FAC_HOME = "E:\\ImgFac";
	public static final int RAN_NAME_LEN = 5;
	protected static final String DATABASE_HOST = "localhost";
	protected static final int DATABASE_PORT = 27017;
	protected static final String DATABASE_NAME = "price_forcast";
	protected static final String DATA_COLL_NAME = "commodity";
	protected static final String REGULAR_EXPRESSION_URL_FIR = "^http.*";
	protected static final String REGULAR_EXPRESSION_URL_SEC = "^E:.*";
	protected static final String REGULAR_EXPRESSION_URL_THID = "^//img.*";
	protected static final String REGULAR_EXPRESSION_URL_CHANGE = ".*/n5/.*";
	protected static final int SACNNER_CYCLE = 3 * 1000;
}
