package tw.com.leadtek.nhiwidget.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import tw.com.leadtek.nhiwidget.model.rdb.MR_SO;

public interface MR_SODao extends JpaRepository<MR_SO, Long> {

  public MR_SO findByInhNo(String inhNo);
}
