package tw.com.leadtek.nhiwidget.sql;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import tw.com.leadtek.tools.Utility;


@Repository
public class WebConfigDao extends BaseSqlDao {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    public java.util.Map<String, Object> getConfig(String id) {
        String sql;
        sql="Select *  \n" + 
            "From webconfig \n" + 
            "Where(id = '%s')";
        sql = String.format(sql, id);
        
        java.util.Map<String, Object> retMap;
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        if (lst.size() > 0)
            retMap = Utility.mapLowerCase(lst.get(0));
        else
            retMap = new java.util.HashMap<String, Object>();
        return (retMap);
    }

    public String getConfigValue(String id) {
        String ret = "";
        String sql;
        sql="Select *  \n" + 
            "From webconfig \n" + 
            "Where(id = '%s')";
        sql = String.format(sql, id);
        
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        if (lst.size() > 0) {
            java.util.Map<String, Object> map = Utility.mapLowerCase(lst.get(0));
            if (map.get("value") != null) {
                ret = map.get("value").toString();
            }
        }
        return (ret);
    }
    
    
    public int setConfig(String id, String val, String desc) {
        String sql;
        sql = "Update webconfig \n" + 
              "Set value='%s', \n" + 
              "    description='%s', \n" +
              "    update_tm=CURRENT_TIMESTAMP\n"+
              "Where(id = '%s')";
        sql = String.format(sql, noInjection(val), noInjection(desc), noInjection(id));
        int ret = jdbcTemplate.update(sql);
        if (ret == 0) {
            sql = "Insert Into \n" + 
                  "webconfig(id,value,description,update_tm) \n"+
                  "Values ('%s','%s','%s',CURRENT_TIMESTAMP)";
            sql = String.format(sql, noInjection(id), noInjection(val), noInjection(desc));
            ret = jdbcTemplate.update(sql);
        }
        
        return (ret);
    }
    
    


}
