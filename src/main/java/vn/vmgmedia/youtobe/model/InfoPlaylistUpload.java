package vn.vmgmedia.youtobe.model;

public class InfoPlaylistUpload {
	
	public String id;
	
	public String typePlaylist;
	
	public String namePlayList;

	public InfoPlaylistUpload(String id, String typePlaylist, String namePlayList) {
		super();
		this.id = id;
		this.typePlaylist = typePlaylist;
		this.namePlayList = namePlayList;
	}

	public InfoPlaylistUpload() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTypePlaylist() {
		return typePlaylist;
	}

	public void setTypePlaylist(String typePlaylist) {
		this.typePlaylist = typePlaylist;
	}

	public String getNamePlayList() {
		return namePlayList;
	}

	public void setNamePlayList(String namePlayList) {
		this.namePlayList = namePlayList;
	}

	@Override
	public String toString() {
		return "InfoPlaylistUpload [id=" + id + ", typePlaylist=" + typePlaylist + ", namePlayList=" + namePlayList
				+ "]";
	}
	
}
