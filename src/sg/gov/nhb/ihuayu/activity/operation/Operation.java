/**
 * 
 */

package sg.gov.nhb.ihuayu.activity.operation;

import java.util.List;

/**
 * @author lixingwang
 */
public interface Operation {

    public List<?> query(String condition, String[] params);

    public List<?> query(String[] conlums, String condition, String[] params);

    public List<?> query(String condition, String[] params, String groupBy, String having,
            String orderBy);

    public List<?> query(String[] conlums, String condition, String[] params, String groupBy,
            String having, String orderBy);

    // public List<Dictionary> queryDictionary(String condition, String[]
    // params);
    // public List<Dictionary> queryDictionary(String[] conlums, String
    // condition, String[] params);
    // public List<Dictionary> queryDictionary(String condition, String[]
    // params, String groupBy, String having, String orderBy);
    // public List<Dictionary> queryDictionary(String[] conlums, String
    // condition, String[] params, String groupBy, String having, String
    // orderBy);
}
