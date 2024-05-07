package  spl.kafkaConnector;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
public class PostgresSinkConnector extends SinkConnector {

    private Map<String, String> config;

    @Override
    public String version() {
        return "1.0";
    }

    @Override
    public void start(Map<String, String> props) {
        log.info("props PostgresSinkConnector:=="+props);
        new PostgresSinkConfig(props);
        config = props;
    }

    @Override
    public Class<? extends Task> taskClass() {
        return spl.kafkaConnector.PostgresSinkTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        log.info("maxTasks--->"+maxTasks);
        List<Map<String, String>> taskConfigs = new ArrayList<>();
        Map<String, String> taskProps = new HashMap<>();
        taskProps.putAll(config);
        for (int i = 0; i < maxTasks; i++) {
            taskConfigs.add(taskProps);
        }
        log.info("taskConfigs====>"+taskConfigs);
        return taskConfigs;
    }

    @Override
    public void stop() {
        // Clean up resources
    }

    @Override
    public ConfigDef config() {
        return PostgresSinkConfig.CONFIG_DEF;
    }
}