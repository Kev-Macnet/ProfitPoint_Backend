/**
 * Created on 2021/2/4.
 */
package tw.com.leadtek.nhiwidget.model.rdb;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

/**
 * 因應各table 全站搜尋所需要的 class
 * 
 * @author kenlai
 *
 */
public class MultiQuerySpec {

  public static Specification<IP_D> method1() {

    return new Specification<IP_D>() {

      private static final long serialVersionUID = 4921106329873919653L;

      @Override
      public Predicate toPredicate(Root<IP_D> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        // 1.混合條件查詢
        Path<String> expROC_ID = root.get("ROC_ID");
        Path<String> expPART_CODE = root.get("PART_CODE");
        Path<String> expFUNC_TYPE = root.get("FUNC_TYPE");
        Path<String> expPRSN_ID = root.get("PRSN_ID");
        Predicate predicate = cb.or(cb.like(expROC_ID, "ROC_ID%"));
        return cb.or(predicate, cb.or(cb.equal(expPART_CODE, "") ));
        //return cb.or(predicate, cb.equal(exp3, "kkk"));

        /*
         * 類似的sql語句為: Hibernate: select count(task0_.id) as col_0_0_ from tb_task task0_ where (
         * task0_.task_name like ? ) and task0_.create_time<? or task0_.task_detail=?
         */
      }

    };
  }
}
