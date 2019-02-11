package org.nasa.api.mars.service;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.nasa.api.mars.model.DataImageSet;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DownloadService {
	
	private final static String marsApiFormat = "https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos?earth_date=%s&page=%d&api_key=%s";
	private final static List<DataImageSet> listDataImage = new ArrayList<DataImageSet>();
	private final static String IMAGE_STORE_PATH = "curiosityImages";
	//private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE MMM dd yyyy");
	//private final static SimpleDateFormat DATE_SIMPLE_FORMAT = new SimpleDateFormat("MMddyyyy");
	//private final static SimpleDateFormat DATE_API_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private final static int OVER_LIMIT = 429;
	private final static String ERROR_OVER_LIMIT = "Over Rate Limit";
	
	private final static String DEMO_KEY = "DEMO_KEY";
	
	@RequestMapping("/exec")
	public DataImageSet exec(@RequestParam String dateParam, @RequestParam String apiKey) throws JSONException, ParseException, IOException 
    {
		//System.out.println(dateParam);
		//simple date format is not thread safe. recreate every call
		SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE MMM dd yyyy");
		SimpleDateFormat DATE_SIMPLE_FORMAT = new SimpleDateFormat("MMddyyyy");
		SimpleDateFormat DATE_API_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
		
		Date date = DATE_FORMAT.parse(dateParam);
		//System.out.println(date);
		
		File folder = new File(IMAGE_STORE_PATH + File.separatorChar + DATE_SIMPLE_FORMAT.format(date));
    	if(!folder.exists()){
    		folder.mkdirs();
    	}
    	
    	JSONArray arr = null;
    	
    	DataImageSet imageSet = new DataImageSet();
    	imageSet.setDate(DATE_API_FORMAT.format(date));
    	imageSet.setFolder(folder);
    	
    	for(int pageNum = 1 ; arr == null || !arr.isEmpty() ; pageNum++) {
    		String jsonRaw = "";
    		try {
    			String keyparam = apiKey == null || apiKey.trim().isEmpty() ? DEMO_KEY : apiKey.trim();
    			String getUrl = String.format(marsApiFormat, DATE_API_FORMAT.format(date), pageNum, keyparam);
    			jsonRaw = sendGET(getUrl);
    		} catch (Exception ex) {
    			ex.printStackTrace();
    			break;
    		}
    		
    		if(jsonRaw == null)
    			break;
    		
    		if(ERROR_OVER_LIMIT.equals(jsonRaw)) {
    			imageSet.setError(ERROR_OVER_LIMIT);
    			return imageSet;
    		}

    		arr = parseJSON(jsonRaw);
    		
	        for (int j = 0; j < arr.length(); j++) {
	            String imgUrl = arr.getJSONObject(j).getString("img_src");
	            imageSet.addImage(imgUrl);
	        }
    	}
    	
    	listDataImage.add(imageSet);
		return imageSet;
    }
    
    private String sendGET(String getUrl) throws IOException 
    {
		URL obj = new URL(getUrl);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		
		int responseCode = con.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			return response.toString();
		} else if (responseCode == OVER_LIMIT){ //over rate limit
			return ERROR_OVER_LIMIT;
		} else {
			return null;
		}
	}
    
    @RequestMapping("/dwnld")
	public DataImageSet dwnld(@RequestParam String dateParam) throws JSONException, ParseException, IOException 
    {
    	DataImageSet toDownload = null;
    	for(DataImageSet imageSet : listDataImage) {
    		if(imageSet.getDate().contentEquals(dateParam)) {
    			toDownload = imageSet;
    			break;
    		}
    	}
    	if(toDownload != null) {
    		fetchImages(toDownload.getFolder(), toDownload.getImages());
    		listDataImage.remove(toDownload);
    		toDownload.setComplete(true);
    		return toDownload;
    	}else {
    		DataImageSet imageSet = new DataImageSet();
        	imageSet.setDate(dateParam);
        	imageSet.setComplete(false);
        	return imageSet;
    	}
    }
    
    private void fetchImages(File folder, List<String> arrImgUrls) 
    {
    	BufferedImage image =null;
        try {
        	for (int i = 0; i < arrImgUrls.size(); i++) {
        		URL url =new URL(arrImgUrls.get(i));
        		image = ImageIO.read(url);
        		
        		ImageIO.write(image, "jpg",new File(folder.getAbsolutePath() + File.separatorChar + i));
        	}
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    private JSONArray parseJSON(final String jsonRaw) 
    {   
        JSONObject obj = new JSONObject(jsonRaw);        
        return obj.getJSONArray("photos");
    }
}