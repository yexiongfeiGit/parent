package com.wokoworks.framework.code.generator.controller;

import com.google.common.collect.ImmutableMap;
import com.wokoworks.framework.code.generator.templates.RepositoryImplementsTemplate;
import com.wokoworks.framework.code.generator.templates.RepositoryInterfaceTemplate;
import com.wokoworks.framework.code.generator.templates.VoTemplate;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author 0x0001
 */
@Slf4j
@RestController
public class IndexController {

    private <T, R> Optional<R> flatMap(T data, Function<T, R> fn) {
        return Optional.ofNullable(data).flatMap(e -> Optional.ofNullable(fn.apply(e)));
    }

    @PostMapping("/testJDBC")
    public Object testJDBC(@Valid JdbcData data, BindingResult bindingResult) {
        log.info("data: {}", data);
        if (bindingResult.hasErrors()) {
            final FieldError fieldError = bindingResult.getFieldError();
            return ImmutableMap.of("code", 400, "message", flatMap(fieldError, FieldError::getDefaultMessage).orElse(""));
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
            return ImmutableMap.of("code", 400, "message", flatMap(fieldError, FieldError::getDefaultMessage).orElse(""));
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
    public Object generator(@Valid GeneratorData data, BindingResult bindingResult) throws IOException {
        log.info("data: {}", data);
        if (bindingResult.hasErrors()) {
            final FieldError fieldError = bindingResult.getFieldError();
            return ImmutableMap.of("code", 400, "message", flatMap(fieldError, FieldError::getDefaultMessage).orElse(""));
        }
        String url = String.format("jdbc:mysql://%s:%s", data.getHost(), data.getPort());
        log.info("jdbc url: {}", url);

        final VoTemplate template = new VoTemplate();
        final RepositoryInterfaceTemplate repositoryInterfaceTemplate = new RepositoryInterfaceTemplate();
        final RepositoryImplementsTemplate repositoryImplementsTemplate = new RepositoryImplementsTemplate();

        final boolean repository = data.getTemplates().contains("repository");
        final boolean vo = data.getTemplates().contains("vo");

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
                while (rs.next()) {
                    final VoTemplate.Column column = new VoTemplate.Column();
                    column.setName(rs.getString("COLUMN_NAME"));
                    column.setType(rs.getInt("DATA_TYPE"));
                    column.setRemark(rs.getString("REMARKS"));
                    columns.add(column);
                }

                // 测试输出到 标准输出, 调试用
                if (vo) {
                    template.process(model, System.out);

                    String filePath = data.getPackageName().replaceAll("\\.", "/") + "/vo/" + VoTemplate.CLASS_CONVERT.convert(table) + ".java";
                    addEntry(filePath, zip, () -> template.process(model, zip));
                }

                if (repository) {
                    {
                        repositoryInterfaceTemplate.process(model, System.out);

                        String filePath = data.getPackageName().replaceAll("\\.", "/") + "/repository/" + VoTemplate.CLASS_CONVERT.convert(table) + "Repository.java";
                        addEntry(filePath, zip, () -> repositoryInterfaceTemplate.process(model, zip));
                    }

                    {
                        repositoryImplementsTemplate.process(model, System.out);

                        String filePath = data.getPackageName().replaceAll("\\.", "/") + "/repository/impl/" + VoTemplate.CLASS_CONVERT.convert(table) + "RepositoryImpl.java";
                        addEntry(filePath, zip, () -> repositoryImplementsTemplate.process(model, zip));
                    }
                }
            }

            IOUtils.closeQuietly(zip);


            final String mimeType = "application/octet-stream";

            final byte[] responseData = out.toByteArray();
            return ResponseEntity
                .ok()
                .contentType(MediaType.valueOf(mimeType))
                .contentLength(responseData.length)
                .header("Content-Disposition", String.format("inline;filename=\"%s.zip\"", data.getDatabase()))
                .body(new InputStreamResource(new ByteArrayInputStream(responseData)));
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


    @FunctionalInterface
    private interface Process {
        void process() throws IOException;
    }

    private void addEntry(String filePath, ZipOutputStream zip, Process process) throws IOException {
        final ZipEntry entry = new ZipEntry(filePath);
        try {
            zip.putNextEntry(entry);
            process.process();
        } finally {
            zip.closeEntry();
        }
    }
}
