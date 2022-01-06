package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;


import java.io.IOException;
import java.sql.*;
import java.util.logging.Logger;
import java.util.logging.Level;

public class DBUtils {
    public static void changeScene(ActionEvent event, String fxmlfile, String title, String username){
        Parent root = null;

        if (username != null){
            try{
                FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlfile));
                root = loader.load();
                LoggedInController loggedInController = loader.getController();
                loggedInController.setUserInformation(username);
            }catch (IOException e){
                e.printStackTrace();
            }
        }else{
            try{
                root=FXMLLoader.load(DBUtils.class.getResource(fxmlfile));
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }
    public static void signUpUser(ActionEvent event,String name,String surname,String username, String password){
        Connection connection = null;
        PreparedStatement psInsert = null;
        PreparedStatement psCheckUserExists = null;
        ResultSet resultSet = null;

        try{
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/javafx","postgres","e17182002_");
            psCheckUserExists = connection.prepareStatement("select * from users where username = ?");
            psCheckUserExists.setString(1, username);
            resultSet = psCheckUserExists.executeQuery();

            if(resultSet.isBeforeFirst()){
                System.out.println("User already exists!");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("You cannot use this username.");
                alert.show();
            }else{
                psInsert = connection.prepareStatement("INSERT INTO users (name,surname,username,password)VALUES (?,?,?,?)");
                psInsert.setString(1,name);
                psInsert.setString(2,surname);
                psInsert.setString(3,username);
                psInsert.setString(4,password);
                psInsert.executeUpdate();

                changeScene(event,"Logged-in.fxml", "Welcome", username);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            if (resultSet != null){
                try{
                    resultSet.close();
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }
            if (psCheckUserExists != null){
                try{
                    psCheckUserExists.close();
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }
            if (psInsert != null){
                try{
                    psInsert.close();
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }
            if (connection != null){
                try{
                    connection.close();
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }
        }

    }
    public static void logInUser(ActionEvent event, String username, String password){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try{
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/javafx","postgres","e17182002_");
            preparedStatement = connection.prepareStatement("select password from users where username = ?");
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();

            if (!resultSet.isBeforeFirst()){
                System.out.println("User not found in the database!");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Provided credentials are incorrect!");
                alert.show();
            }
            else {
                while (resultSet.next()){
                    String retrievedPassword = resultSet.getString("password");
                    if (retrievedPassword.equals(password)){
                        changeScene(event, "logged-in.fxml","Welcome", username);
                    }else {
                        System.out.println("Password did not match!");
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Provided credentials are incorrect!");
                        alert.show();
                    }
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            if (resultSet != null){
                try{
                    resultSet.close();
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }if (preparedStatement != null){
                try{
                    preparedStatement.close();
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }if (connection != null){
                try{
                    connection.close();
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }
        }
    }
    public static void writeToDatabase(String content1){
        String url = "jdbc:postgresql://localhost:5432/javafx";
        String user = "postgres";
        String password = "e17182002_";

        String content =content1;

        String query = "insert into letters (content) values (?)";

        try(Connection con =DriverManager.getConnection(url,user,password);
            PreparedStatement pst =con.prepareStatement(query)){

            pst.setString(1, content);
            pst.executeUpdate();
            System.out.println("Successfully created");
        }catch (SQLException ex){
            Logger lgr = Logger.getLogger(DBUtils.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

}
