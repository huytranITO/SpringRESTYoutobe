package vn.vmgmedia.youtobe.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.vmgmedia.youtobe.model.InfoPlaylistUpload;
import vn.vmgmedia.youtobe.model.InfoVideoUpload;

/** Get info video
 *  Get info playlist
 * @author Huy.Tho
 * 
 * */
@Service
public class MappingInfoService {
	
	private static final Logger logger = Logger.getLogger(MappingInfoService.class);

	public static String chanel = "";
	
	private static String version = "";
	
	private PlaylistService playlistService;
	
	private VideoService videoService;
	
	@Autowired
	public MappingInfoService(PlaylistService playlistService, VideoService videoService) {
		this.playlistService = playlistService;
		this.videoService = videoService;
	}

	public MappingInfoService() {
		super();
	}

	public  Map<String, InfoVideoUpload>  mergeData(String linkChanel) {

		PlaylistService playlistService = new PlaylistService();
		
		VideoService videoService = new VideoService();
		
		MappingInfoService mapping = new MappingInfoService();
		
		// list video info in page videos
		Map<String, InfoVideoUpload> listInfoVideo = new HashMap<String, InfoVideoUpload>();
		
		// list playlist info in page playlist
		Map<String, InfoVideoUpload> listInfoMappingVideo = new HashMap<String, InfoVideoUpload>();
		
		// list mapping
		List<InfoPlaylistUpload> listInfoPlaylist = new ArrayList<InfoPlaylistUpload>();
		
		
		// Mapping data
		listInfoMappingVideo = playlistService.getVideoMappingPlaylist(linkChanel);
		
		// Get video
		videoService.getInfoVideoChanel(linkChanel, listInfoMappingVideo, listInfoVideo);
		
		
		return listInfoVideo;
	}
}
