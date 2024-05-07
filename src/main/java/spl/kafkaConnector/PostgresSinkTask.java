package  spl.kafkaConnector;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;

import java.util.Collection;
import java.util.List;
import java.util.Map;
@Slf4j
public class PostgresSinkTask extends SinkTask {

    private PostgresWriter writer;

    @Override
    public String version() {
        return "1.0";
    }

    @Override
    public void start(Map<String, String> props) {
        // Initialize Postgres writer with configuration
        log.info("props PostgresSinkTask:--->"+props.get(0));
        writer = new PostgresWriter(props);
        log.info("writer PostgresSinkTask start:--->"+writer);
    }

    @Override
    public void put(Collection<SinkRecord> records) {
        // Write records to PostgreSQL
        log.info("records.size() PostgresSinkTask:--->"+records.size());
        writer.write(records);
        log.info("writer PostgresSinkTask put:--->"+writer);
    }

    @Override
    public void stop() {
        // Clean up resources
        writer.close();
    }
}