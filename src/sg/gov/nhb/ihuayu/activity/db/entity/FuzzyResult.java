/**
 * 
 */
package sg.gov.nhb.ihuayu.activity.db.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lixingwang
 *
 */
public class FuzzyResult {
	
	private List<Dictionary> dictionaryList;
	private List<Scenario> scenarioList;
	private boolean isExactResult;
	
	public FuzzyResult() {
		dictionaryList = new ArrayList<Dictionary>();
		scenarioList = new ArrayList<Scenario>();
	}

	public List<Dictionary> getDictionaryList() {
		return dictionaryList;
	}

	public void setDictionaryList(List<Dictionary> dictionaryList) {
		if(dictionaryList != null) {
			this.dictionaryList = dictionaryList;
		}
	}
	
	public List<Scenario> getScenarioList() {
		return scenarioList;
	}

	public void setScenarioList(List<Scenario> scenarioList) {
		if(scenarioList != null) {
			this.scenarioList = scenarioList;
		}
	}

	public boolean isExactResult() {
		return isExactResult;
	}

	public void setExactResult(boolean isExactResult) {
		this.isExactResult = isExactResult;
	}
	
}
