package spl.kafkaConnector;

import java.util.Map;

public class QueryBuilder {

    public static String constructInsertQuery(Map<String, Object> recordMap) {
        StringBuilder queryBuilder = new StringBuilder("INSERT INTO ");
        String tableName = (String) recordMap.get("tableName");
        queryBuilder.append(tableName).append(" (");

        StringBuilder columnsBuilder = new StringBuilder();
        StringBuilder valuesBuilder = new StringBuilder(" VALUES (");
        boolean first = true;
        for (String key : recordMap.keySet()) {
            if (key.equals("tableName") || key.equals("action") || key.equals("key")) {
                continue;
            }
            if (!first) {
                columnsBuilder.append(", ");
                valuesBuilder.append(", ");
            }
            columnsBuilder.append(key);
            valuesBuilder.append("?");
            first = false;
        }
        columnsBuilder.append(")");
        valuesBuilder.append(")");

        queryBuilder.append(columnsBuilder).append(valuesBuilder);
        return queryBuilder.toString();
    }

    // Method to construct UPDATE query
    public static String constructUpdateQuery(Map<String, Object> recordMap) {
        StringBuilder queryBuilder = new StringBuilder("UPDATE ");
        String tableName = (String) recordMap.get("tableName");
        queryBuilder.append(tableName).append(" SET ");

        StringBuilder setBuilder = new StringBuilder();
        boolean first = true;
        String whereConditionKey = (String) recordMap.get("key");

        for (String key : recordMap.keySet()) {
            if (key.equals("tableName") || key.equals("action") || key.equals("key")) {
                continue;
            }
            if (!first) {
                setBuilder.append(", ");
            }
            setBuilder.append(key).append(" = ?");
            first = false;
        }

        queryBuilder.append(setBuilder).append(" WHERE ").append(whereConditionKey).append(" = ").append("?");
        return queryBuilder.toString();
    }

    // Method to construct DELETE query
    public static String constructDeleteQuery(Map<String, Object> recordMap) {
        StringBuilder queryBuilder = new StringBuilder("DELETE FROM ");
        String tableName = (String) recordMap.get("tableName");
        queryBuilder.append(tableName);

        String whereConditionKey = (String) recordMap.get("key");

        queryBuilder.append(" WHERE ").append(whereConditionKey).append(" = ").append("?");
        return queryBuilder.toString();
    }

}
