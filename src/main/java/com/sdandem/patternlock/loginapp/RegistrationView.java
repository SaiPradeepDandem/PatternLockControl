package com.sdandem.patternlock.loginapp;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.Label;
import javafx.scene.control.LabelBuilder;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFieldBuilder;
import javafx.scene.layout.ColumnConstraintsBuilder;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.GridPaneBuilder;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.PaneBuilder;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraintsBuilder;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import com.sdandem.patternlock.PatternLockControl;

public class RegistrationView extends VBox {

	private final TextField userName;
	private final Label pwdLabel;
	private final PatternLockControl lock;
	private final Label passwordSetLabel = LabelBuilder.create().text("Password is set").translateY(15).styleClass("tick").visible(false).build();

	private SimpleStringProperty code1 = new SimpleStringProperty();
	private SimpleStringProperty code2 = new SimpleStringProperty();

	private Button reset;
	private Button register;

	private Callback<User, Void> onSuccess;

	public RegistrationView() {
		super();
		setSpacing(15);
		getStyleClass().add("form");
		getChildren().add(LabelBuilder.create().text("Registration").styleClass("registration").build());

		userName = TextFieldBuilder.create().build();
		pwdLabel = LabelBuilder.create().text("Enter the password").translateY(15).styleClass("formLabel").build();

		lock = new PatternLockControl();
		lock.setPrefSize(200, 200);
		lock.setOnPatternDetected(new Callback<String, Boolean>() {
			@Override
			public Boolean call(String code) {
				// First attempt
				if (code1.get() == null || code1.get().isEmpty()) {
					code1.set(code);
				} else {
					if (code.equals(code1.get())) {
						code2.set(code);
					} else {
						return false;
					}
				}
				return true;
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
		gp.addRow(1, pwdLabel, PaneBuilder.create().children(passwordSetLabel,lock).build());
		VBox.setVgrow(gp, Priority.ALWAYS);
		reset = ButtonBuilder.create().text("Reset").visible(false).onAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				reset();
			}
		}).build();

		register = ButtonBuilder.create().text("Register").onAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if (onSuccess != null) {
					User u = new User();
					u.setUserName(userName.getText());
					u.setPassword(code2.get());
					onSuccess.call(u);
				}
			}
		}).build();
		register.disableProperty().bind(new BooleanBinding() {
			{
				bind(code2, userName.textProperty());
			}

			@Override
			protected boolean computeValue() {
				return !(code2.get() != null && !code2.get().isEmpty() && userName.getText() != null && !userName.getText().isEmpty());
			}
		});
		HBox buttonRow = HBoxBuilder.create().children(reset, register).alignment(Pos.CENTER_RIGHT).spacing(15).build();
		getChildren().addAll(gp, buttonRow);

		code1.addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String value) {
				if (value == null || value.isEmpty()) {
					reset.setVisible(false);
					pwdLabel.setText("Enter the password");
				} else {
					reset.setVisible(true);
					pwdLabel.setText("Re-Enter the password");
				}
			}
		});

		code2.addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String value) {
				if (value == null || value.isEmpty()) {
					passwordSetLabel.setVisible(false);
					lock.setVisible(true);
				} else {
					passwordSetLabel.setVisible(true);
					lock.setVisible(false);
				}
			}
		});
	}
	
	public void reset(){
		userName.clear();
		lock.clear();
		code1.set(null);
		code2.set(null);
	}

	public void setOnSuccess(Callback<User, Void> onSuccess) {
		this.onSuccess = onSuccess;
	}
	
	
}
