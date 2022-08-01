package tw.com.leadtek.nhiwidget.sql;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import tw.com.leadtek.tools.Utility;


@Repository
public class BackupLogDao {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    protected JdbcTemplate jdbcTemplate;
    
    public java.util.List<Map<String, Object>> findAllById(long id, String userName) {
        String sql;
        sql = "Select ID, USERNAME, FILENAME, MODE, DESCRIPTION, UPDATE_TM\r\n"
                + "From NWUSER.BACKUP_LOG\r\n"
                + "Where (1=1)\r\n"
                + " -- and (ID=%d)\r\n"
                + " -- and (USERNAME='%s')\r\n"
                + "Order by UPDATE_TM Desc";
        sql = String.format(sql, id, userName);
        if (id>0) {
            sql = sql.replace("-- and (ID=", " and (ID=");
        }
        if (userName.length()>0) {
            sql = sql.replace("-- and (USERNAME=", " and (USERNAME=");
        }
        logger.debug(sql);
        //sql = "Select ID, USERNAME, FILENAME, MODE, DESCRIPTION, UPDATE_TM From BACKUP_LOG";
        java.util.List<Map<String, Object>> retList = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        //System.out.println("dbbackup sql:" + sql + "-- size=" + retList.size());
        return Utility.listLowerCase(retList);
    }

    public java.util.Map<String, Object> findOne(long id) {
        java.util.List<Map<String, Object>> lst = findAllById(id, "");
        if (lst.size()>0) {
            return (lst.get(0));
        } else {
//            return (new java.util.HashMap<String, Object>());
            return java.util.Collections.emptyMap();
        }

    }
    
    public int delete(long id) {
        String sql;
        sql = "Delete from BACKUP_LOG\r\n"
                + "Where (ID=%d)";
        sql = String.format(sql, id);
        logger.debug(sql);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }
    
    public int add(String username, String filename, int mode, String description) {
        String sql;
        sql = "Insert into \r\n"
                + "BACKUP_LOG(USERNAME, FILENAME, MODE, DESCRIPTION, UPDATE_TM)\r\n"
                + "Values('%s', '%s', %d, '%s', CURRENT_TIMESTAMP)";
        sql = String.format(sql, username, filename, mode, description);
        logger.debug(sql);
        try {
            int ret =  jdbcTemplate.update(sql);
            return ret;
        } catch(DataAccessException ex) {
            return -1;
        }
    }
    
    public int update(long id, String filename, String description) {
        String sql;
        sql = "Update BACKUP_LOG\r\n"
                + "SET FILENAME='%s',\r\n"
                + "    DESCRIPTION='%s'\r\n"
                + "WHERE (ID=%d)";
        sql = String.format(sql, filename, description, id);
        logger.debug(sql);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }

}
