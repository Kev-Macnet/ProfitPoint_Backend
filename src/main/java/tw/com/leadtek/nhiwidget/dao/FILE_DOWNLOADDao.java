/**
 * Created on 2022/2/24.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import tw.com.leadtek.nhiwidget.model.rdb.FILE_DOWNLOAD;

public interface FILE_DOWNLOADDao extends JpaRepository<FILE_DOWNLOAD, Long> {

  public List<FILE_DOWNLOAD> findByFilenameAndFileType(String filename, String fileType);
  
  /**
   * 取得仍在處理中的檔案
   */
  public List<FILE_DOWNLOAD> findAllByProgressLessThan(int progress);
  
  public List<FILE_DOWNLOAD> findAllByOrderByUpdateAtDesc();
}
