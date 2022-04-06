package tw.com.leadtek.nhiwidget.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import tw.com.leadtek.nhiwidget.model.rdb.ICDCM_DRUG;

public interface ICDCM_DRUGDao  extends JpaRepository<ICDCM_DRUG, String>{
	
	/**
	* @return
	*/
	@Query(value = "SELECT * FROM ICDCM_DRUG WHERE DATA_FORMAT = ?1", nativeQuery = true)
	public List<ICDCM_DRUG> queryByDataFormat(String dataFormat);

}
