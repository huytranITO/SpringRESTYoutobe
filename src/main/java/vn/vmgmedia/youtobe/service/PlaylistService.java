package vn.vmgmedia.youtobe.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.HttpsURLConnection;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.stereotype.Service;

import vn.vmgmedia.youtobe.common.ChanelConstants;
import vn.vmgmedia.youtobe.common.ExportDataUntil;
import vn.vmgmedia.youtobe.common.HandleHeaderRequest;
import vn.vmgmedia.youtobe.model.InfoPlaylistUpload;
import vn.vmgmedia.youtobe.model.InfoVideoUpload;


@Service
public class PlaylistService {
	private static final Logger logger = Logger.getLogger(PlaylistService.class);
	
	public static String chanel = "";
	
	public Map<String, InfoVideoUpload> getVideoMappingPlaylist(String linkChanel) {
		
		Map<String, InfoVideoUpload> listVideoMapping = new HashMap<String, InfoVideoUpload>();
		
		PlaylistService service = new PlaylistService();
		service.getInfoPlaylistChanel(linkChanel, listVideoMapping);
		
		return listVideoMapping;
	}
	
	/** Get list playlist chanel
	 * @param String linkChanel youtobe
	 * @return void
	 * */
	public void getInfoPlaylistChanel(String linkChanel, Map<String, InfoVideoUpload> listVideoMapping) {
		
		String pageContinue = ChanelConstants.PAGE_FIRST_PLAYLIST;
		
		String cToken = null;

		do {
			try {
				
				if (cToken != null) {
					linkChanel = "https://www.youtube.com/browse_ajax?ctoken="+cToken
							+"&continuation="+cToken
							+ "&itct="+pageContinue;
				} else {
					linkChanel = linkChanel + pageContinue;
				}
				
				URL url = new URL(linkChanel);
				HttpsURLConnection getConnect = (HttpsURLConnection) url.openConnection();
				getConnect.setRequestMethod("GET");
				getConnect.setRequestProperty("x-youtube-client-name", "1");
				getConnect.setRequestProperty("x-youtube-client-version", "2."+ new ExportDataUntil().getCurrentDate()+".00.00");
				getConnect.setDoOutput(true);
				
				int responseCode = getConnect.getResponseCode();
				
				if (responseCode == HttpsURLConnection.HTTP_OK) {
					
					BufferedReader bufferedReader = 
							new BufferedReader(new InputStreamReader(getConnect.getInputStream()));
					
					StringBuffer response = new StringBuffer();
					
					String readLine = null;
					while ((readLine = bufferedReader.readLine()) != null) {
						
						response.append(readLine);
					}
					
					bufferedReader.close();
					
					JSONArray jsonArrayParent = new JSONArray(response.toString());
					HandleHeaderRequest headerRequest = new HandleHeaderRequest();
					// Get info video to list
					if (pageContinue == ChanelConstants.PAGE_FIRST_PLAYLIST) {
						chanel = jsonArrayParent.getJSONObject(1)
								.getJSONObject("response")
								.getJSONObject("header")
								.getJSONObject("c4TabbedHeaderRenderer")
								.getString("title");
						cToken = headerRequest.getTokenFirst(jsonArrayParent);
						pageContinue =headerRequest. getPageFirstPlaylist(jsonArrayParent);
						getFirstInfoPlayList(jsonArrayParent, listVideoMapping);
					} else {
						cToken = headerRequest.getContinueToken(jsonArrayParent);
						pageContinue = headerRequest.getPageContinue(jsonArrayParent);
						getContinueListPlaylistInfo(jsonArrayParent, listVideoMapping);
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e.getCause());
			}
		} while (pageContinue != null);
	}
	
	public static Map<String, InfoVideoUpload> getFirstInfoPlayList(JSONArray jsonArrayParent, Map<String, InfoVideoUpload> listVideoMapping) {
		//Map<String, InfoVideoUpload> listVideo = new HashMap<String, InfoVideoUpload>();
		try {
			JSONArray listPlaylist = jsonArrayParent.getJSONObject(1)
					.getJSONObject("response")
					.getJSONObject("contents")
					.getJSONObject("twoColumnBrowseResultsRenderer")
					.getJSONArray("tabs")
					.getJSONObject(2)
					.getJSONObject("tabRenderer")
					.getJSONObject("content")
					.getJSONObject("sectionListRenderer")
					.getJSONArray("contents")
					.getJSONObject(0)
					.getJSONObject("itemSectionRenderer")
					.getJSONArray("contents")
					.getJSONObject(0)
					.getJSONObject("gridRenderer")
					.getJSONArray("items");
			int count = 0;
			for (int j = 0; j < listPlaylist.length(); j++) {
				InfoPlaylistUpload playlist = new InfoPlaylistUpload();
			 	playlist.setId(listPlaylist.getJSONObject(j).getJSONObject("gridPlaylistRenderer").getString("playlistId"));
			  
			 	playlist.setTypePlaylist(ChanelConstants.LIST_CREATED);
				playlist.setNamePlayList(listPlaylist.getJSONObject(j).getJSONObject("gridPlaylistRenderer").getJSONObject("title")
																	.getJSONArray("runs").getJSONObject(0).getString("text"));
				MappingInfoService mapping = new MappingInfoService();
				mapping.chanel = chanel;
            	mapping.mappingVideoBaseOnPlaylist(playlist, listVideoMapping);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e.getCause());
			return null;
		}
		return listVideoMapping;
	}
	
	public static void getContinueListPlaylistInfo(JSONArray jsonArrayParent, Map<String, InfoVideoUpload> listVideoMapping) {
		try {
			JSONArray listPlaylist = jsonArrayParent.getJSONObject(1)
					.getJSONObject("response")
					.getJSONObject("continuationContents")
					.getJSONObject("gridContinuation")
					.getJSONArray("items");
			
			for (int j = 0; j < listPlaylist.length(); j++) {
				InfoPlaylistUpload playlist = new InfoPlaylistUpload();
				
				playlist.setId(listPlaylist.getJSONObject(j).getJSONObject("gridPlaylistRenderer").getString("playlistId"));
				
				playlist.setTypePlaylist(ChanelConstants.LIST_CREATED);
				
				playlist.setNamePlayList(listPlaylist.getJSONObject(j).getJSONObject("gridPlaylistRenderer").getJSONObject("title")
						.getJSONArray("runs").getJSONObject(0).getString("text"));
				new Thread(new Runnable() {
                    @Override
                    public void run() {
							MappingInfoService mapping = new MappingInfoService();
	                    	mapping.mappingVideoBaseOnPlaylist(playlist, listVideoMapping);
                    }
                    }).start();
				/* lisInfoPLaylist.add(playlist); */
			}
		} catch (Exception e) {
				logger.error(e.getMessage(), e.getCause());
		}
	}
	 
}
