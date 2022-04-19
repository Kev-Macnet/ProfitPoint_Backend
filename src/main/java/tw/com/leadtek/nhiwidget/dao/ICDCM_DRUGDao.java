package tw.com.leadtek.nhiwidget.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import tw.com.leadtek.nhiwidget.model.rdb.ICDCM_DRUG;
import tw.com.leadtek.nhiwidget.model.rdb.ICDCM_DRUG_KEYS;

public interface ICDCM_DRUGDao  extends JpaRepository<ICDCM_DRUG, ICDCM_DRUG_KEYS>{
	
	/**
	* @return
	*/
	@Query(value = "SELECT * FROM ICDCM_DRUG WHERE DATA_FORMAT = ?1 order by PERCENT desc", nativeQuery = true)
	public List<ICDCM_DRUG> queryByDataFormat(String dataFormat);
	
	@Query(value = "SELECT * FROM ICDCM_DRUG WHERE DATA_FORMAT = ?1 order by PERCENT desc limit 2", nativeQuery = true)
	public List<ICDCM_DRUG> queryByDataFormatLimit(String dataFormat);
	
	@Query(value = "SELECT * FROM ICDCM_DRUG WHERE DATA_FORMAT = ?1 and IS_DRUG = ?2 order by PERCENT desc", nativeQuery = true)
	public List<ICDCM_DRUG> queryDataByDrug(String dataFormat, int i);
	
	

}
