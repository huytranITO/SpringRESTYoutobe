package vn.vmgmedia.youtobe.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.apache.log4j.Logger;
import org.apache.logging.log4j.message.AsynchronouslyFormattable;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import vn.vmgmedia.youtobe.common.ChanelConstants;
import vn.vmgmedia.youtobe.common.ExportDataUntil;
import vn.vmgmedia.youtobe.common.HandleHeaderRequest;
import vn.vmgmedia.youtobe.model.InfoPlaylistUpload;
import vn.vmgmedia.youtobe.model.InfoVideoUpload;

/** 
 *  Get info video base on playlist chanel
 * @author Huy.Tho
 * 
 * */
@Service
public class PlaylistService {
	
	private static final Logger logger = Logger.getLogger(PlaylistService.class);
	
	public static String chanel = "";
	
	public static String PLAYLIST_NAME = "";
	
	private static String version = "";
	
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
	@Async
	public void getInfoPlaylistChanel(String linkChanel, Map<String, InfoVideoUpload> listVideoMapping) {
		version = new HandleHeaderRequest().getVersion(linkChanel);
		
		if (version == null) {
			version = "2."+ new ExportDataUntil().getCurrentDate()+".00.00";
		}
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
				getConnect.setRequestProperty("Content-type", "application/json; charset=utf-8");
				getConnect.setRequestProperty("x-youtube-client-name", "1");
				getConnect.setRequestProperty("x-youtube-client-version", version);
				getConnect.setDoOutput(true);
				
				int responseCode = getConnect.getResponseCode();
				
				if (responseCode == HttpsURLConnection.HTTP_OK) {
					
					BufferedReader bufferedReader = 
							new BufferedReader(new InputStreamReader(getConnect.getInputStream(), "UTF-8"));
					
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
				break;
			}
		} while (pageContinue != null);
	}
	
	@Async
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
            	new PlaylistService().mappingVideoBaseOnPlaylist(version, playlist, listVideoMapping);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e.getCause());
			return null;
		}
		return listVideoMapping;
	}
	
	@Async
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
		        new PlaylistService().mappingVideoBaseOnPlaylist(version, playlist, listVideoMapping);
			}
		} catch (Exception e) {
				logger.error(e.getMessage(), e.getCause());
		}
	}
	//------------------------
	/** Mapping video with playlist
	 * @param List<InfoPlaylistUpload> listPlaylist
	 * @param Map<String, InfoVideoUpload> listInfoVideo
	 * */
	@Async
	public void mappingVideoBaseOnPlaylist(String version, InfoPlaylistUpload playlist, Map<String, InfoVideoUpload> listInfoVideo) {
		String linkPlaylist = ChanelConstants.LINK_PLAYLIST;
		
		String pageContinue = ChanelConstants.FIRST_PAGE;
		
		String cToken = null;
		
		try {
			do {

				if (cToken != null) {
					linkPlaylist = "https://www.youtube.com/browse_ajax?ctoken="+cToken
							+"&continuation="+cToken
							+ "&itct="+pageContinue;
				} else {
					linkPlaylist = linkPlaylist + playlist.id + pageContinue;
				}
				
				URL url = new URL(linkPlaylist);
				HttpsURLConnection getConnect = (HttpsURLConnection) url.openConnection();
				getConnect.setRequestMethod("GET");
				getConnect.setRequestProperty("Content-type", "application/json; charset=utf-8");
				getConnect.setRequestProperty("x-youtube-client-name", "1");
				getConnect.setRequestProperty("x-youtube-client-version", version);
				getConnect.setDoOutput(true);
				
				int responseCode = getConnect.getResponseCode();
				if (responseCode == HttpsURLConnection.HTTP_OK) {
					
					BufferedReader bufferedReader = 
							new BufferedReader(new InputStreamReader(getConnect.getInputStream(),"UTF-8"));
					
					StringBuffer response = new StringBuffer();
					
					String readLine = null;
					while ((readLine = bufferedReader.readLine()) != null) {
						
						response.append(readLine);
					}
					
					bufferedReader.close();
					
					JSONArray jsonArrayParent = new JSONArray(response.toString());
					
					// Get info video to list
					if (pageContinue == ChanelConstants.FIRST_PAGE) {
						cToken = getTokenFirst(jsonArrayParent);
						pageContinue = getPageFirst(jsonArrayParent);
						getFirstListVideoInfo(playlist, jsonArrayParent, listInfoVideo);
					} else {
						cToken = getToken(jsonArrayParent);
						pageContinue = getPage(jsonArrayParent);
						getListVideoInfo(playlist, jsonArrayParent, listInfoVideo);
					}
				}
				
			} while (pageContinue != null);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	public void getFirstListVideoInfo(InfoPlaylistUpload playlist, JSONArray jsonArrayParent, Map<String, InfoVideoUpload> listInfoVideo) {
		try {
			JSONArray listVideo = jsonArrayParent.getJSONObject(1)
					.getJSONObject("response")
					.getJSONObject("contents")
					.getJSONObject("twoColumnBrowseResultsRenderer")
					.getJSONArray("tabs")
					.getJSONObject(0)
					.getJSONObject("tabRenderer")
					.getJSONObject("content")
					.getJSONObject("sectionListRenderer")
					.getJSONArray("contents")
					.getJSONObject(0)
					.getJSONObject("itemSectionRenderer")
					.getJSONArray("contents")
					.getJSONObject(0)
					.getJSONObject("playlistVideoListRenderer")
					.getJSONArray("contents");
			
			for (int j = 0; j < listVideo.length(); j++) {
				try {
					InfoVideoUpload youtobe = new InfoVideoUpload();
					youtobe.setId(j);
					
					String link = listVideo.getJSONObject(j)
							.getJSONObject("playlistVideoRenderer")
							.getString("videoId");
					if(checkKey(link, listInfoVideo)) {
						if (listInfoVideo.get(link).getPlayList() == null) {
							listInfoVideo.get(link).setPlayList("");
						}
						listInfoVideo.get(link).setPlayList(listInfoVideo.get(link).getPlayList().concat(",".concat(playlist.getNamePlayList())));
						continue;
					}
					youtobe.setLinkVideo(ChanelConstants.LINK_YOUTOBE
							.concat(link));
					
					youtobe.setChanel(listVideo.getJSONObject(j)
							.getJSONObject("playlistVideoRenderer")
							.getJSONObject("shortBylineText")
							.getJSONArray("runs")
							.getJSONObject(0)
							.getString("text"));
					
					
					youtobe.setNameVideo(listVideo.getJSONObject(j)
							.getJSONObject("playlistVideoRenderer")
							.getJSONObject("title")
							.getString("simpleText"));
					
					youtobe.setTimeVideo(listVideo.getJSONObject(j)
							.getJSONObject("playlistVideoRenderer")
							.getJSONObject("lengthText")
							.getString("simpleText"));
					
					youtobe.setPlayList(playlist.getNamePlayList());
					
					listInfoVideo.put(link, youtobe);
				} catch (Exception e) {
					continue;
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage() + "method getFirstListVideoInfo");
		}
	}
	
	public void getListVideoInfo(InfoPlaylistUpload playlist, JSONArray jsonArrayParent, Map<String, InfoVideoUpload> listInfoVideo) {
		try {
			JSONArray listVideo = jsonArrayParent.getJSONObject(1)
					.getJSONObject("response")
					.getJSONObject("continuationContents")
					.getJSONObject("playlistVideoListContinuation")
					.getJSONArray("contents");
			
			for (int j = 0; j < listVideo.length(); j++) {
				try {
					InfoVideoUpload youtobe = new InfoVideoUpload();
					youtobe.setId(j);
					
					String link = listVideo.getJSONObject(j)
							.getJSONObject("playlistVideoRenderer")
							.getString("videoId");
					
					if(checkKey(link, listInfoVideo)) {
						if (listInfoVideo.get(link).getPlayList() == null) {
							listInfoVideo.get(link).setPlayList(playlist.getNamePlayList());
						} else {
							listInfoVideo.get(link).setPlayList(listInfoVideo.get(link).getPlayList().concat(",".concat(playlist.getNamePlayList())));
						}
						continue;
					}
					
					youtobe.setLinkVideo(ChanelConstants.LINK_YOUTOBE.concat(link));
					
					youtobe.setChanel(chanel);
						youtobe.setNameVideo(listVideo.getJSONObject(j)
								.getJSONObject("playlistVideoRenderer")
								.getJSONObject("title")
								.getString("simpleText"));
	
					youtobe.setTimeVideo(listVideo.getJSONObject(j)
							.getJSONObject("playlistVideoRenderer")
							.getJSONObject("lengthText")
							.getString("simpleText"));
					
					youtobe.setPlayList(playlist.getNamePlayList());
					
					listInfoVideo.put(link, youtobe);
				} catch (Exception e) {
					continue;
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage() + "method getListVideoInfo" );
		}
	}
	
	public String getPageFirst(JSONArray jsonArrayParent) {
		try {
			return jsonArrayParent.getJSONObject(1)
					.getJSONObject("response")
					.getJSONObject("contents")
					.getJSONObject("twoColumnBrowseResultsRenderer")
					.getJSONArray("tabs")
					.getJSONObject(0)
					.getJSONObject("tabRenderer")
					.getJSONObject("content")
					.getJSONObject("sectionListRenderer")
					.getJSONArray("contents")
					.getJSONObject(0)
					.getJSONObject("itemSectionRenderer")
					.getJSONArray("contents")
					.getJSONObject(0)
					.getJSONObject("playlistVideoListRenderer")
					.getJSONArray("continuations")
					.getJSONObject(0)
					.getJSONObject("nextContinuationData")
					.getString("clickTrackingParams");
		} catch (JSONException e) {
			return null;
		}
	}
	
	public String getTokenFirst(JSONArray jsonArrayParent) {
		try {
			return jsonArrayParent.getJSONObject(1)
					.getJSONObject("response")
					.getJSONObject("contents")
					.getJSONObject("twoColumnBrowseResultsRenderer")
					.getJSONArray("tabs")
					.getJSONObject(0)
					.getJSONObject("tabRenderer")
					.getJSONObject("content")
					.getJSONObject("sectionListRenderer")
					.getJSONArray("contents")
					.getJSONObject(0)
					.getJSONObject("itemSectionRenderer")
					.getJSONArray("contents")
					.getJSONObject(0)
					.getJSONObject("playlistVideoListRenderer")
					.getJSONArray("continuations")
					.getJSONObject(0)
					.getJSONObject("nextContinuationData")
					.getString("continuation");
		} catch (JSONException e) {
			return null;
		}
	}
	
	
	public String getPage(JSONArray jsonArrayParent) {
		try {
			return jsonArrayParent.getJSONObject(1)
					.getJSONObject("response")
					.getJSONObject("continuationContents")
					.getJSONObject("playlistVideoListContinuation")
					.getJSONArray("continuations")
					.getJSONObject(0)
					.getJSONObject("nextContinuationData")
					.getString("clickTrackingParams");
		} catch (Exception e) {
			return null;
		}
	}
	
	public String getToken(JSONArray jsonArrayParent) {
		try {
			return jsonArrayParent.getJSONObject(1)
					.getJSONObject("response")
					.getJSONObject("continuationContents")
					.getJSONObject("playlistVideoListContinuation")
					.getJSONArray("continuations")
					.getJSONObject(0)
					.getJSONObject("nextContinuationData")
					.getString("continuation");
		} catch (Exception e) {
			return null;
		}
	}
	
	public boolean checkKey(String link, Map<String, InfoVideoUpload> obj) {
		if (obj.get(link) != null ) {
			return true;
		}
		return false;
	}
	
	/**
	 * Get info vide phaylist
	 * */
	
	public Map<String, InfoVideoUpload> mappingVideoOfPlaylist(String pathPlayList) {
		
		Map<String, InfoVideoUpload> listInfoVideo = new HashMap<String, InfoVideoUpload>();
		
		version = "2."+ new ExportDataUntil().getCurrentDate()+".00.00";
		
		String linkPlaylist = ChanelConstants.LINK_PLAYLIST;
		
		String pageContinue = ChanelConstants.FIRST_PAGE;
		
		String cToken = null;
		
		try {
			do {

				if (cToken != null) {
					linkPlaylist = "https://www.youtube.com/browse_ajax?ctoken="+cToken
							+"&continuation="+cToken
							+ "&itct="+pageContinue;
				} else {
					linkPlaylist = pathPlayList+ChanelConstants.FIRST_PAGE;
				}
				System.out.println(linkPlaylist+ "-====================================================");
				URL url = new URL(linkPlaylist);
				HttpsURLConnection getConnect = (HttpsURLConnection) url.openConnection();
				getConnect.setRequestMethod("GET");
				getConnect.setRequestProperty("Content-type", "application/json; charset=utf-8");
				getConnect.setRequestProperty("x-youtube-client-name", "1");
				getConnect.setRequestProperty("x-youtube-client-version", version);
				getConnect.setDoOutput(true);
				
				int responseCode = getConnect.getResponseCode();
				if (responseCode == HttpsURLConnection.HTTP_OK) {
					
					BufferedReader bufferedReader = 
							new BufferedReader(new InputStreamReader(getConnect.getInputStream(),"UTF-8"));
					
					StringBuffer response = new StringBuffer();
					
					String readLine = null;
					while ((readLine = bufferedReader.readLine()) != null) {
						
						response.append(readLine);
					}
					
					bufferedReader.close();
					
					JSONArray jsonArrayParent = new JSONArray(response.toString());
					
					// Get info video to list
					if (pageContinue == ChanelConstants.FIRST_PAGE) {
						cToken = getTokenFirst(jsonArrayParent);
						pageContinue = getPageFirst(jsonArrayParent);
						getFirstListVideoInfo(jsonArrayParent, listInfoVideo);
					} else {
						cToken = getToken(jsonArrayParent);
						pageContinue = getPage(jsonArrayParent);
						getListVideoInfo(jsonArrayParent, listInfoVideo);
					}
				}
				
			} while (pageContinue != null);
			return listInfoVideo;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return listInfoVideo;
		}
	}
	
	public void getFirstListVideoInfo(JSONArray jsonArrayParent, Map<String, InfoVideoUpload> listInfoVideo) {
		try {
			JSONArray listVideo = jsonArrayParent.getJSONObject(1)
					.getJSONObject("response")
					.getJSONObject("contents")
					.getJSONObject("twoColumnBrowseResultsRenderer")
					.getJSONArray("tabs")
					.getJSONObject(0)
					.getJSONObject("tabRenderer")
					.getJSONObject("content")
					.getJSONObject("sectionListRenderer")
					.getJSONArray("contents")
					.getJSONObject(0)
					.getJSONObject("itemSectionRenderer")
					.getJSONArray("contents")
					.getJSONObject(0)
					.getJSONObject("playlistVideoListRenderer")
					.getJSONArray("contents");
			
			PLAYLIST_NAME = jsonArrayParent.getJSONObject(1)
								.getJSONObject("response")
								.getJSONObject("microformat")
								.getJSONObject("microformatDataRenderer")
								.getString("title");
			for (int j = 0; j < listVideo.length(); j++) {
				try {
					InfoVideoUpload youtobe = new InfoVideoUpload();
					youtobe.setId(j);
					
					String link = listVideo.getJSONObject(j)
							.getJSONObject("playlistVideoRenderer")
							.getString("videoId");
					if(checkKey(link, listInfoVideo)) {
						if (listInfoVideo.get(link).getPlayList() == null) {
							listInfoVideo.get(link).setPlayList("");
						}
						listInfoVideo.get(link).setPlayList(listInfoVideo.get(link).getPlayList().concat(",".concat(PLAYLIST_NAME)));
						continue;
					}
					
					youtobe.setLinkVideo(ChanelConstants.LINK_YOUTOBE
							.concat(link));
					
					youtobe.setChanel(listVideo.getJSONObject(j)
										.getJSONObject("playlistVideoRenderer")
										.getJSONObject("shortBylineText")
										.getJSONArray("runs")
										.getJSONObject(0)
										.getString("text"));
					
					
					youtobe.setNameVideo(listVideo.getJSONObject(j)
							.getJSONObject("playlistVideoRenderer")
							.getJSONObject("title")
							.getString("simpleText"));
					
					youtobe.setTimeVideo(listVideo.getJSONObject(j)
							.getJSONObject("playlistVideoRenderer")
							.getJSONObject("lengthText")
							.getString("simpleText"));
					
					youtobe.setPlayList(PLAYLIST_NAME);
					
					listInfoVideo.put(link, youtobe);
				} catch (Exception e) {
					continue;
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage() + "method getFirstListVideoInfo");
		}
	}
	
	
	public void getListVideoInfo(JSONArray jsonArrayParent, Map<String, InfoVideoUpload> listInfoVideo) {
		try {
			JSONArray listVideo = jsonArrayParent.getJSONObject(1)
					.getJSONObject("response")
					.getJSONObject("continuationContents")
					.getJSONObject("playlistVideoListContinuation")
					.getJSONArray("contents");
			
			for (int j = 0; j < listVideo.length(); j++) {
				try {
					InfoVideoUpload youtobe = new InfoVideoUpload();
					youtobe.setId(j);
					
					String link = listVideo.getJSONObject(j)
							.getJSONObject("playlistVideoRenderer")
							.getString("videoId");
					
					if(checkKey(link, listInfoVideo)) {
						if (listInfoVideo.get(link).getPlayList() == null) {
							listInfoVideo.get(link).setPlayList(PLAYLIST_NAME);
						} else {
							listInfoVideo.get(link).setPlayList(listInfoVideo.get(link).getPlayList().concat(",".concat(PLAYLIST_NAME)));
						}
						continue;
					}
					
					youtobe.setLinkVideo(ChanelConstants.LINK_YOUTOBE.concat(link));
					
					youtobe.setChanel(listVideo.getJSONObject(j)
							.getJSONObject("playlistVideoRenderer")
							.getJSONObject("shortBylineText")
							.getJSONArray("runs")
							.getJSONObject(0)
							.getString("text"));
					
					youtobe.setNameVideo(listVideo.getJSONObject(j)
								.getJSONObject("playlistVideoRenderer")
								.getJSONObject("title")
								.getString("simpleText"));
	
					youtobe.setTimeVideo(listVideo.getJSONObject(j)
							.getJSONObject("playlistVideoRenderer")
							.getJSONObject("lengthText")
							.getString("simpleText"));
					
					youtobe.setPlayList(PLAYLIST_NAME);
					
					listInfoVideo.put(link, youtobe);
				} catch (Exception e) {
					continue;
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage() + "method getListVideoInfo" );
		}
	}
	
	
	public static void main(String[] args) {
/*		PlaylistService sv =new PlaylistService();
		Map<String, InfoVideoUpload> list = new HashMap<String, InfoVideoUpload>();
		sv.mappingVideoOfPlaylist("https://www.youtube.com/playlist?list=PLmHAaC9TU7sOWL2qqng-b-manW2FhsGu_", list);
		ExportDataUntil ex = new ExportDataUntil();
		System.out.println(ex.exportJson("", "", list));
		System.out.println(list.size());*/
		/*System.out.println(exp.exportJson("123", "playlist", list));*/
	}
	
}
