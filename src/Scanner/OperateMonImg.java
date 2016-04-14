package Scanner;

import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;

public class OperateMonImg {
	public static void insertData(MongoCollection<Document> mongoColl) {
		ArrayList<String> imgurl = new ArrayList<String>();

		Document doc = new Document();
		doc.append(new Date().toString(), 1000).append(new Date().toString(),
				2000);

		imgurl.add("//img10.360buyimg.com/n5/jfs/t241/302/1519885396/159931/6b34e86d/56f34a2dNf8e104b0.jpg");
		imgurl.add("http://img10.360buyimg.com/n5/jfs/t2155/46/2179863605/128295/9da614d1/56f34a0fN69e24ab9.jpg");

		Document document = new Document("advice", "一周内购买")
				.append("anticipate_change_time", "一周之内")
				.append("anticipate_price_high", 2000)
				.append("anticipate_price_low", 1000)
				.append("current_price", 1888)
				.append("current_time", new Date())
				.append("description", "iphone 6s").append("id", "2323")
				.append("img_url", imgurl).append("lowest_price", 1000)
				.append("lowest_time", new Date()).append("name", "iphone 6s")
				.append("price_list", doc).append("source", "jd")
				.append("update_time", new Date())
				.append("url", "http://item.jd.com/1856584.html");

		Document document2 = new Document("advice", "一周内购买")
				.append("anticipate_change_time", "一周之内")
				.append("anticipate_price_high", 2000)
				.append("anticipate_price_low", 1000)
				.append("current_price", 1888)
				.append("current_time", new Date())
				.append("description", "iphone 6s").append("id", "23")
				.append("img_url", imgurl).append("lowest_price", 1000)
				.append("lowest_time", new Date()).append("name", "iphone 6s")
				.append("price_list", doc).append("source", "tb")
				.append("update_time", new Date())
				.append("url", "http://item.jd.com/1856584.html");

		mongoColl.insertOne(document);
		mongoColl.insertOne(document2);

	}

	public static void forkAllData(MongoCollection<Document> mongoColl,
			MongoCollection<Document> mongoCollBy) {
		FindIterable<Document> findIterable = mongoCollBy.find();
		MongoCursor<Document> mongoCursor = findIterable.iterator();
		Document doc = null;
		ArrayList<Document> docs = new ArrayList<Document>();
		int num = 0;
		while (mongoCursor.hasNext() && num <= 10000) {
			doc = mongoCursor.next();
			mongoColl.insertOne(doc);
			num = num + 1;
			// System.out.println(doc);
			// docs.add(doc);

		}

		// if(docs != null)
		// mongoColl.insertMany(docs);
	}

	public static void deleteData(MongoCollection<Document> mongoColl) {
		FindIterable<Document> findIterable = mongoColl.find();
		MongoCursor<Document> mongoCursor = findIterable.iterator();
		Document doc = null;
		while (mongoCursor.hasNext()) {
			doc = mongoCursor.next();
			mongoColl.deleteOne(doc);
		}
	}

	public static int findImgUrl(MongoCollection<Document> mongoColl,
			Logger logger) {
		FindIterable<Document> findIterable = mongoColl.find();
		MongoCursor<Document> mongoCursor = findIterable.iterator();

		int countUpdate = 0;

		String urlImg;
		String urlCopy;
		Document doc;
		ArrayList<String> imgUrl = null;
		byte[] btImg;
		String imgName;
		String floderId;
		String floderSource;
		String subsWay = null;
		int index;
		Bson filter;
		UpdateResult updateRes = null;

		while (mongoCursor.hasNext()) {
			doc = mongoCursor.next();

			imgUrl = extracted(doc);
			ObjectId _id = doc.getObjectId("_id");
			floderId = doc.getString("id");
			floderSource = doc.getString("source");
			if (imgUrl == null) {
				logger.warn("not hava img_url for" + floderSource + floderId);
				continue;
			}
			for (String str : imgUrl) {
				index = imgUrl.indexOf(str);
				urlImg = getComplUrl(str);
				if (urlImg != null) {
					urlCopy = urlImg;
					urlImg = getBigPicURL(urlImg);
				} else {
					logger.warn("illegal url for : " + floderSource + floderId
							+ "[" + index + "]");
					continue;
				}

				if (urlImg.length() > 0 && urlImg.compareTo("local") != 0) {
					btImg = Download.getImgFromNetByUrl(urlImg, logger);
					if (btImg != null && btImg.length > 0) {

						logger.info("download img success" + floderSource
								+ floderId + "[" + index + "]");

						imgName = Download.getRandomName(Config.RAN_NAME_LEN);
						if ((subsWay = Download.saveImgToFac(btImg,
								floderSource, floderId, imgName, logger)) != null) {// update
																					// mongo
							imgUrl.set(index, subsWay);
							filter = Filters.eq("_id", _id);
							updateRes = mongoColl.updateOne(filter,
									new Document("$set", new Document(
											"img_url", imgUrl)));
							if (updateRes.getModifiedCount() > 0) {
								countUpdate += updateRes.getModifiedCount();
								continue;
							} else {

								logger.error("update data fail" + floderSource
										+ floderId + "[" + index + "]");

							}
						}

					} else {
						logger.warn("this url do not hava a n7 pic"
								+ floderSource + floderId + "[" + index + "]");

						btImg = Download.getImgFromNetByUrl(urlCopy, logger);
						if (btImg != null && btImg.length > 0) {

							logger.info("download img success" + floderSource
									+ floderId + "[" + index + "]");

							imgName = Download
									.getRandomName(Config.RAN_NAME_LEN);
							if ((subsWay = Download.saveImgToFac(btImg,
									floderSource, floderId, imgName, logger)) != null) {// update
																						// mongo
								imgUrl.set(index, subsWay);
								filter = Filters.eq("_id", _id);
								updateRes = mongoColl.updateOne(filter,
										new Document("$set", new Document(
												"img_url", imgUrl)));
								if (updateRes.getModifiedCount() > 0) {
									countUpdate += updateRes.getModifiedCount();
									continue;
								} else {

									logger.error("update data fail"
											+ floderSource + floderId + "["
											+ index + "]");

								}
							}

						} else {
							logger.error("wrong url" + floderSource + floderId
									+ "[" + index + "]");
						}

					}
				}

				else {
					if (urlImg.compareTo("local") != 0)
						logger.warn("illegal url" + floderSource + floderId
								+ "[" + index + "]");
				}
			}

		}

		return countUpdate;
	}

	private static ArrayList<String> extracted(Document doc) {
		return (ArrayList<String>) doc.get("img_url");
	}

	private static String getComplUrl(String str) {
		if (str == null)
			return null;

		Matcher matcher = null;
		Pattern pattern;

		pattern = Pattern.compile(Config.REGULAR_EXPRESSION_URL_FIR);
		matcher = pattern.matcher(str);
		if (matcher.matches()) {
			return str;
		}

		else {
			// String subStr = str.substring(0, 5);
			pattern = Pattern.compile(Config.REGULAR_EXPRESSION_URL_THID);
			matcher = pattern.matcher(str);
			// if(subStr.compareTo("//img") == 0){
			// return "http:" + str;
			// }
			if (matcher.matches()) {
				return "http:" + str;
			}

			pattern = Pattern.compile(Config.REGULAR_EXPRESSION_URL_SEC);
			matcher = pattern.matcher(str);
			if (matcher.matches()) {
				return "local";
			}

			return null;

		}

	}

	private static String getBigPicURL(String str) {
		Matcher matcher;
		Pattern pattern;

		pattern = Pattern.compile(Config.REGULAR_EXPRESSION_URL_CHANGE);
		matcher = pattern.matcher(str);

		if (matcher.matches()) {
			return str.replaceFirst("/n5/", "/n7/");
		}

		return str;
	}

}
