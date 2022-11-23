import java.sql.*;

public class CreateDB {
    //TODO you need to set 'USER' and 'PASSWORD' in environment variables

    private static final String DEFAULT_DBNAME = "postgres";
    private static final String LOCALHOST_5432 = "jdbc:postgresql://localhost:5432/";
    private static final String USER = System.getenv("USER");
    private static final String PASSWORD = System.getenv("PASSWORD");
    private static final String DB_NAME = "bos_webshop";
    private Connection connection;

    public CreateDB(Connection connection) {
        this.connection = connection;
    }

    public static void main(String[] args) {
        try (Connection con = DriverManager.getConnection(LOCALHOST_5432 + DEFAULT_DBNAME, USER, PASSWORD)) {
            System.out.println("Creating database...");
            CreateDB createDB = new CreateDB(con);
            createDB.createDataBase();
            System.out.println("Database created");
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        try (Connection con = DriverManager.getConnection(LOCALHOST_5432 + DB_NAME, USER, PASSWORD)) {
            CreateDB createDB = new CreateDB(con);
            createDB.createProductsTable();
            createDB.createSuppliersTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createDataBase() throws SQLException {
        String SqlQuery = "CREATE DATABASE " + DB_NAME;
        Statement statement = connection.createStatement();
        statement.execute(SqlQuery);
    }

    private void createProductsTable() throws SQLException {
        String SqlQuery = "CREATE TABLE products (" +
                            "product_id serial PRIMARY KEY," +
                            "\"name\" varchar(40) NOT NULL," +
                            "price integer NOT NULL," +
                            "currency varchar(4) NOT NULL," +
                            "description text," +
                            "product_category_id integer NULL," +
                            "supplier_id integer NULL);";
        Statement statement = connection.createStatement();
        statement.execute(SqlQuery);
    }

    private void createSuppliersTable() throws SQLException {
        String SqlQuery = "CREATE TABLE suppliers (" +
                "supplier_id serial PRIMARY KEY," +
                "\"name\" varchar(10) NOT NULL," +
                "description text);";
        Statement statement = connection.createStatement();
        statement.execute(SqlQuery);
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}