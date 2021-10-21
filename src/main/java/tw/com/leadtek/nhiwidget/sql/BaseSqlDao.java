package tw.com.leadtek.nhiwidget.sql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


@Repository
public class BaseSqlDao {

    @Autowired
    protected JdbcTemplate jdbcTemplate;
    
    public String noInjection(String str) {
        if (str!=null) {
            return (str.replaceAll("\'", "\'\'"));
        } else {
            return str;
        }
    }

    public long newTableId_l(String tbName, String fdName) {
        long lastID = 0;
        String s1;
        s1 = "Select Max(" + fdName + ") as lastid \n" +
             "From " + tbName;
        try {
            lastID = jdbcTemplate.queryForObject(s1, Long.class);
        } catch (java.lang.NullPointerException e) {
            lastID = 0;
        }
        return (lastID + 1);
    }
    
    public static String quotedNotNull(String str) {
        if (str==null) {
            return "NULL";
        } else {
            
            return "\'"+str.replaceAll("\'", "\'\'")+"\'";
        }
    }
    
    public Object[] listObjectToArray(java.util.List<Object> lst) {
        //java.util.List<Object> lst = new java.util.ArrayList<Object>();
        //lst.add(note);
        Object[] params = new Object[lst.size()];
        lst.toArray(params);
        return (params);
    }


}
