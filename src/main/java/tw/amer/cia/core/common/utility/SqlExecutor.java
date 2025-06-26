package tw.amer.cia.core.common.utility;

import tw.amer.cia.core.common.utility.item.DbConfig;
import tw.amer.cia.core.common.utility.item.DbProxy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;

import java.sql.*;
import java.util.*;

@Slf4j
public class SqlExecutor {

    public static List<Map<String, Object>> singleQueryExecutor(DbConfig db, String sqlStatement, @Nullable DbProxy dbProxy) {
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


        List<Map<String, Object>> dataList = new ArrayList<>();
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
                dataList.add(resultMap);
            }
        } catch (SQLException ex) {
            log.error("SQLException: " + ex.getMessage());
            log.error("SQLState: " + ex.getSQLState());
            log.error("VendorError: " + ex.getErrorCode());
        }
        return dataList;
    }
}
