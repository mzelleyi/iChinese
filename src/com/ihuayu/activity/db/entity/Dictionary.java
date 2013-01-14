/**
 * 
 */
package com.ihuayu.activity.db.entity;

/**
 * @author lixingwang
 *
 */
public class Dictionary {
	private int id;
	private String language_dir;
	private String keyword;
	private String keyword_len;
	private String src;
	private String destiontion;
	private String chinese_audio;
	private String chineser_tone_py;
	private String dic_catagory;
	private String sample_sentance_en;
	private String sample_sentence_ch;
	private String sample_sentance_py;
	private String sample_sentance_audio;
	
//	public String getId() {
//		return id;
//	}
//	public void setId(String id) {
//		this.id = id;
//	}
	
	public String getLanguage_dir() {
		return language_dir;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setLanguage_dir(String language_dir) {
		this.language_dir = language_dir;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getKeyword_len() {
		return keyword_len;
	}
	public void setKeyword_len(String keyword_len) {
		this.keyword_len = keyword_len;
	}
	public String getSrc() {
		return src;
	}
	public void setSrc(String src) {
		this.src = src;
	}
	public String getDestiontion() {
		return destiontion;
	}
	public void setDestiontion(String destiontion) {
		this.destiontion = destiontion;
	}
	public String getChinese_audio() {
		return chinese_audio;
	}
	public void setChinese_audio(String chinese_audio) {
		this.chinese_audio = chinese_audio;
	}
	public String getDic_catagory() {
		return dic_catagory;
	}
	public void setDic_catagory(String dic_catagory) {
		this.dic_catagory = dic_catagory;
	}
	public String getChineser_tone_py() {
		return chineser_tone_py;
	}
	public void setChineser_tone_py(String chineser_tone_py) {
		this.chineser_tone_py = chineser_tone_py;
	}
	public String getSample_sentance_en() {
		return sample_sentance_en;
	}
	public void setSample_sentance_en(String sample_sentance_en) {
		this.sample_sentance_en = sample_sentance_en;
	}
	public String getSample_sentence_ch() {
		return sample_sentence_ch;
	}
	public void setSample_sentence_ch(String sample_sentence_ch) {
		this.sample_sentence_ch = sample_sentence_ch;
	}
	public String getSample_sentance_py() {
		return sample_sentance_py;
	}
	public void setSample_sentance_py(String sample_sentance_py) {
		this.sample_sentance_py = sample_sentance_py;
	}
	public String getSample_sentance_audio() {
		return sample_sentance_audio;
	}
	public void setSample_sentance_audio(String sample_sentance_audio) {
		this.sample_sentance_audio = sample_sentance_audio;
	}
}
