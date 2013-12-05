package com.sdandem.patternlock;

import com.sun.javafx.scene.control.skin.SkinBase;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.input.TouchPoint;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

/**
 * Skin implementation for the {@link PatternLock} control.
 *
 * @author Sai.Dandem
 *
 */
public class PatternLockSkin extends SkinBase<PatternLockControl, PatternLockBehavior> {

    private final StringBuilder code = new StringBuilder();
    private final SimpleBooleanProperty invalid = new SimpleBooleanProperty();
    private final SimpleDoubleProperty radius = new SimpleDoubleProperty(18);

    private final PatternLockControl control;
    private final AnchorPane anchorBase = new AnchorPane();

    private final List<Dot> dots = new ArrayList<>();
    private final List<Line> lines = new ArrayList<>();
    private final List<Node> arrows = new ArrayList<>();
    private final StackPane mask = new StackPane();
    private Line currentLine;

    private double startDragX;
    private double startDragY;
    private double pointX;
    private double pointY;

    /**
     * Creates an instance of the {@link PatternLockSkin} for the provided PatternLock control.
     *
     * @param control
     *                Instance of {@link PatternLockControl}.
     */
    public PatternLockSkin(PatternLockControl control) {
        super(control, new PatternLockBehavior(control));
        this.control = control;
        configure();
    }

    /**
     * Configures the layout and implementation for the skin.
     */
    private void configure() {
        paddingProperty().bind(control.paddingProperty());
        setAlignment(Pos.TOP_LEFT);
        getChildren().add(anchorBase);
        mask.getStyleClass().add("mask");

        anchorBase.getChildren().clear();
        final int size = control.getSize().getValue();
        int number = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                final Dot dot = new Dot(number++, i, j);
                addMouseEventHandlers(dot);
                addTouchEventHandlers(dot);
                anchorBase.getChildren().add(dot);
                dots.add(dot);
            }
        }
    }

    /**
     * Adds the mouse based event handlers to the dot.
     *
     * @param dot
     *            The dot object to which the handlers need to be added.
     */
    private void addMouseEventHandlers(final Dot dot) {
        final Circle dotRegion = dot.getOuter();
        dotRegion.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                evaluateForPressed(dot);
            }
        });
        
        dotRegion.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent arg0) {
            	dotRegion.startFullDrag();
            }
        });
        
       dotRegion.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                evaluateForDrag(mouseEvent.getX(), mouseEvent.getY());
            }
        });
        
        dotRegion.setOnMouseDragEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent arg0) {
                evaluateDragEntered(dot);
            }
        });

        dotRegion.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent arg0) {
                evaluateForReleased();
            }
        });
    }

    /**
     * Adds the mouse based event handlers to the dot.
     *
     * @param dot
     *            The dot object to which the handlers need to be added.
     */
    private void addTouchEventHandlers(final Dot dot) {
        final Circle dotRegion = dot.getOuter();
        dotRegion.setOnTouchPressed(new EventHandler<TouchEvent>() {
            @Override
            public void handle(TouchEvent event) {
                evaluateForPressed(dot);
            }
        });
        
        dotRegion.setOnTouchMoved(new EventHandler<TouchEvent>() {
            @Override
            public void handle(TouchEvent t) {
                TouchPoint tp = t.getTouchPoint();
                evaluateForDrag(tp.getX(), tp.getY());
            }
        });
        
        dotRegion.setOnTouchReleased(new EventHandler<TouchEvent>() {
            @Override
            public void handle(TouchEvent arg0) {
                evaluateForReleased();
            }
        });
    }
    
    /**
     * Toggles the mask over the control.
     *
     * @param show
     *             Flag to indicate whether to show the mask or not.
     */
    private void toggleMask(boolean show) {
        anchorBase.getChildren().remove(mask);
        if (show) {
            anchorBase.getChildren().add(mask);
        }
    }

    private void evaluateForPressed(final Dot dot) {
        clearPattern();

        code.append(dot.getNumber());
        dot.setActive(true);
        startDragX = dot.getTranslateX() + radius.getValue();
        startDragY = dot.getTranslateY() + radius.getValue();
        pointX = startDragX;
        pointY = startDragY;

        currentLine = buildNewLine();
        currentLine.setStartX(startDragX);
        currentLine.setStartY(startDragY);
        currentLine.setEndX(startDragX);
        currentLine.setEndY(startDragY);
        anchorBase.getChildren().add(currentLine);
    }

    private void evaluateForDrag(double x, double y) {
        final double sw = (currentLine.getStrokeWidth() - 1);
        double eX, eY;
        if (pointX > (x + startDragX)) {
            eX = x + startDragX + sw;
        } else {
            eX = x + startDragX - sw;
        }
        if (pointY > (y + startDragY)) {
            eY = y + startDragY + sw;
        } else {
            eY = y + startDragY - sw;
        }

        double delta = (currentLine.getStrokeWidth() / 2) + 2;
        if ((eX - delta) < 0) {
            currentLine.setEndX(0);
        } else if ((eX + delta) < anchorBase.getWidth()) {
            currentLine.setEndX(eX);
        } else {
            currentLine.setEndX(anchorBase.getWidth() - delta);
        }

        if ((eY - delta) < 0) {
            currentLine.setEndY(0);
        } else if ((eY + delta) < anchorBase.getHeight()) {
            currentLine.setEndY(eY);
        } else {
            currentLine.setEndY(anchorBase.getHeight() - delta);
        }
    }

    private void evaluateDragEntered(final Dot dot) {
        if (!dot.isActive()) {
            dot.setActive(true);
            double x = dot.getTranslateX() + radius.getValue();
            double y = dot.getTranslateY() + radius.getValue();

            pointX = x;
            pointY = y;
            currentLine.setEndX(x);
            currentLine.setEndY(y);
            lines.add(currentLine);
            addArrow(currentLine);

            code.append(dot.getNumber());
            currentLine = buildNewLine();
            currentLine.setStartX(x);
            currentLine.setStartY(y);
            currentLine.setEndX(x);
            currentLine.setEndY(y);
            anchorBase.getChildren().add(currentLine);
        }
    }

    private void evaluateForReleased() {
        if (currentLine != null) {
            anchorBase.getChildren().remove(currentLine);
        }
        if (control.getOnPatternDetected() != null) {
        	toggleMask(true);
            boolean isValid = control.getOnPatternDetected().call(code.toString());
            if (isValid) {
                clearPattern();
            } else {
                invalid.set(true);
           }
        } else {
            clearPattern();
        }
    }

    /**
     * Clears/resets the pattern.
     */
    public void clearPattern() {
        invalid.set(false);
        for (Dot d : dots) {
            d.setActive(false);
        }
        code.setLength(0);
        if (currentLine != null) {
            anchorBase.getChildren().remove(currentLine);
        }
        anchorBase.getChildren().removeAll(lines);
        anchorBase.getChildren().removeAll(arrows);
        lines.clear();
        arrows.clear();
        toggleMask(false);
    }

    /**
     * Adds an arrow to the provided line direction.
     *
     * @param l
     *          Line to which the arrow need to be pointed.
     */
    private void addArrow(Line l) {
        double x1 = l.getStartX();
        double y1 = l.getStartY();
        double x2 = l.getEndX();
        double y2 = l.getEndY();
        double dx = x2 - x1;
        double dy = y2 - y1;

        double angRad = Math.atan(dy / dx);
        double angDeg = ((angRad * 180) / Math.PI);
        double clockWiseAngle;
        if (dx > -1 && dy < 0) {
            clockWiseAngle = 360 + angDeg;
        } else if (dx < 0) {
            clockWiseAngle = 180 + angDeg;
        } else {
            clockWiseAngle = angDeg;
        }

        double dist = radius.getValue() + 5;
        double tx = (double)(dist * Math.cos(clockWiseAngle * Math.PI / 180F)) + x1;
        double ty = (double)(dist * Math.sin(clockWiseAngle * Math.PI / 180F)) + y1;

        Node arrow = buildArrow();
        arrow.setRotate(clockWiseAngle);
        arrow.setLayoutX(tx);
        arrow.setLayoutY(ty);
        arrows.add(arrow);
        anchorBase.getChildren().add(arrow);
    }

    /**
     * Builds a new line.
     *
     * @return A line node.
     */
    private Line buildNewLine() {
        final Line line = new Line();
        line.getStyleClass().add("line");
        return line;
    }

    /**
     * Builds a new arrow.
     *
     * @return An arrow node.
     */
    private StackPane buildArrow() {
        final double w = 6.0;
        final double h = 9.0;
        final StackPane arrow = new StackPane();
        arrow.getStyleClass().add("arrow");
        arrow.setMaxHeight(h);
        arrow.setPrefHeight(h);
        arrow.setMaxWidth(w);
        arrow.setPrefWidth(w);
        arrow.setTranslateX(-w / 2);
        arrow.setTranslateY(-h / 2);
        return arrow;
    }

    /**
     * Each dot node in the pattern.
     *
     * @author Sai.Dandem
     *
     */
    class Dot extends StackPane {
        private int number;

        private Circle outer;

        private Circle inner;

        private SimpleBooleanProperty active = new SimpleBooleanProperty();

        public Dot(final int number, final int row, final int col) {
            super();

            outer = new Circle();
            outer.getStyleClass().add("outer");

            inner = new Circle(5);
            inner.getStyleClass().add("inner");

            translateXProperty().bind(new DoubleBinding() {
                {
                    bind(anchorBase.widthProperty(), radius);
                }

                @Override
                protected double computeValue() {
                    double d = (anchorBase.getWidth() - (2 * radius.getValue())) / (control.getSize().getValue() - 1);
                    return (d * col);
                }
            });
            translateYProperty().bind(new DoubleBinding() {
                {
                    bind(anchorBase.widthProperty(), radius);
                }

                @Override
                protected double computeValue() {
                    double d = (anchorBase.getWidth() - (2 * radius.getValue())) / (control.getSize().getValue() - 1);
                    return (d * row);
                }
            });
            setNumber(number);
            outer.radiusProperty().bind(radius.subtract(2)); // excluding stroke width
            getChildren().addAll(inner,outer);
            maxWidthProperty().bind(radius.multiply(2));
            prefWidthProperty().bind(radius.multiply(2));
            maxHeightProperty().bind(radius.multiply(2));
            prefHeightProperty().bind(radius.multiply(2));

            active.addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> paramObservableValue, Boolean paramT1,
                        Boolean active) {
                    outer.getStyleClass().removeAll("outer-active", "outer-invalid");
                    inner.getStyleClass().removeAll("inner-active");
                    if (active) {
                        outer.getStyleClass().add("outer-active");
                        inner.getStyleClass().add("inner-active");
                    }
                }
            });

            invalid.addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> paramObservableValue, Boolean paramT1,
                        Boolean invalid) {
                    outer.getStyleClass().removeAll("outer-active", "outer-invalid");
                    if (isActive() && invalid) {
                        outer.getStyleClass().add("outer-invalid");
                    }
                }
            });
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public boolean isActive() {
            return active.get();
        }

        public void setActive(boolean active) {
            this.active.set(active);
        }

        public Circle getOuter() {
            return outer;
        }

    }
}