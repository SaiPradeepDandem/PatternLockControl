package com.sdandem.patternlock.loginapp;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.Label;
import javafx.scene.control.LabelBuilder;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.StackPaneBuilder;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;

public class LoginApplication extends Application {

	Stage stage;
	Scene scene;
	StackPane contentRoot;
	private final Label successlbl = LabelBuilder.create().text("Login Successfull !!").styleClass("login-success").build();
	private final Button reset = ButtonBuilder.create().text("Reset").styleClass("refresh").build();
	
	public static void main(String[] args) {
		Application.launch(args);
	}

	private void configureStage() {
		stage.setTitle(this.getClass().getSimpleName());
		stage.setWidth(600);
		stage.setHeight(600);
		stage.setScene(this.scene);
		stage.show();
	}

	private void configureScene() {
		StackPane rt = new StackPane();
		rt.getStyleClass().add("base");
		final double size = 400;
		StackPane root2 = new StackPane();
		root2.setPrefSize(size, size);
		root2.setMaxSize(size, size);
		root2.setMinSize(size, size);

		StackPane bg = new StackPane();
		bg.getStyleClass().add("view-port");
		contentRoot = new StackPane();
		root2.getChildren().addAll(bg, contentRoot);
		rt.getChildren().add(root2);
		this.scene = new Scene(rt, Color.LINEN);
		scene.getStylesheets().add("com/sdandem/patternlock/loginapp/loginapplication.css");
	}

	@Override
	public void start(Stage stage) throws Exception {
		this.stage = stage;
		configureScene();
		configureStage();
		StackPane s = StackPaneBuilder.create().children(successlbl).build();
		final VBox successPane = VBoxBuilder.create().children(s, reset).alignment(Pos.CENTER_RIGHT).padding(new Insets(20)).build();
		VBox.setVgrow(s, Priority.ALWAYS);

		final LoginView login = new LoginView();
		final RegistrationView registration = new RegistrationView();
		registration.setOnSuccess(new Callback<User, Void>() {
			@Override
			public Void call(User paramP) {
				login.setUser(paramP);
				contentRoot.getChildren().clear();
				contentRoot.getChildren().add(login);
				return null;
			}
		});

		login.setOnSuccess(new Callback<Void, Void>() {
			@Override
			public Void call(Void p) {
				contentRoot.getChildren().clear();
				contentRoot.getChildren().add(successPane);
				return null;
			}
		});

		reset.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent paramT) {
				registration.reset();
				login.reset();
				contentRoot.getChildren().clear();
				contentRoot.getChildren().add(registration);
			}
		});
		contentRoot.getChildren().add(registration);
	}

}
