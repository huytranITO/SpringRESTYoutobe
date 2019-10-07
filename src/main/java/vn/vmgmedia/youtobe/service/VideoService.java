package vn.vmgmedia.youtobe.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.springframework.stereotype.Service;

import vn.vmgmedia.youtobe.common.ChanelConstants;
import vn.vmgmedia.youtobe.common.ExportDataUntil;
import vn.vmgmedia.youtobe.common.HandleHeaderRequest;
import vn.vmgmedia.youtobe.model.InfoVideoUpload;

@Service
public class VideoService {
	private static final Logger logger = Logger.getLogger(VideoService.class);
	
	private static String chanel = "";
	
	public Map<String, InfoVideoUpload> getListVideo(String linkChanel, Map<String, InfoVideoUpload> listInfoMappingPlaylist) {
		VideoService video = new VideoService();
		Map<String, InfoVideoUpload> listVideo = new HashMap<String, InfoVideoUpload>();
		video.getInfoVideoChanel(linkChanel, listInfoMappingPlaylist, listVideo);
		return listVideo;
	}
	
	/**
	 * @param String linkChanel youtobe
	 * @return void
	 * */
	public void getInfoVideoChanel(String linkChanel, Map<String, InfoVideoUpload> listInfoMappingPlaylist, Map<String, InfoVideoUpload> listInfoVideo) {
		String pageContinue = ChanelConstants.FIRST_PAGE_VIDEO_PUBLISH;
		
		String cToken = null;
		String date = new ExportDataUntil().getCurrentDate();
		do {
			try {
				
				if (cToken != null) {
					linkChanel = "https://www.youtube.com/browse_ajax?ctoken="+cToken
							+"&continuation"+cToken
							+ "&itct="+pageContinue;
				} else {
					linkChanel = linkChanel + pageContinue;
				}
				
				URL url = new URL(linkChanel);
				HttpsURLConnection getConnect = (HttpsURLConnection) url.openConnection();
				getConnect.setRequestMethod("GET");
				getConnect.setRequestProperty("x-youtube-client-name", "1");
				getConnect.setRequestProperty("x-youtube-client-version", "2."+date+".00.00");
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
					if (pageContinue == ChanelConstants.FIRST_PAGE_VIDEO_PUBLISH) {
						cToken = headerRequest.getTokenFirstContinue(jsonArrayParent);
						pageContinue =headerRequest.getPageFirstContinue(jsonArrayParent);
						chanel = jsonArrayParent.getJSONObject(1).getJSONObject("response").getJSONObject("microformat").getJSONObject("microformatDataRenderer").getString("title");
						getFirstListVideoInfo(jsonArrayParent, listInfoMappingPlaylist, listInfoVideo);
					} else {
						cToken = headerRequest.getContinueToken(jsonArrayParent);
						pageContinue = headerRequest.getPageContinue(jsonArrayParent);
						getContinueListVideoInfo(jsonArrayParent, listInfoMappingPlaylist, listInfoVideo);
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage() + " method getInfoVideoChanel");
			}
		} while (pageContinue != null);
	}
	
	/** Get info video
	 * @param JSONArray json data
	 * @param List<InformationYoutobe> list video
	 * */
	public void getFirstListVideoInfo(JSONArray jsonArrayParent, Map<String, InfoVideoUpload> listInfoMappingPlaylist, Map<String, InfoVideoUpload> listInfoVideo) {
		try {
			JSONArray listVideo = jsonArrayParent.getJSONObject(1)
					.getJSONObject("response")
					.getJSONObject("contents")
					.getJSONObject("twoColumnBrowseResultsRenderer")
					.getJSONArray("tabs")
					.getJSONObject(1)
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
			
			for (int j = 0; j < listVideo.length(); j++) {
				try {
					InfoVideoUpload youtobe = new InfoVideoUpload();
					youtobe.setId(listInfoVideo.size()+1);
					
					String link = listVideo.getJSONObject(j)
							.getJSONObject("gridVideoRenderer")
							.getString("videoId");
					
					if (listInfoMappingPlaylist.get(link) != null) {
						listInfoVideo.put(link, listInfoMappingPlaylist.get(link));
						chanel = listInfoMappingPlaylist.get(link).getChanel();
						continue;
					}
					
					youtobe.setLinkVideo(ChanelConstants.LINK_YOUTOBE.concat(link));
					
					youtobe.setChanel(chanel);
					
					youtobe.setNameVideo(listVideo.getJSONObject(j)
							.getJSONObject("gridVideoRenderer")
							.getJSONObject("title")
							.getString("simpleText"));
					
					youtobe.setTimeVideo(listVideo.getJSONObject(j)
							.getJSONObject("gridVideoRenderer")
							.getJSONArray("thumbnailOverlays")
							.getJSONObject(0)
							.getJSONObject("thumbnailOverlayTimeStatusRenderer")
							.getJSONObject("text")
							.getString("simpleText"));
					listInfoVideo.put(link, youtobe);
				} catch (Exception e) {
					continue;
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage() + " method getFirstListVideoInfo");
		}
	}
	
	public void getContinueListVideoInfo(JSONArray jsonArrayParent, Map<String, InfoVideoUpload> listInfoMappingPlaylist, Map<String, InfoVideoUpload> listInfoVideo) {
		try {
			JSONArray listVideo = jsonArrayParent.getJSONObject(1)
					.getJSONObject("response")
					.getJSONObject("continuationContents")
					.getJSONObject("gridContinuation")
					.getJSONArray("items");
			
			for (int j = 0; j < listVideo.length(); j++) {
				InfoVideoUpload youtobe = new InfoVideoUpload();
				youtobe.setId(listInfoVideo.size()+1);
				
				String link = listVideo.getJSONObject(j)
						.getJSONObject("gridVideoRenderer")
						.getString("videoId");

				if (listInfoMappingPlaylist.get(link) != null) {
					listInfoVideo.put(link, listInfoMappingPlaylist.get(link));
					continue;
				}
				
				youtobe.setLinkVideo(ChanelConstants.LINK_YOUTOBE.concat(link));
				
				youtobe.setChanel(chanel);
				
				youtobe.setNameVideo(listVideo.getJSONObject(j)
						.getJSONObject("gridVideoRenderer")
						.getJSONObject("title")
						.getString("simpleText"));
				
				youtobe.setTimeVideo(listVideo.getJSONObject(j)
						.getJSONObject("gridVideoRenderer")
						.getJSONArray("thumbnailOverlays")
						.getJSONObject(0)
						.getJSONObject("thumbnailOverlayTimeStatusRenderer")
						.getJSONObject("text")
						.getString("simpleText"));
				listInfoVideo.put(link, youtobe);
			}
		} catch (Exception e) {
				logger.error(e.getMessage() + " method getContinueListVideoInfo");
		}
	}
}
