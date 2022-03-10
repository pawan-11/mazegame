package resources;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import resources.BlockTheme.MWBlockTheme;
import resources.BlockTheme.PWBlockTheme;
import resources.BlockTheme.WarBlockTheme;
import resources.MazeTheme.MWMazeTheme;
import resources.MazeTheme.PWMazeTheme;
import resources.MazeTheme.WarMazeTheme;
import resources.ModeTheme.MWTheme;
import resources.ModeTheme.PWTheme;
import resources.ModeTheme.WarTheme;
import resources.PlayerTheme.BulletTheme;
import resources.PlayerTheme.MWPlayerTheme;
import resources.PlayerTheme.PWPlayerTheme;
import resources.PlayerTheme.PrincessTheme;
import resources.PlayerTheme.WarPlayerTheme;

public abstract class Theme {


	public static MWTheme[] mw_themes = new MWTheme[3];
	public static PWTheme[] pw_themes = new PWTheme[1];
	public static WarTheme[] war_themes = new WarTheme[7];

	public static BulletTheme bullet_theme;

	
	static {
		MWBlockTheme classic_block = new MWBlockTheme(Images.cavewall, "#000000");
		MWBlockTheme cave_block = new MWBlockTheme(Images.cavewall, "#ffc20a");
		MWBlockTheme water_block = new MWBlockTheme(Images.waterwall, "#04380d");
		MWBlockTheme grass_block = new MWBlockTheme(Images.grasswall, "#ffffa4");

		MWMazeTheme classic_maze = new MWMazeTheme(classic_block);
		MWMazeTheme cave_maze = new MWMazeTheme(cave_block);
		MWMazeTheme water_maze = new MWMazeTheme(water_block);
		MWMazeTheme grass_maze = new MWMazeTheme(grass_block);

		MWPlayerTheme[] balls = {new MWPlayerTheme(Images.botball, Images.botparachute, Images.propellor),
				new MWPlayerTheme(Images.whiteball, Images.whiteparachute, Images.propellor),
				new MWPlayerTheme(Images.redball, Images.redparachute, Images.propellor),
				new MWPlayerTheme(Images.yellowball, Images.yellowparachute, Images.propellor),
				new MWPlayerTheme(Images.purpleball, Images.purpleparachute, Images.propellor)};

		MWTheme classic = new MWTheme(balls, classic_maze, Images.cavebg, Images.caveborder, "#ffffff", "#000000");
		MWTheme cave = new MWTheme(balls, cave_maze, Images.cavebg, Images.caveborder, "#455f8a", "#000000");
		MWTheme grass = new MWTheme(balls, grass_maze, Images.grassbg, Images.grassborder, "#75d92e", "#097807");
		MWTheme water = new MWTheme(balls, water_maze, Images.waterbg, Images.waterborder, "#00a9f7", "#783607");
		
		mw_themes[0] = cave;
		mw_themes[0] = classic;
		mw_themes[1] = grass;
		mw_themes[2] = water;
	}


	static {
		PWBlockTheme castle_block =  new PWBlockTheme(Images.castlewall, Images.castleshore, Images.water, Images.opendoor, Images.closedoor,
				Images.castleblock, Images.bridge, "#363636", "#363636", "#6e181e", "#0ca1eb", "#1b0133", "#8f3929");

		PWMazeTheme castle_maze = new PWMazeTheme(castle_block, Images.castlemazebg, "#a1a7ab");
		PWPlayerTheme[] princes = {new PWPlayerTheme(Images.botprince), new PWPlayerTheme(Images.whiteprince), new PWPlayerTheme(Images.blackprince), 
				new PWPlayerTheme(Images.redprince), new PWPlayerTheme(Images.yellowprince)};	
		PrincessTheme princess = new PrincessTheme(Images.princess);
		PWTheme castle = new PWTheme(princes, princess, castle_maze, Images.castlebg1, Images.castleborder, "#cb6bff", "#454545");

		PWBlockTheme christmas_block =  new PWBlockTheme(Images.christmaswall, Images.christmaswall, Images.jelly, Images.openbox, Images.closedbox,
				Images.castleblock, Images.bridge, "#363636", "#363636", "#6e181e", "#0ca1eb", "#1b0133", "#8f3929");
		PWMazeTheme christmas_maze = new PWMazeTheme(christmas_block, Images.castlemazebg, "#fecd24");
		PWPlayerTheme[] kids = {new PWPlayerTheme(Images.whiteprince), new PWPlayerTheme(Images.blackprince), 
				new PWPlayerTheme(Images.redprince), new PWPlayerTheme(Images.yellowprince)};	
		PrincessTheme santa = new PrincessTheme(Images.santa);
		PWTheme christmas = new PWTheme(kids, santa, christmas_maze, Images.christmasbg, Images.christmasborder, "#3cff00", "#04380d");
		
		pw_themes[0] = castle;
	}


	static {
		
	//	WarBlockTheme war_block = new WarBlockTheme(Images.waterwall, Images.water, Images.grassbg, "#000000", "#32a850", "#23eb1c");
		WarBlockTheme war_block1 = new WarBlockTheme(null, null, null,
				"#000000", "#ff47c5", "#ff80d7");
		WarMazeTheme war_maze1 = new WarMazeTheme(war_block1, null, "#455f8a");
		WarPlayerTheme[] balls1 = {new WarPlayerTheme(Images.greywarball), new WarPlayerTheme(Images.whitewarball),
				 new WarPlayerTheme(Images.yellowwarball)};
		WarTheme wartheme1 = new WarTheme(balls1, war_maze1, null, null, "#ff47c5", "#cf6b0e");
		
		WarBlockTheme war_block2 = new WarBlockTheme(null, null, null,
				"#000000", "#1abf08", "#3dcf2d");
		WarMazeTheme war_maze2 = new WarMazeTheme(war_block2, null, "#fcba03");
		WarPlayerTheme[] balls2 = {new WarPlayerTheme(Images.greywarball), new WarPlayerTheme(Images.redwarball),
				 new WarPlayerTheme(Images.pinkwarball), new WarPlayerTheme(Images.greenwarball)};
		WarTheme wartheme2 = new WarTheme(balls2, war_maze2, null, null, "#1abf08", "#0e7802");
		
		WarBlockTheme war_block3 = new WarBlockTheme(null, null, null,
				"#000000", "#38d1ff", "#30a9cf");
		WarMazeTheme war_maze3 = new WarMazeTheme(war_block3, null, "#fcba03");
		WarPlayerTheme[] balls3 = {new WarPlayerTheme(Images.greywarball), 
				new WarPlayerTheme(Images.cyanwarball), new WarPlayerTheme(Images.bluewarball)};
		WarTheme wartheme3 = new WarTheme(balls3, war_maze3, null, null, "30a9cf", "#036396");

		WarBlockTheme war_block4 = new WarBlockTheme(null, null, null,
				"#000000", "#ffffff", "#ffffff");
		WarMazeTheme war_maze4 = new WarMazeTheme(war_block4, null, "#fcba03");
		WarTheme wartheme4 = new WarTheme(balls1, war_maze4, null, null, "#ffffff", "#000000");
		
		WarBlockTheme war_block5 = new WarBlockTheme(null, null, null,
				"#000000", "#59340a", "#6e6152");
		WarMazeTheme war_maze5 = new WarMazeTheme(war_block5, null, "#59340a");
		WarTheme wartheme5 = new WarTheme(balls2, war_maze5, null, null, "#59340a", "#472600");
	
		WarBlockTheme war_block6 = new WarBlockTheme(null, null, null,
				"#000000", "#ff1808", "#d12b1f");
		WarMazeTheme war_maze6 = new WarMazeTheme(war_block6, null, "#d12b1f");
		WarTheme wartheme6 = new WarTheme(balls3, war_maze6, null, null, "#ff1808", "#872222");
		
		WarBlockTheme war_block7 = new WarBlockTheme(null, null, null,
				"#000000", "#004cff", "#1b44a6");
		WarMazeTheme war_maze7 = new WarMazeTheme(war_block7, null, "#1b44a6");
		WarTheme wartheme7 = new WarTheme(balls2, war_maze7, null, null, "#1b44a6", "#632f04");
		
		war_themes[0] = wartheme1; war_themes[3] = wartheme4;
		war_themes[1] = wartheme2; war_themes[4] = wartheme5;
		war_themes[2] = wartheme3; war_themes[5] = wartheme6;
		war_themes[6] = wartheme7;
		
	}
	
	static {
		bullet_theme = new BulletTheme(Images.bullet);
	}


}
