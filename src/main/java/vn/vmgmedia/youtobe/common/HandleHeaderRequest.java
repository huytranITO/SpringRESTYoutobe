package vn.vmgmedia.youtobe.common;

import org.json.JSONArray;
import org.json.JSONException;

public class HandleHeaderRequest {
	public String getTokenFirst(JSONArray jsonArrayParent) {
		try {
			return jsonArrayParent.getJSONObject(1)
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
					.getJSONArray("continuations")
					.getJSONObject(0)
					.getJSONObject("nextContinuationData")
					.getString("continuation");
		} catch (JSONException e) {
			return null;
		}
	}
	
	public String getContinueToken(JSONArray jsonArrayParent) {
		try {
			return jsonArrayParent.getJSONObject(1)
					.getJSONObject("response")
					.getJSONObject("continuationContents")
					.getJSONObject("gridContinuation")
					.getJSONArray("continuations")
					.getJSONObject(0)
					.getJSONObject("nextContinuationData")
					.getString("continuation");
		} catch (JSONException e) {
			return null;
		}
	}
	
	public String getPageFirstContinue(JSONArray jsonArrayParent) {
		try {
			return jsonArrayParent.getJSONObject(1)
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
					.getJSONArray("continuations")
					.getJSONObject(0)
					.getJSONObject("nextContinuationData")
					.getString("clickTrackingParams");
		} catch (JSONException e) {
			return null;
		}
	}
	
	public String getTokenFirstContinue(JSONArray jsonArrayParent) {
		try {
			return jsonArrayParent.getJSONObject(1)
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
					.getJSONArray("continuations")
					.getJSONObject(0)
					.getJSONObject("nextContinuationData")
					.getString("continuation");
		} catch (JSONException e) {
			return null;
		}
	}
	
	public String getPageFirstPlaylist(JSONArray jsonArrayParent) {
		try {
			return jsonArrayParent.getJSONObject(1)
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
					.getJSONArray("continuations")
					.getJSONObject(0)
					.getJSONObject("nextContinuationData")
					.getString("clickTrackingParams");
		} catch (JSONException e) {
			return null;
		}
	}
	
	public String getPageContinue(JSONArray jsonArrayParent) {
		try {
			return jsonArrayParent.getJSONObject(1)
					.getJSONObject("response")
					.getJSONObject("continuationContents")
					.getJSONObject("gridContinuation")
					.getJSONArray("continuations")
					.getJSONObject(0)
					.getJSONObject("nextContinuationData")
					.getString("clickTrackingParams");
		} catch (JSONException e) {
			return null;
		}
	}
	
}
