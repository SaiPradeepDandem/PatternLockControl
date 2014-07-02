package com.sdandem.patternlock.demo;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.GroupBuilder;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;

import com.sdandem.patternlock.PatternLockControl;
import com.sdandem.patternlock.PatternLockControl.PatternSize;

/**
 * Demo application for default {@link PatternLockControl}.
 */
public class DefaultPatternLockControlDemo extends Application {
	Stage stage;
	Scene scene;
	StackPane contentRoot;

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
		contentRoot = new StackPane();
		contentRoot.setAlignment(Pos.CENTER);
		this.scene = new Scene(contentRoot, Color.WHITE);
	}

	@Override
	public void start(Stage stage) throws Exception {
		this.stage = stage;
		configureScene();
		configureStage();

		final Label pwdLabel = new Label();

		PatternLockControl patternLock = new PatternLockControl();
		patternLock.setPrefSize(200, 200);
		patternLock.setOnPatternDetected(new Callback<String, Boolean>() {
			@Override
			public Boolean call(String code) {
				pwdLabel.setText("Password entered is : " + code);
				return null;
			}
		});

		HBox layout = HBoxBuilder.create().spacing(15).alignment(Pos.TOP_CENTER).build();
		layout.getChildren().addAll(new Label("Enter password :"), patternLock);
		contentRoot.getChildren().add(VBoxBuilder.create().children(layout, pwdLabel).spacing(15).alignment(Pos.CENTER).build());
	}
}
