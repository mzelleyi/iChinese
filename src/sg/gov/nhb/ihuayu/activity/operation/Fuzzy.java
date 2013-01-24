package sg.gov.nhb.ihuayu.activity.operation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sg.gov.nhb.ihuayu.activity.db.entity.Dictionary;

import android.annotation.SuppressLint;


/**
 * 
 */

/**
 * @author lixingwang
 *
 */
@SuppressLint("DefaultLocale")
public class Fuzzy {

	public List<Dictionary> fuzzySearch(String keyword, String langDir, IhuayuOperationImpl operation){

		// 模糊匹配精度
		double kFuzzyMatchPercent = 0.4;

		// 模糊匹配结果集截取
		double kFuzzyResultsMaxCount = 15;

		// 计算模糊匹配精度
		int nMaxAllowed = (int)(kFuzzyMatchPercent * keyword.length());
		// 抽取模糊串
		String strSubText  = keyword.substring(0, nMaxAllowed);//[keyword substringWithRange:NSMakeRange(0,  nMaxAllowed)];

		// 查询数据库
		String querySql = "SELECT * FROM Dictionary WHERE keyword LIKE ? AND language_dir= ? AND keyword_length >= ? AND keyword_length <= ? ORDER BY dict_category ASC, src ASC";
		
		String[] param = new String[]{strSubText + "%", langDir, (keyword.length() - nMaxAllowed) + "",(nMaxAllowed + keyword.length())+""};
		

		// 根据SQL查询的结果集results，进行模糊匹配。
		List<Dictionary> results = operation.queryDictionary(querySql, param);
		// 计算results中每项item的distance
		List<Dictionary> finalResults = new ArrayList<Dictionary>();
		List<Integer> distances = new ArrayList<Integer>(); 
		for(Dictionary dictionary : results){
			
			String subKeyword = dictionary.getKeyword();
			
			int distance = calcDistenceByChar(subKeyword.toLowerCase(), keyword.toLowerCase(), nMaxAllowed);
		    if (distance > nMaxAllowed || distance <= 0) {
		        continue; //overflow cost, skip the word
		    }
			
			// set item's distance
//			item.distance = distance;
		    dictionary.setDistance(distance);
		    distances.add(distance);
		    finalResults.add(dictionary);
		}
		
		Collections.sort(distances);
		List<Dictionary> finalSortResults = new ArrayList<Dictionary>();
		if( finalResults.size() > 0)
		{
			for(int distance : distances) {
				finalSortResults.add(getDictionary(distance, finalResults));
				if(finalSortResults.size() >= kFuzzyResultsMaxCount) break;
			}
			// 根据item.distance 降序（ASC）排列结果集finalResults。如果排序后的结果集大于kFuzzyResultsMaxCount（15）项，则截取排序后的结果集的前kFuzzyResultsMaxCount （15）项
			// 此处省略代码若干…O(∩_∩)O~…
		}

	 return finalSortResults;
	}

	public Dictionary getDictionary(int distance, List<Dictionary> dictionaries) {
		for(Dictionary dic : dictionaries) {
			if(distance == dic.getDistance()){
				dictionaries.remove(dic);
				return dic; 
			}
		}
		return null;
	}
	
	int calcDistenceByChar(String s1, String s2, int nMaxCostAllow)
	{
	    if (s1.length() < s2.length())
	    {
	        // 这个需要把NSString替换为JAVA string class?
	        String  s = s1;
	        s1 = s2;
	        s2 = s;
	    }
	    
	    int nNewRow = (s1.length() + 1);
	    int nNewCol = (s2.length() + 1);
	    int[] sqrDists0 = new int[nNewRow];
	    int[] sqrDists1 = new int[nNewCol];
//	    memset(sqrDists0, 0, nNewCol * sizeof(int));
//	    memset(sqrDists1, 0, nNewCol * sizeof(int));
	    for (int j = 1; j < nNewCol; j++) { sqrDists0[j] = j; sqrDists1[j] = j; }
	    
	    int[] top = null;
	    int[] bottom = null;
	    int MinIdx = 0;
	    for (int i = 1; i < nNewRow; i++)
	    {
	        int nBegin = ((MinIdx - nMaxCostAllow + 1 > 1) ? (MinIdx - nMaxCostAllow + 1) : 1);
	        int nEnd = ((MinIdx + nMaxCostAllow + 1 < nNewCol - 1) ? (MinIdx + nMaxCostAllow + 1) : nNewCol - 1);
	        
	        if (i % 2 == 0)
	        {
	            top = sqrDists1;
	            bottom = sqrDists0;
	        }
	        else
	        {
	            top = sqrDists0;
	            bottom = sqrDists1;
	        }
	        
	        MinIdx = (nBegin - 1);
	        
	        
	        if (nBegin == 1)
	            bottom[0] = i;
	        else
	        {
	            int nCost = 0;
	            if (s1.toCharArray()[i - 1] != s2.toCharArray()[nBegin - 2])
	                nCost = 1;
	            
	            bottom[nBegin - 1] = Math.min(top[nBegin - 1] + 1, top[nBegin - 2] + nCost);
	        }
	        
	        
	        for (int j = nBegin; j <= nEnd; j++)
	        {
	            int nCost = 0;
	            if (s1.toCharArray()[i - 1] != s2.toCharArray()[j - 1])
	            {
	                nCost = 1;
	            }
	            
	            int nUp = (top[j] + 1);
	            int nLeft = (bottom[j - 1] + 1);
	            int nUpLeft = (top[j - 1] + nCost);
	            
	            if (nUpLeft <= nUp && nUpLeft <= nLeft)
	                bottom[j] = nUpLeft;
	            else if (nUp <= nLeft && nUp <= nUpLeft)
	                bottom[j] = nUp;
	            else
	                bottom[j] = nLeft;
	            //                    bottom[j] = Math.Min(Math.Min(nUp, nLeft), nUpLeft);
	            
	            MinIdx = bottom[MinIdx] > bottom[j] ? j : MinIdx;
	        }
	        
	        
	        if (bottom[MinIdx] > nMaxCostAllow)
	        {
//	            free(sqrDists0);
//	            free(sqrDists1);
	            
	            return -2; //cost too big (overflow)
	        }
	    }
	    
	    return bottom[s2.length()];
	}



}
