package spl.kafkaConnector;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

@Slf4j
public class PostgresSinkConfig extends AbstractConfig {

    public static final String CONNECTION_URL_CONFIG = "connection.url";
    public static final String CONNECTION_USER_CONFIG = "connection.user";
    public static final String CONNECTION_PASSWORD_CONFIG = "connection.password";
    public static final String TABLE_NAME_CONFIG = "table.name";

    public static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define(CONNECTION_URL_CONFIG, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "PostgreSQL connection URL")
            .define(CONNECTION_USER_CONFIG, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "PostgreSQL connection username")
            .define(CONNECTION_PASSWORD_CONFIG, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "PostgreSQL connection password")
            .define(TABLE_NAME_CONFIG, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "PostgreSQL table name");

    public PostgresSinkConfig(Map<?, ?> props) {
        super(CONFIG_DEF, props);
    }

    public String getConnectionUrl() {
        log.info("CONNECTION_URL_CONFIG: "+CONNECTION_URL_CONFIG);
        return this.getString(CONNECTION_URL_CONFIG);
    }

    public String getConnectionUser() {
        log.info("CONNECTION_USER_CONFIG: "+CONNECTION_USER_CONFIG);
        return this.getString(CONNECTION_USER_CONFIG);
    }

    public String getConnectionPassword() {
        log.info("CONNECTION_PASSWORD_CONFIG: "+CONNECTION_PASSWORD_CONFIG);
        return this.getString(CONNECTION_PASSWORD_CONFIG);
    }

    public String getTableName() {
        log.info("TABLE_NAME_CONFIG: "+TABLE_NAME_CONFIG);
        return this.getString(TABLE_NAME_CONFIG);
    }
}