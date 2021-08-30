package tw.com.leadtek.nhiwidget.sql;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import tw.com.leadtek.tools.Utility;


@Repository
public class WebConfigDao {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    public java.util.Map<String, Object> getConfig(String id) {
        String sql;
        sql="Select * \r\n" + 
            "From webconfig\r\n" + 
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
        sql="Select * \r\n" + 
            "From webconfig\r\n" + 
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
        sql = "Update webconfig\r\n" + 
              "Set value='%s',\r\n" + 
              "    description='%s'\r\n" + 
              "Where(id = '%s')";
        sql = String.format(sql, val, desc, id);
        int ret = jdbcTemplate.update(sql);
        if (ret == 0) {
            sql = "Insert Into\r\n" + 
                  "webconfig(id,value,description)\r\n"+
                  "Values ('%s','%s','%s')";
            sql = String.format(sql, id, val, desc);
            ret = jdbcTemplate.update(sql);
        }
        
        return (ret);
    }
    
    


}
