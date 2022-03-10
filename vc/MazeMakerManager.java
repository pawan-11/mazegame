package vc;

import resources.Images;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import gamemode.GameMode;
import gamemode.MazeWar;
import gamemode.MazeWorld;
import gamemode.PrincessWorld;
import util.ImageButton;
import util.ImageButton.TextImageButton;


public class MazeMakerManager extends Pane {


	private GameMode<?, ?, ?> selected_mode;
	private MazeWorld mw;
	private PrincessWorld pw;
	private MazeWar war;
	
	private MazeMaker<?, ?, ?> selected_maker;
	private MWMazeMaker mwmaze_maker;
	private PWMazeMaker pwmaze_maker;
	private WarMazeMaker warmaze_maker;
	
	private int width, height;

	private GridPane mazes_grid, modes_grid;
	private TextField rowstf, colstf;
	private Text rowsLb, colsLb;
	private HBox hbox = new HBox();
	private ImageButton homebutton, createbutton, backbutton;
	private Text replace_status, save_idx_status;

	private int save_idx;
	private boolean replace;


	public MazeMakerManager(MazeWorld mw, PrincessWorld pw, MazeWar war) {
		this.mw = mw;
		this.pw = pw;
		this.war = war; 
		selected_mode = mw;

		mwmaze_maker = new MWMazeMaker(mw, this);
		pwmaze_maker = new PWMazeMaker(pw, this);
		warmaze_maker = new WarMazeMaker(war, this);

		selected_maker = mwmaze_maker;

		addContent();
		addEvents();
		fixlayout();
		updateTheme();
		show_modes();
		updateSetting(selected_mode.get_mazes_size(), true);
	}


	private void addContent() {
		createbutton = new ImageButton(Images.createbutton);		
		homebutton = new ImageButton(Images.homebutton);
		backbutton = new ImageButton(Images.greennextbutton);

		replace_status = new Text();
		save_idx_status = new Text();	

		rowsLb = new Text("Rows:");
		colsLb = new Text("Columns:");	
		rowstf = new TextField("6");
		colstf = new TextField("10");

		hbox = new HBox();
		hbox.getChildren().addAll(rowsLb, rowstf, colsLb, colstf, createbutton);

		modes_grid = new GridPane();
		mazes_grid = new GridPane();

		this.getChildren().addAll(mazes_grid, modes_grid, replace_status, save_idx_status, hbox, homebutton, backbutton);
	}

	private void addEvents() {

		createbutton.setOnMouseClicked((m)->{
			try {
				int rows = Integer.parseInt(rowstf.getText());
				int cols = Integer.parseInt(colstf.getText());
				selected_maker.newMaze(rows, cols, save_idx);
				this.getScene().setRoot(selected_maker);
				selected_maker.requestFocus();
			}
			catch (Exception e) {
			}	
		});
		backbutton.setOnMouseClicked((e)->{
			show_modes();
		});

	}


	public void setBackScreen(Parent p) {
		homebutton.setOnMouseClicked(m->{
			this.getScene().setRoot(p);
			p.requestFocus();
		});
	}


	public void fixlayout() {
		hbox.setAlignment(Pos.CENTER);
		rowstf.setBorder(getBorder());
		rowstf.selectEnd();
		colstf.setBorder(getBorder());
		colstf.selectEnd();
		backbutton.setRotate(180);

		mazes_grid.setCache(true);
		mazes_grid.setCacheShape(true);
		mazes_grid.setCacheHint(CacheHint.SPEED);
		modes_grid.setCache(true);
		modes_grid.setCacheShape(true);
		modes_grid.setCacheHint(CacheHint.SPEED);

		this.setStyle("-fx-background-color: #EF8128;");
	}


	public void resize(int width, int height) {	
		this.width = width;
		this.height = height;
		int k = GameMenu.getK(width, height);

		homebutton.resize(k,k);
		backbutton.resize(k,k);
		createbutton.resize(k*3,k);
		updateModesGrid();
		updateMazesGrid();
		hbox.setSpacing(k/4);

		rowsLb.setFont(Font.font(GameMenu.game_font, k/2.5));
		rowstf.getLayoutBounds();
		rowstf.setMinWidth(k*1.5);
		rowstf.setPrefWidth(k*1.5);
		rowstf.setFont(Font.font(GameMenu.game_font, k/2.5));
		colsLb.setFont(Font.font(GameMenu.game_font, k/2.5));
		colstf.setMinWidth(k*1.5);
		colstf.setPrefWidth(k*1.5);
		colstf.setFont(Font.font(GameMenu.game_font, k/2.5));
		HBox.setMargin(createbutton, new Insets(0,k/2,0,k/4));

		save_idx_status.setFont(Font.font(GameMenu.game_font, k/3));
		replace_status.setFont(Font.font(GameMenu.game_font, k/3));


		this.layout();
		this.requestLayout();

		homebutton.setLayoutX(5);
		homebutton.setLayoutY(5);
		backbutton.setLayoutX(5);
		backbutton.setLayoutY(5);

		hbox.setLayoutX(width-hbox.getLayoutBounds().getWidth());
		hbox.setLayoutY(5);

		replace_status.setLayoutX(width-replace_status.getLayoutBounds().getWidth()-k/2);
		replace_status.setLayoutY(k*2);
		save_idx_status.setLayoutX(width-save_idx_status.getLayoutBounds().getWidth()-k/2);
		save_idx_status.setLayoutY(k*2.5);

		modes_grid.setLayoutX((width-modes_grid.getLayoutBounds().getWidth())/2);
		modes_grid.setLayoutY((height-modes_grid.getLayoutBounds().getHeight())/2);	
		mazes_grid.setLayoutX((width-mazes_grid.getLayoutBounds().getWidth())/2);
		mazes_grid.setLayoutY((height-mazes_grid.getLayoutBounds().getHeight())/2);

		mwmaze_maker.resize(width, height);
		pwmaze_maker.resize(width, height);
		warmaze_maker.resize(width, height);
	}

	public void updateTheme() {
		mwmaze_maker.updateTheme();
		pwmaze_maker.updateTheme();
		warmaze_maker.updateTheme();
		
		updateModesGrid();
	//	updateMazesGrid(); //TODO: test
	}
	
	private void show_modes() {
		mazes_grid.setVisible(false);
		modes_grid.setVisible(true);
		backbutton.setVisible(false);
		createbutton.setDisable(true);
		homebutton.setVisible(true);
		replace_status.setVisible(false);
		save_idx_status.setVisible(false);
	}

	private void show_mazes() {
		modes_grid.setVisible(false);
		mazes_grid.setVisible(true);
		homebutton.setVisible(false);
		backbutton.setVisible(true);
		createbutton.setDisable(false);
		replace_status.setVisible(true);
		save_idx_status.setVisible(true);
		updateMazesGrid();
	}


	private void updateModesGrid() {	
		GameMode<?,?,?> modes[] = {mw, pw, war};
		MazeMaker<?,?,?> makers[] = {mwmaze_maker, pwmaze_maker, warmaze_maker};
		String mode_names[] = {"Maze World",  "Princess World", "Maze War"};
		
		modes_grid.getChildren().clear();
		modes_grid.setMinSize(width*0.75, height*0.7);
		
		int cols = (int)Math.ceil((modes.length/Math.sqrt(modes.length)));
		int rows = (int)Math.ceil(Math.sqrt(modes.length));
		int w =(int)(width*0.75/cols);
		int l = (int)(height*0.7/(rows));
		int row = 0, col = 0;

		for (int i = 0; i < modes.length; i++) {

			final int j = i;

			if (GameMenu.classic) { //TODO: update it in updateTheme()
				Button b = new Button(mode_names[i]);
				b.setText(mode_names[i]);
				b.setWrapText(true);

				b.setPrefSize(w,l);
				b.setStyle(black_border);
				b.setFont(Font.font(GameMenu.game_font, 20));
				
				b.setOnMouseEntered(e->{
					b.setStyle(white_border);
				});
				b.setOnMouseExited(e->{
					b.setStyle(black_border);
				});
				b.setOnMouseClicked(k->{
					selected_mode = modes[j];
					selected_maker = makers[j];
					updateSetting(modes[j].get_mazes_size(), true); //for new maze
					show_mazes();
				});	
				
				modes_grid.add(b, col, row);
			}
			else {
				TextImageButton b = new TextImageButton(Images.modeicon, mode_names[i]);
				
				b.resize(w, l);
				b.setOnMouseClicked(k->{
					selected_mode = modes[j];
					selected_maker = makers[j];
					updateSetting(modes[j].get_mazes_size(), true); //for new maze
					show_mazes();
				});	

				modes_grid.add(b, col, row);				
			}

			col += 1;
			if (col == cols) {
				col = 0;
				row += 1;
			}
		}
	}

	public void updateMazesGrid() {
		mazes_grid.getChildren().clear();
		mazes_grid.setMinSize(width*0.75, height*0.7);
		if (selected_mode.get_mazes_size()==0) return;

		int cols = (int)Math.ceil((selected_mode.get_mazes_size()/Math.sqrt(selected_mode.get_mazes_size())));
		int rows = (int)Math.ceil(Math.sqrt(selected_mode.get_mazes_size()));
		int w =(int)(width*0.75/cols);
		int l = (int)(height*0.7/(rows));
		int col = 0, row = 0;

		for (int i = 0; i < selected_mode.get_mazes_size(); i++) {
			final int j = i;
			row = i/cols;
			col = i%cols;	
			
			if (GameMenu.classic) {
				Button b = new Button(""+i);
				b.setStyle(black_border);
				b.setPrefSize(w, l);
	
				b.setCache(true);
				b.setCacheShape(true);
				b.setCacheHint(CacheHint.SPEED);
	
				b.setOnMouseClicked((k)->{
	
					if (k.isShiftDown()) {
						updateSetting(j, !replace);
					}
					else {
						if (!replace)
							selected_maker.oldMaze(j, save_idx, replace);
						else 
							selected_maker.oldMaze(j, j, replace);
						this.getScene().setRoot(selected_maker);
						selected_maker.requestFocus();
					}
				});	
				b.setOnMouseEntered(e->{
					b.setStyle(white_border);
				});
				b.setOnMouseExited(e->{
					b.setStyle(black_border);
				});
				mazes_grid.add(b, col, row);
			}
			else {
				TextImageButton b = new TextImageButton(Images.mazeicon, i+"");
				b.resize(w, l);
	
				b.setOnMouseClicked((k)->{
	
					if (k.isShiftDown()) {
						updateSetting(j, !replace);
					}
					else {
						if (!replace)
							selected_maker.oldMaze(j, save_idx, replace);
						else 
							selected_maker.oldMaze(j, j, replace);
						this.getScene().setRoot(selected_maker);
						selected_maker.requestFocus();
					}
				});	

				mazes_grid.add(b, col, row);
			}
		}


	}


	protected void updateSetting(int save_idx, boolean replace) {
		this.save_idx = save_idx;
		this.replace = replace;
		replace_status.setText("Replace Maze: "+replace);
		save_idx_status.setText("Save Index: "+save_idx);	
	}


	private String black_border = "-fx-background-color:lime;-fx-background-radius:4;"
			+ "-fx-border-color:black;-fx-border-width: 3 3 3 3;-fx-background-insets: 2;";
	private String white_border = "-fx-background-color:#7bfc03;-fx-background-radius:4;"
			+ "-fx-border-color:white;-fx-border-width: 3 3 3 3;-fx-background-insets: 2;";

	/*

		Button b0 = new Button("Maze World");	
		b0.setOnMouseClicked(k->{
			selected_mode = mw;
			selected_maker = mwmaze_maker;
			updateSetting(mw.get_mazes_size(), true); //for new maze
			show_mazes();
		});		


		Button b1 = new Button("Princess World");	
		b1.setFont(Font.font(GameMenu.game_font, 20));
		b1.setOnMouseClicked(k->{
			selected_mode = pw;
			selected_maker = pwmaze_maker;
			updateSetting(pw.get_mazes_size(), true); //for new maze
			show_mazes();
		});	
				modes_grid.add(b0, 0, 0);
		modes_grid.add(b1, 1, 0);
	 */
}
