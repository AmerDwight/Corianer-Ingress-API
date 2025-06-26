package tw.amer.cia.core.common.utility;

import tw.amer.cia.core.common.utility.item.DbConfig;
import tw.amer.cia.core.common.utility.item.DbProxy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.sql.*;
import java.util.*;
import java.util.function.Consumer;

@Slf4j
public class SqlBatchQueryExecutor {
    /**
     * 執行 SQL 查詢，並分批讀取資料，使用外部 Consumer 處理資料.
     *
     * @param db                資料庫連線配置
     * @param sqlStatement      SQL 查詢語句
     * @param dbProxy           資料庫代理配置 (可為 null)
     * @param batchSize         每批次處理的資料量
     * @param batchDataConsumer 資料批次處理 Consumer
     */

    public static void batchedQueryExecutor(DbConfig db, String sqlStatement, @Nullable DbProxy dbProxy, int batchSize, Consumer<List<Map<String, Object>>> batchDataConsumer) {
        if (batchSize <= 0) {
            throw new IllegalArgumentException("batchSize must be a positive integer.");
        }
        if (batchDataConsumer == null) {
            throw new IllegalArgumentException("batchDataConsumer cannot be null.");
        }

        // 連線字串、連線屬性、Proxy 設定
        StringBuilder url = new StringBuilder("jdbc:oracle:thin:@");
        if(StringUtils.isNotEmpty(db.getSid())){
            url.append(db.getAddress()).append(":").append(db.getPort()).append(":").append(db.getSid());
        } else if (StringUtils.isNotEmpty(db.getServerName())) {
            url.append("//").append(db.getAddress()).append(":").append(db.getPort()).append("/").append(db.getServerName());
        }

        // 設置連線屬性
        Properties props = new Properties();
        props.setProperty("user", db.getAccount());
        props.setProperty("password", db.getPassword());

        // 設置 Proxy
        if(dbProxy != null){
            props.setProperty("oracle.net.http_proxy", "true");
            props.setProperty("oracle.net.http_proxy_host", dbProxy.getProxyHost());
            props.setProperty("oracle.net.http_proxy_port",  dbProxy.getProxyPort().toString());

            if(dbProxy.isRequireAuth()){
                props.setProperty("oracle.net.proxy_user", dbProxy.getProxyAccount());
                props.setProperty("oracle.net.proxy_password", dbProxy.getProxyPassword());
            }
        }

        // 開始進行 Batch 處理
        List<Map<String, Object>> dataBatch = new ArrayList<>();
        int rowCountInBatch = 0;
        int batchCount = 0;

        try (Connection conn = DriverManager.getConnection(url.toString(), props);
             Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(sqlStatement)) {

            log.debug("Connected to the database!");

            while (resultSet.next()) {
                Map<String, Object> resultMap = new HashMap<>();
                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    String columnName = resultSet.getMetaData().getColumnName(i);
                    Object columnValue = resultSet.getObject(i);
                    resultMap.put(columnName, columnValue);
                }
                dataBatch.add(resultMap);
                rowCountInBatch++;

                if (rowCountInBatch >= batchSize) {
                    batchCount++;
                    log.info("Processing batch: {}", batchCount);
                    batchDataConsumer.accept(dataBatch); // 使用 Consumer 處理資料
                    dataBatch.clear();
                    rowCountInBatch = 0;
                }
            }

            if (!dataBatch.isEmpty()) {
                batchCount++;
                log.info("Processing final batch: {}", batchCount);
                batchDataConsumer.accept(dataBatch); // 處理最後一批
            }

        } catch (SQLException ex) {
            log.error("SQLException: " + ex.getMessage());
            log.error("SQLState: " + ex.getSQLState());
            log.error("VendorError: " + ex.getErrorCode());
        }

    }
}
