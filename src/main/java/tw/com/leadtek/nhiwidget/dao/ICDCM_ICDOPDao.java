package tw.com.leadtek.nhiwidget.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import tw.com.leadtek.nhiwidget.model.rdb.ICDCM_ICDOP;

public interface ICDCM_ICDOPDao extends JpaRepository<ICDCM_ICDOP, String>{

	/**
	* @return
	*/
	@Query(value = "SELECT * FROM ICDCM_ICDOP WHERE DATA_FORMAT = ?1", nativeQuery = true)
	public List<ICDCM_ICDOP> queryByDataFormat(String dataFormat);
	/**
	 * 門診
	 * @param icdcm
	 * @return
	 */
	@Query(value = "SELECT * FROM ICDCM_ICDOP WHERE DATA_FORMAT = 10 and ICDCM = ?1 order by TOTAL desc ", nativeQuery = true)
	public List<ICDCM_ICDOP> queryClinicOperation(String icdcm);
	/**
	 * 住院
	 * @param icdcm
	 * @return
	 */
	@Query(value = "SELECT * FROM ICDCM_ICDOP WHERE DATA_FORMAT = 20 and ICDCM = ?1 order by TOTAL ", nativeQuery = true)
	public List<ICDCM_ICDOP> queryHospitalOperation(String icdcm);
}
