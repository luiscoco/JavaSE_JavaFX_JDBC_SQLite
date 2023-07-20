package com.sample3;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import com.sample3.model.Album;
import com.sample3.model.Artist;
import com.sample3.model.Datasource;

public class Controller {

    @FXML
    private TableView<Artist> artistTable;

    @FXML
    private ProgressBar progressBar;

     @FXML
    private TextField newArtistNameField; // Newly added TextField for the new artist name

    public void initialize() {
        newArtistNameField.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                BorderPane root = (BorderPane) newScene.getRoot();
                double halfWidth = root.getWidth() / 2;
                newArtistNameField.setPrefWidth(halfWidth);

                // Add a listener to adjust the width if the window size changes
                root.widthProperty().addListener((widthObservable, oldWidth, newWidth) -> {
                    double newHalfWidth = newWidth.doubleValue() / 2;
                    newArtistNameField.setPrefWidth(newHalfWidth);
                });
            }
        });
    }

    public void listArtists() {
        Task<ObservableList<Artist>> task = new GetAllArtistsTask();
        artistTable.itemsProperty().bind(task.valueProperty());
        progressBar.progressProperty().bind(task.progressProperty());

        progressBar.setVisible(true);

        task.setOnSucceeded(e -> progressBar.setVisible(false));
        task.setOnFailed(e -> progressBar.setVisible(false));

        new Thread(task).start();
    }

    @FXML
    public void listAlbumsForArtist() {
        final Artist artist = artistTable.getSelectionModel().getSelectedItem();
        if(artist == null) {
            System.out.println("NO ARTIST SELECTED");
            return;
        }
        Task<ObservableList<Album>> task = new Task<ObservableList<Album>>() {
            @Override
            protected ObservableList<Album> call() throws Exception {
                return FXCollections.observableArrayList(
                        Datasource.getInstance().queryAlbumsForArtistId(artist.getId()));
            }
        };
        artistTable.itemsProperty().bind((ObservableValue<? extends ObservableList<Artist>>) task.valueProperty());

        new Thread(task).start();
    }

    @FXML
    public void updateArtist() {
        final Artist artist = artistTable.getSelectionModel().getSelectedItem();

        if (artist == null) {
            System.out.println("NO ARTIST SELECTED");
            return;
        }

        // Get the new artist name from the text field
        String newArtistName = newArtistNameField.getText();

        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                // Perform the update with the new artist name
                return Datasource.getInstance().updateArtistName(artist.getId(), newArtistName);
            }
        };

        // Show the progress bar during the update process
        progressBar.setVisible(true);

        task.setOnSucceeded(e -> {
            if (task.getValue()) {
                artist.setName(newArtistName);
                artistTable.refresh();
            }
            // Hide the progress bar after the update is done
            progressBar.setVisible(false);
        });

        task.setOnFailed(e -> progressBar.setVisible(false));

        new Thread(task).start();
    }

}

class GetAllArtistsTask extends Task<ObservableList<Artist>> {

    @Override
    public ObservableList<Artist> call() {
        return FXCollections.observableArrayList(Datasource.getInstance().queryArtists(Datasource.ORDER_BY_ASC));
    }
}
