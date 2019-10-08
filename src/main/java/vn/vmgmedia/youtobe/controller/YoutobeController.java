package vn.vmgmedia.youtobe.controller;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import vn.vmgmedia.youtobe.common.ChanelConstants;
import vn.vmgmedia.youtobe.common.ExportDataUntil;
import vn.vmgmedia.youtobe.model.InfoVideoUpload;
import vn.vmgmedia.youtobe.service.MappingInfoService;
import vn.vmgmedia.youtobe.service.VideoService;

/** Controller
 * @author Huy.Tho
 * 
 * */
@RestController
public class YoutobeController {
	
	@Value("${storage.location}")
	private String pathFileFolder;
	
	
	@Autowired
	private MappingInfoService mappingInfoService;
	
	@Autowired
	private VideoService videoService;
	
	@PostMapping(path= "/videos/chanel", consumes = "application/json", produces = "application/json")
	public String getVideoInfoChanel(@RequestBody String chanel) throws Exception {
		JSONObject jsObject = new JSONObject(chanel);
		
		String linkChanel = jsObject.getString("chanel");

		String [] tmpChanel = linkChanel.split("/");
		
		String nameChanel = tmpChanel[tmpChanel.length-1];
		
		Map<String, InfoVideoUpload> listInfoVideo = mappingInfoService.mergeData(linkChanel);
		
		ExportDataUntil exp = new ExportDataUntil();
		String fileName = "\\"+exp.timeSystem().concat(nameChanel.concat(".xlsx"));
		
		String fileExport = exp.wirteFileExel(pathFileFolder, fileName, listInfoVideo);
		JSONObject json = exp.exportJson(fileExport, ChanelConstants.TYPES, listInfoVideo);
        
		return json.toString();
	}
	 
	@PostMapping(path= "/videos/chanel/playlist", consumes = "application/json", produces = "application/json")
	public String getVideoInfoPlaylist(@RequestBody String chanel) throws Exception {
		
		String user ="abc"+chanel;
        
		return chanel;
	}
}
