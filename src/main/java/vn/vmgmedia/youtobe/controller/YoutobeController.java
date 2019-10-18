package vn.vmgmedia.youtobe.controller;

import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import vn.vmgmedia.youtobe.common.ChanelConstants;
import vn.vmgmedia.youtobe.common.ExportDataUntil;
import vn.vmgmedia.youtobe.model.InfoVideoUpload;
import vn.vmgmedia.youtobe.service.MappingInfoService;
import vn.vmgmedia.youtobe.service.PlaylistService;
import vn.vmgmedia.youtobe.service.VideoService;

/** Controller
 * @author Huy.Tho
 * 
 * */
@CrossOrigin("http://localhost:8080")
@RestController
public class YoutobeController {
	
	private static final Logger logger = LoggerFactory.getLogger(PlaylistService.class); 
	
	@Value("${storage.location}")
	private String pathFileFolder;
	
	
	@Autowired
	private MappingInfoService mappingInfoService;
	
	@Autowired
	private PlaylistService playlistService;
	
	@Autowired
	private VideoService videoService;
	
	@PostMapping(path= "/videos/chanel", consumes = "application/json", produces = "application/json")
	public String getVideoInfoChanel(@RequestBody String chanel) throws Exception {
		logger.info("Post request playlist link: ", chanel);
		
		JSONObject jsObject = new JSONObject(chanel);
		
		String linkChanel = jsObject.getString("chanel");
		System.out.println(linkChanel);
		String [] tmpChanel = linkChanel.split("/");
		
		String nameChanel = tmpChanel[tmpChanel.length-1];
		
		Map<String, InfoVideoUpload> listInfoVideo = new HashedMap<String, InfoVideoUpload>();
		listInfoVideo = videoService.getListVideo(linkChanel, listInfoVideo);
		
		ExportDataUntil exp = new ExportDataUntil();
		String fileName = "\\"+exp.timeSystem().concat(nameChanel.concat("videos.xlsx"));
		
		String fileExport = exp.wirteFileExel(pathFileFolder, fileName, listInfoVideo);
		JSONObject json = exp.exportJson(fileExport, ChanelConstants.TYPES, listInfoVideo);
		logger.info("Playlist info return : ", json);
		return json.toString();
	}
	 
	@PostMapping(path= "/videos/playlists", consumes = "application/json", produces = "application/json")
	public String getVideoInfoPlaylist(@RequestBody String chanel) throws Exception {
		logger.info("Post request playlist link: ", chanel);
		
		JSONObject jsObject = new JSONObject(chanel);
		
		String linkChanel = jsObject.getString("chanel");

		String [] tmpChanel = linkChanel.split("/");
		
		String nameChanel = tmpChanel[tmpChanel.length-1];
		
		Map<String, InfoVideoUpload> listInfoVideo = new HashedMap<String, InfoVideoUpload>();
		listInfoVideo = playlistService.getVideoMappingPlaylist(linkChanel);
		
		ExportDataUntil exp = new ExportDataUntil();
		String fileName = "\\"+exp.timeSystem().concat(nameChanel.concat("playlist.xlsx"));
		
		String fileExport = exp.wirteFileExel(pathFileFolder, fileName, listInfoVideo);
		JSONObject json = exp.exportJson(fileExport, ChanelConstants.TYPES, listInfoVideo);
		
		logger.info("Playlist info return : ", json);
		return json.toString();
	}
	
	@PostMapping(path= "/videos/playlist", consumes = "application/json", produces = "application/json")
	public String getInfoPlaylist(@RequestBody String chanel) throws Exception {
		logger.info("Post request playlist link: ", chanel);
		
		JSONObject jsObject = new JSONObject(chanel);
		
		String linkChanel = jsObject.getString("chanel");
		
		Map<String, InfoVideoUpload> listInfoVideo = playlistService.mappingVideoOfPlaylist(linkChanel);
		
		String namePlaylist = "";
		for (Map.Entry<String, InfoVideoUpload> video : listInfoVideo.entrySet()) {
			if(video.getValue().getChanel() != null) {
				namePlaylist = video.getValue().getChanel().replace(" ", "");
				break;
			}
		}
		
		ExportDataUntil exp = new ExportDataUntil();
		String fileName = "\\"+exp.timeSystem().concat(namePlaylist.concat("playlist.xlsx"));
		String fileExport = exp.wirteFileExel(pathFileFolder, fileName, listInfoVideo);
		JSONObject json = exp.exportJson(fileExport, ChanelConstants.TYPES, listInfoVideo);
        
		logger.info("Playlist info return : ", json);
		return json.toString();
	}
}
