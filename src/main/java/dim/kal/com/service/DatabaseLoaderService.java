package dim.kal.com.service;

import dim.kal.com.exception.DataLoadingException;
import dim.kal.com.model.Document;
import io.agroal.api.AgroalDataSource;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DatabaseLoaderService implements IDocumentLoaderService{
    @Inject
    AgroalDataSource dataSource; // Quarkus JDBC

    @Override
    public boolean supports(DocumentType type) {
        return type == DocumentType.DATABASE_JDBC;
    }

    @Override
    public List<Document> load(String tableName, Map<String, String> params) {
        List<Document> docs = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName)){
            ResultSetMetaData meta = rs.getMetaData();
            while (rs.next()) {
                StringBuilder content = new StringBuilder();
                Map<String, String> metadata = new HashMap<>();

                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    String value = rs.getString(i);
                    if (value != null) {
                        content.append(meta.getColumnName(i)).append(": ").append(value).append("\n");
                    }
                    metadata.put(meta.getColumnName(i), value);
                }

                docs.add(new Document(
                        UUID.randomUUID().toString(),
                        content.toString(),
                        "db:" + tableName,
                        metadata
                ));
            }
        } catch (SQLException e) {
            throw new DataLoadingException("Database load failed", Response.Status.INTERNAL_SERVER_ERROR);
        }

        return docs;
    }
}
