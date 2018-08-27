package com.wokoworks.framework.code.generator.controller;

import com.google.common.collect.ImmutableMap;
import com.wokoworks.framework.code.generator.templates.VoTemplate;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author 0x0001
 */
@Slf4j
@RestController
public class IndexController {

    private Map<String, byte[]> downloadFiles = new LinkedHashMap<String, byte[]>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, byte[]> eldest) {
            return size() > 5;
        }
    };

    @GetMapping("/download/{file}")
    public ResponseEntity<ByteArrayResource> download(@PathVariable String file) {
        final byte[] bytes = downloadFiles.get(file);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE,"application/zip")
            .body(new ByteArrayResource(bytes))
            ;
    }

    @PostMapping("/testJDBC")
    public Object testJDBC(@Valid JdbcData data, BindingResult bindingResult) {
        log.info("data: {}", data);
        if (bindingResult.hasErrors()) {
            final FieldError fieldError = bindingResult.getFieldError();
            return ImmutableMap.of("code", 400, "message", fieldError.getDefaultMessage());
        }
        String url = String.format("jdbc:mysql://%s:%s", data.host, data.port);
        log.info("jdbc url: {}", url);
        try (Connection conn = DriverManager.getConnection(url, data.getUserName(), data.getPassword())) {
            return ImmutableMap.of("code", 200, "message", "success");
        } catch (SQLException e) {
            log.info(e.getMessage(), e);
            return ImmutableMap.of("code", 300, "message", "can't connect server");
        }
    }

    @PostMapping("/getDatabases")
    public Object getDatabases(@Valid JdbcData data, BindingResult bindingResult) {
        log.info("data: {}", data);
        if (bindingResult.hasErrors()) {
            final FieldError fieldError = bindingResult.getFieldError();
            return ImmutableMap.of("code", 400, "message", fieldError.getDefaultMessage());
        }

        String url = String.format("jdbc:mysql://%s:%s", data.host, data.port);
        log.info("jdbc url: {}", url);
        try (Connection conn = DriverManager.getConnection(url, data.getUserName(), data.getPassword())) {
            List<String> databases = new ArrayList<>();
            try (ResultSet rs = conn.getMetaData().getCatalogs()) {
                while (rs.next()) {
                    final String database = rs.getString(1);
                    databases.add(database);
                }
            }
            return ImmutableMap.of("code", 200, "data", databases);
        } catch (SQLException e) {
            log.info(e.getMessage(), e);
            return ImmutableMap.of("code", 300, "message", "can't connect server");
        }
    }

    @PostMapping("/getTables")
    public Object getTables(@Valid DatabaseData data, BindingResult bindingResult) {
        log.info("data: {}", data);
        if (bindingResult.hasErrors()) {
            final FieldError fieldError = bindingResult.getFieldError();
            return ImmutableMap.of("code", 400, "message", fieldError.getDefaultMessage());
        }

        String url = String.format("jdbc:mysql://%s:%s", data.getHost(), data.getPort());
        log.info("jdbc url: {}", url);
        try (Connection conn = DriverManager.getConnection(url, data.getUserName(), data.getPassword())) {
            List<String> tableNames = new ArrayList<>();
            try (final ResultSet rs = conn.getMetaData().getTables(data.getDatabase(), null, "%", new String[]{"table"})) {
                while (rs.next()) {
                    final String tableName = rs.getString("TABLE_NAME");
                    tableNames.add(tableName);
                }
            }

            return ImmutableMap.of("code", 200, "data", tableNames);
        } catch (SQLException e) {
            log.info(e.getMessage(), e);
            return ImmutableMap.of("code", 300, "message", "can't connect server");
        }
    }

    @PostMapping("/generator")
    public Object generator(@Valid @RequestBody GeneratorData data, BindingResult bindingResult) {
        log.info("data: {}", data);
        if (bindingResult.hasErrors()) {
            final FieldError fieldError = bindingResult.getFieldError();
            return ImmutableMap.of("code", 400, "message", fieldError.getDefaultMessage());
        }
        String url = String.format("jdbc:mysql://%s:%s", data.getHost(), data.getPort());
        log.info("jdbc url: {}", url);

        final VoTemplate template = new VoTemplate();

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ZipOutputStream zip = new ZipOutputStream(out);

        try (Connection conn = DriverManager.getConnection(url, data.getUserName(), data.getPassword())) {
            for (String table : data.tables) {
                final VoTemplate.Model model = new VoTemplate.Model();
                model.setTableName(table);
                model.setPackageName(data.getPackageName());

                final ArrayList<VoTemplate.Column> columns = new ArrayList<>();
                model.setColumnList(columns);

                final ResultSet rs = conn.getMetaData().getColumns(data.getDatabase(), null, table, "%");
//                final ResultSetMetaData metaData = rs.getMetaData();
                while (rs.next()) {
                    final VoTemplate.Column column = new VoTemplate.Column();
                    column.setName(rs.getString("COLUMN_NAME"));
                    column.setType(rs.getInt("DATA_TYPE"));
                    column.setRemark(rs.getString("REMARKS"));
                    columns.add(column);
//                    for (int i = 1; i <= metaData.getColumnCount(); i++) {
//                        log.info("name: {} value: {}", metaData.getColumnName(i), rs.getObject(i));
//                    }
//                    log.info("");
                }

                {
                    try {
                        template.process(model, System.out);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                String filePath = data.getPackageName().replaceAll("\\.", "/") + "/vo/" + VoTemplate.CLASS_CONVERT.convert(table) + ".java";
                final ZipEntry entry = new ZipEntry(filePath);
                try {
                    zip.putNextEntry(entry);
                    template.process(model, zip);
                    zip.closeEntry();
                } catch (IOException e) {
                    log.warn(e.getMessage(), e);
                }
            }

            IOUtils.closeQuietly(zip);
//
            try (final FileOutputStream fout = new FileOutputStream("/Users/timtang/Downloads/test.zip")) {
                IOUtils.copy(new ByteArrayInputStream(out.toByteArray()), fout);
            } catch (IOException e) {
                e.printStackTrace();
            }
            final String uuid = UUID.randomUUID().toString();
            downloadFiles.put(uuid, out.toByteArray());

            return ImmutableMap.of("code", 200, "file", uuid);

        } catch (SQLException e) {
            log.info(e.getMessage(), e);
            return ImmutableMap.of("code", 300, "message", "can't connect server");
        }

    }


    @Data
    static class JdbcData {
        private String title;
        private String host;
        private int port;
        private String userName;
        private String password;
    }

    @Data
    static class DatabaseData extends JdbcData {
        private String database;
    }

    @Data
    static class GeneratorData extends DatabaseData {
        private String packageName;
        private List<String> tables;
        private List<String> templates;
    }
}
