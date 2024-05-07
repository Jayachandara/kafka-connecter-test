package spl.kafkaConnector;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.connect.sink.SinkRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Slf4j
public class PostgresWriter {

    PostgresSinkConfig postgresSinkConfig = null;

    //private static final String query = "";
    public PostgresWriter(Map<String, String> props) {
        postgresSinkConfig = new PostgresSinkConfig(props);
    }

    private final Map<String, String> queries = new HashMap<>();

    public void write(Collection<SinkRecord> records) {
        Connection connection = null;
        try {

            String url = postgresSinkConfig.getConnectionUrl();
            String user = postgresSinkConfig.getConnectionUser();
            String password = postgresSinkConfig.getConnectionPassword();
            connection = DriverManager.getConnection(url, user, password);

            Iterator<SinkRecord> iterator = records.iterator();
            while (iterator.hasNext()) {
                SinkRecord record = iterator.next();
                Object primaryKey = record.key();
                Object value = record.value();
                log.info("primary key:" + primaryKey);
                log.info("json Obj:" + value);
                Gson gson = new Gson();

                Map<String, Object> receivedRecord = gson.fromJson(String.valueOf(value), LinkedHashMap.class);
                String action = (String) receivedRecord.get("action");

                String query = getSqlQuery(receivedRecord);
                log.info("query11" + query);
                if (query != null) {
                    if ("create".equals(action)) {
                        insertData(connection, query, receivedRecord);
                    } else if ("update".equals(action)) {
                        updateData(connection, query, receivedRecord);
                    } else if ("delete".equals(action)) {
                        deleteData(connection, query, receivedRecord);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        log.info("records.getClass().getName()" + records.getClass().getName());
    }

    private String getSqlQuery(Map<String, Object> receivedRecord) {
        String tableName = (String) receivedRecord.get("tableName");
        String action = (String) receivedRecord.get("action");
        String keyColumn = (String) receivedRecord.get("key");
        String queryKey = tableName + "-" + action + "-" + keyColumn;

        String query = queries.get(queryKey);

        if (query == null) {

            if ("create".equals(action)) {
                query = QueryBuilder.constructInsertQuery(receivedRecord);
            } else if ("update".equals(action)) {
                query = QueryBuilder.constructUpdateQuery(receivedRecord);
            } else if ("delete".equals(action)) {
                query = QueryBuilder.constructDeleteQuery(receivedRecord);
            }

            queries.put(queryKey, query);
        }
        return query;
    }

    private void insertData(Connection connection, String query, Map<String, Object> receivedRecord) throws SQLException {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(query);
            int parameterIndex = 1;

            for (Map.Entry<String, Object> entry : receivedRecord.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (key.equals("tableName") || key.equals("action") || key.equals("key")) {
                    continue;
                }

                statement.setObject(parameterIndex++, value);

            }
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            log.info("error in insertData--->" + e);
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateData(Connection connection, String query, Map<String, Object> receivedRecord) throws SQLException {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(query);
            int parameterIndex = 1;
            String whereConditionKey = (String) receivedRecord.get("key");
            Object whereConditionVal = receivedRecord.get(whereConditionKey);


            for (Map.Entry<String, Object> entry : receivedRecord.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (key.equals("tableName") || key.equals("action") || key.equals("key")) {
                    continue;
                }

                statement.setObject(parameterIndex++, value);

            }
            statement.setObject(parameterIndex, whereConditionVal);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            log.info("error in updateData--->" + e);
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteData(Connection connection, String query, Map<String, Object> receivedRecord) throws SQLException {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(query);
            int parameterIndex = 1;
            String whereConditionKey = (String) receivedRecord.get("key");
            Object whereConditionVal = receivedRecord.get(whereConditionKey);
            statement.setObject(parameterIndex, whereConditionVal);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            log.info("error in updateData--->" + e);
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void close() {

    }

}
