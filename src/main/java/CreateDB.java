import com.codecool.buyourstuff.model.Product;
import com.codecool.buyourstuff.model.ProductCategory;
import com.codecool.buyourstuff.model.Supplier;
import com.codecool.buyourstuff.model.User;
import com.codecool.buyourstuff.util.BaseData;

import java.sql.*;
import java.util.List;

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
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("Quitting...");
            return;
        }
        try (Connection con = DriverManager.getConnection(LOCALHOST_5432 + DB_NAME, USER, PASSWORD)) {
            CreateDB createDB = new CreateDB(con);
            //ATTENTION: order is extremely important here because of Foreign keys
            createDB.createSuppliersTable();
            createDB.createProductCategoriesTable();
            createDB.createCartsTable();
            createDB.createProductsTable();
            createDB.createUsersTable();
            createDB.createLineItemsTable();
            /*ATTENTION: if you run this file multiple times, then the tables will be filled with the same data multiple times
                    comment out the 4 lines below after running it once
             */
            createDB.addDataToSuppliersTable();
            createDB.addDataToProductCategoriesTable();
            createDB.addDataToProductsTable();
            createDB.addDataToUsersTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createDataBase() throws SQLException {
        String SqlQuery = "CREATE DATABASE " + DB_NAME + ";";
        Statement statement = connection.createStatement();
        statement.execute(SqlQuery);
    }

    private void createSuppliersTable() throws SQLException {
        String SqlQuery = "CREATE TABLE IF NOT EXISTS suppliers (" +
                "supplier_id serial PRIMARY KEY," +
                "\"name\" varchar(10) NOT NULL," +
                "description text);";
        Statement statement = connection.createStatement();
        statement.execute(SqlQuery);
    }

    private void createProductCategoriesTable() throws SQLException {
        String SqlQuery = "CREATE TABLE IF NOT EXISTS product_categories (" +
                "product_category_id serial PRIMARY KEY," +
                "\"name\" varchar(20) NOT NULL," +
                "description text," +
                "department varchar(20));";
        Statement statement = connection.createStatement();
        statement.execute(SqlQuery);
    }

    private void createCartsTable() throws SQLException {
        String SqlQuery = "CREATE TABLE IF NOT EXISTS carts (" +
                "cart_id serial PRIMARY KEY," +
                "currency varchar(4) DEFAULT 'USD' NOT NULL);";
        Statement statement = connection.createStatement();
        statement.execute(SqlQuery);
    }

    private void createProductsTable() throws SQLException {
        String SqlQuery = "CREATE TABLE IF NOT EXISTS products (" +
                "product_id serial PRIMARY KEY, " +
                "\"name\" varchar(40) NOT NULL, " +
                "price decimal(5,2) NOT NULL, " +
                "currency varchar(4) NOT NULL, " +
                "description text, " +
                "product_category_id int, " +
                "supplier_id int, " +
                "CONSTRAINT fk_product_categories " +
                "FOREIGN KEY (product_category_id) REFERENCES product_categories(product_category_id) " +
                "ON UPDATE CASCADE " +
                "ON DELETE SET NULL, " +
                "CONSTRAINT fk_suppliers " +
                "FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id) " +
                "ON UPDATE CASCADE " +
                "ON DELETE SET NULL);";
        Statement statement = connection.createStatement();
        statement.execute(SqlQuery);
    }

    private void createUsersTable() throws SQLException {
        String SqlQuery = "CREATE TABLE IF NOT EXISTS users (" +
                "user_id serial PRIMARY KEY, " +
                "\"name\" varchar(20) NOT NULL, " +
                "\"password\" varchar(100) NOT NULL," +
                "cart_id int, " +
                "CONSTRAINT fk_cart " +
                "FOREIGN KEY (cart_id) REFERENCES carts(cart_id) " +
                "ON UPDATE CASCADE " +
                "ON DELETE SET NULL);";
        Statement statement = connection.createStatement();
        statement.execute(SqlQuery);
    }

    private void createLineItemsTable() throws SQLException {
        String SqlQuery = "CREATE TABLE IF NOT EXISTS line_items (" +
                "line_items_id serial PRIMARY KEY, " +
                "product_id int, " +
                "cart_id int, " +
                "quantity int NOT NULL DEFAULT 1, " +
                "CONSTRAINT fk_product " +
                "FOREIGN KEY (product_id) REFERENCES products(product_id) " +
                "ON UPDATE CASCADE " +
                "ON DELETE CASCADE, " +
                "CONSTRAINT fk_cart " +
                "FOREIGN KEY (cart_id) REFERENCES carts(cart_id) " +
                "ON UPDATE CASCADE " +
                "ON DELETE CASCADE);";
        Statement statement = connection.createStatement();
        statement.execute(SqlQuery);
    }

    private void addDataToSuppliersTable() throws SQLException {
        String SqlQuery = "INSERT INTO suppliers(name, description) VALUES (?, ?)";
        PreparedStatement ps;
        for (Supplier supplier : BaseData.defaultSuppliers()) {
            ps = connection.prepareStatement(SqlQuery);
            ps.setString(1, supplier.getName());
            ps.setString(2, supplier.getDescription());
            ps.execute();
        }
    }

    private void addDataToProductCategoriesTable() throws SQLException {
        String SqlQuery = "INSERT INTO product_categories(name, description, department) VALUES (?, ?, ?)";
        PreparedStatement ps;
        for (ProductCategory pc : BaseData.defaultProductCategories()) {
            ps = connection.prepareStatement(SqlQuery);
            ps.setString(1, pc.getName());
            ps.setString(2, pc.getDescription());
            ps.setString(3, pc.getDepartment());
            ps.execute();
        }
    }

    private void addDataToProductsTable() throws SQLException {
        String SqlQuery = "INSERT INTO products(name, price, currency, description, product_category_id, supplier_id) " +
                "VALUES (?, ?, ?, ?, (" +
                "SELECT product_category_id FROM product_categories as pc WHERE pc.\"name\" = ?), (" +
                "SELECT supplier_id FROM suppliers as s WHERE s.\"name\" = ?));";
        PreparedStatement ps;
        for (Product product : BaseData.defaultProducts()) {
            ps = connection.prepareStatement(SqlQuery);
            ps.setString(1, product.getName());
            ps.setFloat(2, Float.parseFloat(product.getPrice().split(" ")[0]));
            ps.setString(3, product.getDefaultCurrency().toString());
            ps.setString(4, product.getDescription());
            ps.setString(5, product.getProductCategory().getName());
            ps.setString(6, product.getSupplier().getName());
            ps.execute();
        }
    }

    private void addDataToUsersTable() throws SQLException {
        String SqlQuery = "INSERT INTO users(name, password) VALUES (?, ?)";
        PreparedStatement ps;
        for (User user : BaseData.defaultUsers()) {
            ps = connection.prepareStatement(SqlQuery);
            ps.setString(1, user.getName());
            ps.setString(2, user.getPassword());
            ps.execute();
        }
    }

}
