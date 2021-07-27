package tw.com.leadtek.nhiwidget.sql;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import tw.com.leadtek.tools.Utility;


@Repository
public class PaymentTermsDao {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    protected JdbcTemplate jdbcTemplate;

    public java.util.List<Map<String, Object>> searchPaymentTerms(String feeNo, String nhiNo, String category, 
            java.util.Date startDate, java.util.Date endDate) {
        String strStart = Utility.dateFormat(startDate, "yyyy/MM/dd");
        String strEnd = Utility.dateFormat(endDate, "yyyy/MM/dd");
        
        String sql;
        sql = "SELECT ID, FEE_NO, FEE_NAME, NHI_NO, NHI_NAME, START_DATE, END_DATE, CATEGORY, HOSPITAL_TYPE, OUTPATIENT_TYPE, HOSPITALIZED_TYPE\r\n"
                + "FROM PT_PAYMENT_TERMS\r\n"
                + "Where (1=1)\r\n"
                + " -- and (FEE_NO like '%s%%')\r\n"
                + " -- and (NHI_NO  like '%s%%')\r\n"
                + " -- and (CATEGORY='%s')\r\n"
                + " -- and (START_DATE='%s')\r\n"
                + " -- and (END_DATE='%s')";
        sql = String.format(sql, feeNo, nhiNo, category, strStart,strEnd, strStart,strEnd, strStart,strEnd);
        if (feeNo.length()>0) {
            sql=sql.replace("-- and (FEE_NO", " and (FEE_NO");
        }
        if (nhiNo.length()>0) {
            sql=sql.replace("-- and (NHI_NO", " and (NHI_NO");
        }
        if (category.length()>0) {
            sql=sql.replace("-- and (CATEGORY", " and (CATEGORY");
        }
        if (strStart.length()>0) {
          sql=sql.replace("-- and (START_DATE=", " and (START_DATE=");
        }
        if (strEnd.length()>0) {
          sql=sql.replace("-- and (END_DATE=", " and (END_DATE=");
        }
        logger.info(sql);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        
        return Utility.listLowerCase(lst);
    }


}
