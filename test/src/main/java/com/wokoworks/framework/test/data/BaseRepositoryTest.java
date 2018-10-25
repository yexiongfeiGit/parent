package com.wokoworks.framework.test.data;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.SQLException;

@RunWith(SpringRunner.class)
public abstract class BaseRepositoryTest {

    @Autowired
    private DataSource dataSource;

    private IDataSet dataSet;
    private IDatabaseConnection connection;

    @Before
    public void setup() {
        try {
            final IDataSet dataset = getDataset();

            connection = new DatabaseDataSourceConnection(dataSource);
            final DatabaseConfig config = connection.getConfig();
            config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new H2DataTypeFactory());

            DatabaseOperation.CLEAN_INSERT.execute(connection, dataset);
        } catch (SQLException | DatabaseUnitException e) {
            throw new RuntimeException(e);
        }
    }

    @After
    public void teardown() {
        if (connection != null) {
            try {
                connection.close();
            } catch(SQLException e) {
                throw new RuntimeException(e);
            }
        }
        connection = null;
    }

    private IDataSet getDataset() throws DataSetException {
        if (dataSet != null) {
            return dataSet;
        }
        return dataSet = new FlatXmlDataSetBuilder()
            .setColumnSensing(true)
            .build(getClass().getClassLoader().getResourceAsStream("dataset.xml"));
    }
}
