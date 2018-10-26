package net.querz.mcaselector.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.querz.mcaselector.Config;
import net.querz.mcaselector.util.Helper;
import java.util.Optional;

//does not return something, but sets the configuration directly
public class SettingsDialog extends Dialog<SettingsDialog.Result> {

	/*
	* Region selection color and opacity
	* Chunk selection color and opacity
	* MCAFilePipe thread options:
	* - Number of threads for file reading
	* - Number of threads for processing
	* - Number of threads for writing
	* - Maximum amount of loaded files
	* toggle debug
	* */

	private static final int procCount = Runtime.getRuntime().availableProcessors();
	private static final long maxMem = Runtime.getRuntime().maxMemory();

	private Slider readThreadsSlider = createSlider(1, procCount, 1, 1);
	private Slider processThreadsSlider = createSlider(1, procCount * 2, 1, procCount);
	private Slider writeThreadsSlider = createSlider(1, procCount, 1, procCount < 4 ? procCount : 4);
	private Slider maxLoadedFilesSlider = createSlider(1, (int) Math.ceil(maxMem / 100_000_000L), 1, procCount + procCount / 2);
	private Button regionSelectionColorPreview = new Button();
	private Button chunkSelectionColorPreview = new Button();

	private Color regionSelectionColor = Config.getRegionSelectionColor();
	private Color chunkSelectionColor = Config.getChunkSelectionColor();

	public SettingsDialog(Stage primaryStage) {
		setTitle("Edit settings");
		initStyle(StageStyle.UTILITY);
		getDialogPane().getStyleClass().add("settings-dialog-pane");
		getDialogPane().getScene().getStylesheets().addAll(primaryStage.getScene().getStylesheets());
		getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		setResultConverter(c -> new Result(
				(int) readThreadsSlider.getValue(),
				(int) processThreadsSlider.getValue(),
				(int) writeThreadsSlider.getValue(),
				(int) maxLoadedFilesSlider.getValue(),
				regionSelectionColor,
				chunkSelectionColor
		));

		regionSelectionColorPreview.getStyleClass().clear();
		chunkSelectionColorPreview.getStyleClass().clear();
		regionSelectionColorPreview.getStyleClass().add("color-preview-button");
		chunkSelectionColorPreview.getStyleClass().add("color-preview-button");
		regionSelectionColorPreview.setBackground(new Background(new BackgroundFill(regionSelectionColor, CornerRadii.EMPTY, Insets.EMPTY)));
		chunkSelectionColorPreview.setBackground(new Background(new BackgroundFill(chunkSelectionColor, CornerRadii.EMPTY, Insets.EMPTY)));

		regionSelectionColorPreview.setOnMousePressed(e -> {
			Optional<Color> result = new ColorPicker(getDialogPane().getScene().getWindow(), regionSelectionColor).showColorPicker();
			result.ifPresent(c -> {
				regionSelectionColor = c;
				regionSelectionColorPreview.setBackground(new Background(new BackgroundFill(c, CornerRadii.EMPTY, Insets.EMPTY)));
			});
		});
		chunkSelectionColorPreview.setOnMousePressed(e -> {
			Optional<Color> result = new ColorPicker(getDialogPane().getScene().getWindow(), chunkSelectionColor).showColorPicker();
			result.ifPresent(c -> {
				chunkSelectionColor = c;
			});
		});

		GridPane grid = new GridPane();
		grid.getStyleClass().add("slider-grid-pane");
		grid.add(new Label("Read Threads"), 0, 0, 1, 1);
		grid.add(new Label("Process Threads"), 0, 1, 1, 1);
		grid.add(new Label("Write Threads"), 0, 2, 1, 1);
		grid.add(new Label("Max loaded Files"), 0, 3, 1, 1);
		grid.add(new Label("Region selection color"), 0, 4, 1, 1);
		grid.add(new Label("Chunk selection color"), 0, 5, 1, 1);
		grid.add(readThreadsSlider, 1, 0, 1, 1);
		grid.add(processThreadsSlider, 1, 1, 1, 1);
		grid.add(writeThreadsSlider, 1, 2, 1, 1);
		grid.add(maxLoadedFilesSlider, 1, 3, 1, 1);
		grid.add(regionSelectionColorPreview, 1, 4, 2, 1);
		grid.add(chunkSelectionColorPreview, 1, 5, 2, 1);
		grid.add(Helper.attachTextFieldToSlider(readThreadsSlider), 2, 0, 1, 1);
		grid.add(Helper.attachTextFieldToSlider(processThreadsSlider), 2, 1, 1, 1);
		grid.add(Helper.attachTextFieldToSlider(writeThreadsSlider), 2, 2, 1, 1);
		grid.add(Helper.attachTextFieldToSlider(maxLoadedFilesSlider), 2, 3, 1, 1);

		getDialogPane().setContent(grid);
	}

	private Slider createSlider(int min, int max, int steps, int init) {
		Slider slider = new Slider(min, max, init);
		slider.setMajorTickUnit(steps);
		slider.setMinorTickCount(0);
		slider.setBlockIncrement(steps);
		return slider;
	}

	public class Result {

		private int readThreads, processThreads, writeThreads, maxLoadedFiles;
		private Color regionColor, chunkColor;

		public Result(int readThreads, int processThreads, int writeThreads, int maxLoadedFiles, Color regionColor, Color chunkColor) {
			this.readThreads = readThreads;
			this.processThreads = processThreads;
			this.writeThreads = writeThreads;
			this.maxLoadedFiles = maxLoadedFiles;
			this.regionColor = regionColor;
			this.chunkColor = chunkColor;
		}

		public int getReadThreads() {
			return readThreads;
		}

		public int getProcessThreads() {
			return processThreads;
		}

		public int getWriteThreads() {
			return writeThreads;
		}

		public int getMaxLoadedFiles() {
			return maxLoadedFiles;
		}

		public Color getRegionColor() {
			return regionColor;
		}

		public Color getChunkColor() {
			return chunkColor;
		}
	}
}
