package com.sample3;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;
import com.sample3.model.Album;
import com.sample3.model.Artist;
import com.sample3.model.Datasource;

public class Controller {

    @FXML
    private TableView<Artist> artistTable;

    @FXML
    private ProgressBar progressBar;

    @FXML
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

        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return Datasource.getInstance().updateArtistName(artist.getId(), "AC/DC");
            }
        };

        task.setOnSucceeded(e -> {
            if (task.getValue()) {
                artist.setName("AC/DC");
                artistTable.refresh();
            }
        });

        new Thread(task).start();
    }
}

class GetAllArtistsTask extends Task<ObservableList<Artist>> {

    @Override
    public ObservableList<Artist> call() {
        return FXCollections.observableArrayList(Datasource.getInstance().queryArtists(Datasource.ORDER_BY_ASC));
    }
}
