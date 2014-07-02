package com.sdandem.patternlock.loginapp;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.LabelBuilder;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFieldBuilder;
import javafx.scene.layout.ColumnConstraintsBuilder;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.GridPaneBuilder;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraintsBuilder;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import com.sdandem.patternlock.PatternLockControl;

/**
 * View implementation for the login pane.
 */
public class LoginView extends VBox {

	private final TextField userName;
	private final PatternLockControl lock;
	private Callback<Void, Void> onSuccess;
	private Callback<Void, Void> onRegistration;
	private User user;
	private final Button registrationBtn;

	public LoginView() {
		super();
		setSpacing(15);
		getStyleClass().add("form");
		getChildren().add(LabelBuilder.create().text("Login AA").styleClass("login").build());

		registrationBtn = ButtonBuilder.create().text("Registration ad").styleClass("refresh").onAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				onRegistration.call(null);
			}
		}).build();
		
		userName = TextFieldBuilder.create().build();
		userName.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> paramObservableValue, String paramT1, String paramT2) {
				userName.getStyleClass().removeAll("error");
			}
		});

		lock = new PatternLockControl();
		lock.setPrefSize(200, 200);
		lock.setOnPatternDetected(new Callback<String, Boolean>() {
			@Override
			public Boolean call(String code) {
				if (onSuccess != null) {
					if (!userName.getText().equals(user.getUserName())) {
						userName.getStyleClass().add("error");
						return true;
					}

					if (!code.equals(user.getPassword())) {
						return false;
					}
					onSuccess.call(null);
					return true;
				}
				return true;
			}
		});

		lock.disableProperty().bind(new BooleanBinding() {
			{
				bind(userName.textProperty());
			}

			@Override
			protected boolean computeValue() {
				return !(userName.getText() != null && !userName.getText().isEmpty());
			}
		});

		GridPane gp = GridPaneBuilder
				.create()
				.vgap(20)
				.hgap(10)
				.columnConstraints(ColumnConstraintsBuilder.create().prefWidth(130).build())
				.rowConstraints(RowConstraintsBuilder.create().valignment(VPos.TOP).build(),
						RowConstraintsBuilder.create().valignment(VPos.TOP).build()).build();
		gp.addRow(0, LabelBuilder.create().text("Enter user name").translateY(4).styleClass("formLabel").build(), userName);
		gp.addRow(1, LabelBuilder.create().text("Enter password").translateY(15).styleClass("formLabel").build(), lock);
		VBox.setVgrow(gp, Priority.ALWAYS);

		HBox buttonRow = HBoxBuilder.create().children(registrationBtn).alignment(Pos.CENTER_RIGHT).build();
		getChildren().addAll(gp, buttonRow);
	}

	public void reset() {
		userName.clear();
		lock.clear();
	}

	public void setOnSuccess(Callback<Void, Void> onSuccess) {
		this.onSuccess = onSuccess;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setOnRegistration(Callback<Void, Void> onRegistration) {
		this.onRegistration = onRegistration;
	}

}
