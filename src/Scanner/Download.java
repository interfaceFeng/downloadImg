package Scanner;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import org.apache.log4j.Logger;



public class Download {
	
	public static byte[] getImgFromNetByUrl(String strUrl, Logger logger){
		try {
			URL url = new URL(strUrl);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5 * 1000);
			InputStream inStream = conn.getInputStream();
			
			byte[] btImg = readInputStream(inStream);//得到图片的二进制表示
			
			return btImg;
		}catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("download error: " + e.getMessage());
			
			return null;
		}
		
		
		
		
	}
	
	public static byte[] readInputStream(InputStream inStream) throws IOException{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[2048];
		int len = 0;
		
		while((len = inStream.read(buffer)) != -1){
			outStream.write(buffer, 0, len);
		}
		
		inStream.close();
		byte[] buffer2 = outStream.toByteArray();
		outStream.close();
		return buffer2;
	}
	
	
	public static String getRandomName(int len){
		String randomFr = "1234567890qwertyuioplkjhgfdsazxcvbnm";
		Random random = new Random();
		StringBuffer strBuf = new StringBuffer();
		
		int index;
		for(int i = 0; i < len; i++){
			index = random.nextInt(36);
			strBuf.append(randomFr.charAt(index));
		}
		
		if(strBuf != null && strBuf.length() > 0){
			return strBuf.toString();
		}
		
		return null;
	}
	
	public static String saveImgToFac(byte[] btImg,
									String floderSource, String floderId, String imgName,
									Logger logger){
		String parFiName = Config.IMG_FAC_HOME + "\\" + floderSource + "\\" + floderId;
		File parFile = new File(parFiName);
		
		if(!parFile.exists() && !parFile.isDirectory()){
			try {
				parFile.mkdirs();
				
				logger.info("make a new floder" + parFiName);
				
				//log create a new floder
			} catch (Exception e) {
				e.printStackTrace();
				
				logger.error("mkdirs error" + parFiName);
				
				return null;
				
				//log create a new floder failed
			}
		}
		
		try {
			String subsWay = parFiName + "\\" + imgName + ".gif";
			FileOutputStream fops = new FileOutputStream(subsWay);
			fops.write(btImg);
			fops.flush();
			fops.close();
			
			logger.info("write img in disk" + subsWay);

			return subsWay;
			//log img insert success
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//log write img fail
			
			logger.error("write img to disk error" + floderSource + floderId);
			
			e.printStackTrace();
		}
		
		return null;
	}
}




















