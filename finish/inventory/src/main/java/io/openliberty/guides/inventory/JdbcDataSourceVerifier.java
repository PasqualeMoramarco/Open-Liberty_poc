//package io.openliberty.guides.inventory;
//
//import javax.annotation.Resource;
//import javax.enterprise.context.ApplicationScoped;
//import javax.enterprise.context.Initialized;
//import javax.enterprise.event.Observes;
//import javax.sql.DataSource;
//import java.sql.DatabaseMetaData;
//import java.sql.SQLException;
//import java.util.logging.Logger;
//
//@ApplicationScoped
//public class JdbcDataSourceVerifier {
//    private static Logger logger = Logger.getLogger(JdbcDataSourceVerifier.class.getName());
//
//    @Resource(lookup = "jdbc/postgresql")
//    private DataSource dataSource;
//
//    public void init(@Observes @Initialized(ApplicationScoped.class) Object init) throws SQLException {
//        logger.warning("Verifying connection to PostgreSQL");
//
//        DatabaseMetaData metaData = dataSource.getConnection().getMetaData();
//
//        logger.warning(metaData.getDatabaseProductName());
//        logger.warning("Version: " + metaData.getDatabaseMajorVersion() + "." + metaData.getDatabaseMinorVersion());
//    }
//}
