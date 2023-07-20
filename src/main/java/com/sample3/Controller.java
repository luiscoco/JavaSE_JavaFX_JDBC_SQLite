package com.sample3;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import com.sample3.model.Album;
import com.sample3.model.Artist;
import com.sample3.model.Datasource;

public class Controller {

    @FXML
    private TableView<Artist> artistTable;

    // Create a property to hold the albums for the selected artist
    private ObjectProperty<ObservableList<Album>> selectedArtistAlbums = new SimpleObjectProperty<>();

    public void listArtists() {
        Task<ObservableList<Artist>> task = new GetAllArtistsTask();
        artistTable.itemsProperty().bind(task.valueProperty());

        new Thread(task).start();
    }

    @FXML
    public void listAlbumsForArtist() {
        final Artist artist = artistTable.getSelectionModel().getSelectedItem();
        if (artist == null) {
            System.out.println("NO ARTIST SELECTED");
            return;
        }
        Task<ObservableList<Album>> task = new Task<ObservableList<Album>>() {
            @Override
            protected ObservableList<Album> call() throws Exception {
                return FXCollections
                        .observableArrayList(Datasource.getInstance().queryAlbumsForArtistId(artist.getId()));
            }
        };

        // Bind the selectedArtistAlbums property with the task's value property
        artistTable.itemsProperty().bind((ObservableValue<? extends ObservableList<Artist>>) task.valueProperty());

        // Start the task in a new thread
        new Thread(task).start();
    }
}

class GetAllArtistsTask extends Task<ObservableList<Artist>> {
    @Override
    public ObservableList<Artist> call() {
        return FXCollections.observableArrayList(Datasource.getInstance().queryArtists(Datasource.ORDER_BY_ASC));
    }
}