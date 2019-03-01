package sample;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Controller {
    private List<Class<?>> classes = new ArrayList<>();
    String path;
    private boolean isLoaded = false;
    // @FXML
    // private Button testButton;

    @FXML
    private StackPane myBack;

    @FXML
    private MenuBar loadFile;

    @FXML
    private Menu menu;

    @FXML
    private MenuItem menuItem;

    @FXML
    private Menu helpButton;

    @FXML
    private MenuItem aboutAuthor;


    @FXML
    private TextField firstArgText;

    @FXML
    private TextField ResultText;

    @FXML
    private Button runButton;

    @FXML
    private TextArea descriptionText;

    @FXML
    private TextField secArgText;

    @FXML
    private ListView<Class<?>> listView;


    @FXML
    void initialize() {
        //TODO zrobic buttona RUN czy cos
        final Tooltip tooltipButton = new Tooltip("Click to run the program");
        runButton.setTooltip(tooltipButton);


        firstArgText.setOnKeyTyped(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (!isNum(event.getCharacter().charAt(0))) event.consume();
            }
        });

        secArgText.setOnKeyTyped(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(!isNum(event.getCharacter().charAt(0))) event.consume();
            }
        });

        menuItem.setOnAction(event -> {
            classLoad();
        });
        listView.setOnMouseClicked(event -> {
            descriptionText.clear();
            if (descriptionText.getText().isEmpty())
                descriptionText.setText(listView.getSelectionModel().getSelectedItem().getAnnotation(Description.class).description());
        });


        aboutAuthor.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Autor");
            alert.setHeaderText("Autor: Rafał Pęszyński\n grupa: I6B1S1");
            alert.showAndWait().ifPresent(e -> {
                if (e == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });
        });
    }


    public void classLoad() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Wybierz plik typu .JAR");

        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Pliki typu JAR", "*.jar");
        fileChooser.getExtensionFilters().add(filter);
        File selectedFiles = fileChooser.showOpenDialog(null);

        if (selectedFiles != null) {

        } else {
            System.out.println("Invalid file!");
        }

        JarFile jarFile = null;

        try {
            System.out.println(path = selectedFiles.getAbsolutePath());
            jarFile = new JarFile(path);

            Enumeration<JarEntry> entry = jarFile.entries();

            URL[] adresy = {new URL("jar:file:" + path + "!/")};


            URLClassLoader urlClassLoader = URLClassLoader.newInstance(adresy);


            classes.clear();

            while (entry.hasMoreElements()) {
                JarEntry jarEntry = entry.nextElement();
                if (jarEntry.isDirectory() || !jarEntry.getName().endsWith(".class")) continue;

                String className = jarEntry.getName().substring(0, jarEntry.getName().length() - 6);
                className = className.replace('/', ',');
                //System.out.println(className);
                try {
                    Class<?> classList = urlClassLoader.loadClass(className);
                    //System.out.println(urlClassLoader);
                    //  System.out.println("__________________________");
                    // System.out.println(classList);
                    classes.add(classList);
                    // System.out.println(classes);
                    listView.getItems().addAll(classes);


                    if (classList.isAnnotationPresent(Description.class)) {
                        Description opis = classList.getAnnotation(Description.class);
                        System.out.println(opis);
                        if (ICallable.class.isAssignableFrom(classList)) {
                            ICallable iCallable = (ICallable) classList.getConstructor().newInstance();
                            System.out.println(iCallable.toString());
                            if (iCallable != null) classes.add(classList);
                        }

                    }

                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            isLoaded = true;
            if (jarFile != null) {
                try {
                    jarFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }


    }

    public boolean isNum(char zn) {
        return (zn >= '0' && zn <= '9');

    }
}
