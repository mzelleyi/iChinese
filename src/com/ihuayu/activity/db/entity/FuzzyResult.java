/**
 * 
 */
package com.ihuayu.activity.db.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lixingwang
 *
 */
public class FuzzyResult {
	
	private List<Dictionary> dictionaryList;
	private boolean isExactResult;
	
	public FuzzyResult() {
		dictionaryList = new ArrayList<Dictionary>();
	}

	public List<Dictionary> getDictionaryList() {
		return dictionaryList;
	}

	public void setDictionaryList(List<Dictionary> dictionaryList) {
		if(dictionaryList != null) {
			this.dictionaryList = dictionaryList;
		}
	}

	public boolean isExactResult() {
		return isExactResult;
	}

	public void setExactResult(boolean isExactResult) {
		this.isExactResult = isExactResult;
	}

}
