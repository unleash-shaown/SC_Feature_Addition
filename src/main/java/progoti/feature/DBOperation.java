/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progoti.feature;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 *
 * @author Shaown
 */
public class DBOperation {
    private static String module = "";
    private static String operation = "";
    private static String description = "";
    private static String role = "";
    private static Map<String, Integer> roleMap = new TreeMap<>();

    public static Map<String, Integer> getRoleMap() {
        return roleMap;
    }

    public enum Status {
        SUCCESS("SUCCESS"), FAILED("FAILED"), DB_35("192.168.1.35"), DB_55("192.168.1.55"), DB_LOCAL("localhost"),
        RBL("profino_rbl"), FSIBL("profino"), BCBL("profino_bcbl"), JBL("profino_jbl"),
        USERNAME("root"), PASSWORD("admin@pr0g0t1@db@35"), PORT("3306"),
        RBL_USERNAME(""), RBL_PASSWORD(""), FSIBL_USERNAME(""), FSIBL_PASSWORD(""), BCBL_USERNAME(""), BCBL_PASSWORD(""),
        JBL_USERNAME(""), JBL_PASSWORD("");

        private String value;

        Status(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    static {
        fetchRoleForComboBox(Status.DB_35.getValue());
        try {
            readConfigFile("@35");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readConfigFile(String db) throws IOException {
        Properties properties = new Properties();
        Resource resource = new FileSystemResource("application.properties");
        properties.load(resource.getInputStream());
        if(db.equals("@35")){
            setRBLDB(properties, "db_35");
            setFSIBLDB(properties, "db_35");
            setBCBLDB(properties, "db_35");
            setJBLDB(properties, "db_35");
        } else if(db.equals("@55")){
            setRBLDB(properties, "db_55");
            setFSIBLDB(properties, "db_55");
            setBCBLDB(properties, "db_55");
            setJBLDB(properties, "db_55");
        } else if(db.equals("@local")){
            setRBLDB(properties, "db_local");
        }
    }

    private static void setJBLDB(Properties properties, String db) {
        Status.JBL.setValue(properties.getProperty(db + ".jbl.db"));
        Status.JBL_USERNAME.setValue(properties.getProperty(db + ".jbl.username"));
        Status.JBL_PASSWORD.setValue(properties.getProperty(db + ".jbl.password"));
    }

    private static void setBCBLDB(Properties properties, String db) {
        Status.BCBL.setValue(properties.getProperty(db + ".bcbl.db"));
        Status.BCBL_USERNAME.setValue(properties.getProperty(db + ".bcbl.username"));
        Status.BCBL_PASSWORD.setValue(properties.getProperty(db + ".bcbl.password"));
    }

    private static void setFSIBLDB(Properties properties, String db) {
        Status.FSIBL.setValue(properties.getProperty(db + ".fsibl.db"));
        Status.FSIBL_USERNAME.setValue(properties.getProperty(db + ".fsibl.username"));
        Status.FSIBL_PASSWORD.setValue(properties.getProperty(db + ".fsibl.password"));
    }

    private static void setRBLDB(Properties properties, String db) {
        Status.RBL.setValue(properties.getProperty(db + ".rbl.db"));
        Status.RBL_USERNAME.setValue(properties.getProperty(db + ".rbl.username"));
        Status.RBL_PASSWORD.setValue(properties.getProperty(db + ".rbl.password"));
    }

    public static void fetchRoleForComboBox(String db) {
        String authority_id_query = "SELECT id, role_title FROM sc_authority";
        String dbUrl = "jdbc:mysql://" + db + ":" + Status.PORT.getValue() + "/" + Status.RBL.getValue();
        Connection connection;
        Statement statement;
        try {
            connection = DriverManager.getConnection(dbUrl, Status.RBL_USERNAME.getValue(), Status.RBL_PASSWORD.getValue());
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(authority_id_query);
            roleMap.clear();
            while (resultSet.next()){
                roleMap.put(resultSet.getString("role_title").toUpperCase(), resultSet.getInt("id"));
            }
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Response startDBOperation(Request request) throws SQLException {
        initializeValue(request);
        Response response = new Response();
        if(request.getDb().equals("@55")){
            doDBOperation(Status.DB_55.getValue(), response);
        } else if(request.getDb().equals("@35")){
            doDBOperation(Status.DB_35.getValue(), response);
        } else if(request.getDb().equals("@local")){
            doDBOperation(Status.DB_LOCAL.getValue(), response);
        }

        return response;
    }

    private void doDBOperation(String host, Response response) throws SQLException {
        if(initializeDB(host, Status.RBL.getValue(), Status.RBL_USERNAME.getValue(), Status.RBL_PASSWORD.getValue())){
            response.getInnerStatusMap().get("RBL").setStatus(Status.SUCCESS.getValue());
        } else {
            response.getInnerStatusMap().get("RBL").setStatus(Status.FAILED.getValue());
        }
        if(initializeDB(host, Status.FSIBL.getValue(), Status.FSIBL_USERNAME.getValue(), Status.FSIBL_PASSWORD.getValue())){
            response.getInnerStatusMap().get("FSIBL").setStatus(Status.SUCCESS.getValue());
        } else {
            response.getInnerStatusMap().get("FSIBL").setStatus(Status.FAILED.getValue());
        }
        if(initializeDB(host, Status.BCBL.getValue(), Status.BCBL_USERNAME.getValue(), Status.BCBL_PASSWORD.getValue())){
            response.getInnerStatusMap().get("BCBL").setStatus(Status.SUCCESS.getValue());
        } else {
            response.getInnerStatusMap().get("BCBL").setStatus(Status.FAILED.getValue());
        }
        if(initializeDB(host, Status.JBL.getValue(), Status.JBL_USERNAME.getValue(), Status.JBL_PASSWORD.getValue())){
            response.getInnerStatusMap().get("JBL").setStatus(Status.SUCCESS.getValue());
        } else {
            response.getInnerStatusMap().get("JBL").setStatus(Status.FAILED.getValue());
        }
    }

    private boolean initializeDB(String host, String dbName, String userName, String password) throws SQLException {
        String dbUrl = "jdbc:mysql://" + host + ":" + Status.PORT.getValue() + "/" + dbName;
        Connection connection = DriverManager.getConnection(dbUrl, userName, password);
        return executeQuery(connection);
    }

    private boolean executeQuery(Connection connection) throws SQLException {
        String insert_into_feature_query = "INSERT INTO sc_feature (module, operation, description, menu_priority, app_name) VALUES (?, ?, ?, ?, 'SureCashWeb')";
        String feature_id_query = "SELECT id FROM sc_feature WHERE module = '" + module + "' AND operation = '" + operation + "' ORDER BY id DESC LIMIT 1";
        String insert_into_authority_feature = "INSERT INTO sc_authority_features (sc_authority_id, sc_feature_id) VALUES (?, ?)";

        PreparedStatement preparedStatement = connection.prepareStatement(insert_into_feature_query);
        preparedStatement.setString(1, module);
        preparedStatement.setString(2, operation);
        preparedStatement.setString(3, description);
        preparedStatement.setInt(4, new Random().nextInt(30));
        if(preparedStatement.executeUpdate() > 0){
            preparedStatement.close();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(feature_id_query);
            int featureId = 0;
            while (resultSet.next()){
                featureId = resultSet.getInt("id");
            }
            preparedStatement = connection.prepareStatement(insert_into_authority_feature);
            preparedStatement.setInt(1, roleMap.get(role));
            preparedStatement.setInt(2, featureId);
            if(preparedStatement.executeUpdate() > 0){
                preparedStatement.close();
                statement.close();
                return true;
            }
        }
        preparedStatement.close();
        return false;
    }

    private static void initializeValue(Request request) {
        module = request.getController().trim();
        operation = request.getAction().trim();
        description = request.getTitle().trim();
        role = request.getRole().trim();

    }
}
