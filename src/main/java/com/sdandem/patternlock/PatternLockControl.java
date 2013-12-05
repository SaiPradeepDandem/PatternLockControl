package com.sdandem.patternlock;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.util.Callback;

/**
 * Pattern lock control implementation.
 */
public class PatternLockControl extends Control {
	// Default properties
	private String DEFAULT_STYLE_CLASS = "pattern-lock";
	private double DEFAULT_SIZE = 240.0D;
	private Insets DEFAULT_PADDING = new Insets(10);

	// Configurable properties
	private PatternSize size;
	private ObjectProperty<Insets> padding;
	private Callback<String, Boolean> onPatternDetected;

	/**
	 * Creates an instance of PatternLockControl with the default {@link #sizeProperty() size}.
	 */
	public PatternLockControl() {
		this(PatternSize.THREE);
	}

	/**
	 * Creates an instance of PatternLockControl with the {@link #sizeProperty() size} property set.
	 * 
	 * @param patternSize
	 *            The size of the pattern. The control will be rendered in n*n size.
	 */
	public PatternLockControl(PatternSize patternSize) {
		super();
		setSize(patternSize);
		configure();
	}

	/**
	 * Configures the required properties for the control.
	 */
	private void configure() {
		getStyleClass().setAll(new String[] { DEFAULT_STYLE_CLASS });
		setPrefSize(DEFAULT_SIZE, DEFAULT_SIZE);

	}

	/**
	 * Return the path to the CSS file so things are setup right.
	 */
	@Override
	protected String getUserAgentStylesheet() {
		return this.getClass().getResource(this.getClass().getSimpleName().toLowerCase() + ".css").toString();
	}

	/**
	 * Sets the size property that represents the pattern size to show. The pattern will be rendered with {@link PatternSize} enum size. The
	 * default value is <strong>PatternSize.THREE</strong>.
	 * 
	 * @return An enum property representing the size of the PatternLock control.
	 */
	public final void setSize(PatternSize patternSize) {
		if (getSkin() != null) {
			throw new IllegalStateException("Cannot set pattern size once the control is rendered");
		}
		this.size = patternSize;
	}

	public final PatternSize getSize() {
		return this.size;
	}

	/**
	 * Returns the callback that is called after a pattern is detected.
	 * 
	 * @return A callback which is called after a pattern is detected.
	 */
	public Callback<String, Boolean> getOnPatternDetected() {
		return onPatternDetected;
	}

	/**
	 * Sets the callback that need to be called when a pattern is detected.
	 * 
	 * @param onPatternDetected
	 *            A callback that need to be called after a pattern is detected.
	 */
	public void setOnPatternDetected(Callback<String, Boolean> onPatternDetected) {
		this.onPatternDetected = onPatternDetected;
	}

	/**
	 * The padding property represents the padding surrounding to the outer dots. The default value is <strong>10</strong> for all sides.
	 * 
	 * @return A property representing the padding of the PatternLock control.
	 */
	public final ObjectProperty<Insets> paddingProperty() {
		if (this.padding == null) {
			this.padding = new SimpleObjectProperty<>(this, "padding");
			this.padding.setValue(DEFAULT_PADDING);
		}
		return this.padding;
	}

	public final void setPadding(Insets padding) {
		paddingProperty().setValue(padding);
	}

	public final Insets getPadding() {
		return this.padding.getValue();
	}

	/**
	 * Clears the pattern.
	 */
	public void clear() {
		((PatternLockSkin) getSkin()).clearPattern();
	}

	/**
	 * An enumeration denoting the size of the pattern.
	 */
	public static enum PatternSize {
		/**
		 * Indicates that the pattern size is of 2 X 2 matrix.
		 */
		TWO(2),
		/**
		 * Indicates that the pattern size is of 3 X 3 matrix.
		 */
		THREE(3),
		/**
		 * Indicates that the pattern size is of 4 X 4 matrix.
		 */
		FOUR(4);

		private final int value;

		private PatternSize(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

}
