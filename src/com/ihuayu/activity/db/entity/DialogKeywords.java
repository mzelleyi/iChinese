/**
 * 
 */
package com.ihuayu.activity.db.entity;

/**
 * @author lixingwang
 *
 */
public class DialogKeywords {
	private String id;
	private String dialog_id;
	private String src_keyword;
	private String dest_keyword;
	private String keyword_py;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDialog_id() {
		return dialog_id;
	}
	public void setDialog_id(String dialog_id) {
		this.dialog_id = dialog_id;
	}
	public String getSrc_keyword() {
		return src_keyword;
	}
	public void setSrc_keyword(String src_keyword) {
		this.src_keyword = src_keyword;
	}
	public String getDest_keyword() {
		return dest_keyword;
	}
	public void setDest_keyword(String dest_keyword) {
		this.dest_keyword = dest_keyword;
	}
	public String getKeyword_py() {
		return keyword_py;
	}
	public void setKeyword_py(String keyword_py) {
		this.keyword_py = keyword_py;
	}
}
