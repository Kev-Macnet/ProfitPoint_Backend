package tw.com.leadtek.nhiwidget.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import tw.com.leadtek.nhiwidget.model.rdb.ICDCM_ORDER;

public interface ICDCM_ORDERDao extends JpaRepository<ICDCM_ORDER, String> {

	/**
	* @return
	*/
	@Query(value = "SELECT * FROM ICDCM_ORDER WHERE DATA_FORMAT = ?1", nativeQuery = true)
	public List<ICDCM_ORDER> queryByDataFormat(String dataFormat);
}
