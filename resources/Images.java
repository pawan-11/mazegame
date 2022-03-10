package resources;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import util.Util;
import vc.GameMenu;

public final class Images { //final means class cant be extended/overriden

	public static Image disablednextbutton, greennextbutton, homebutton,
	createbutton, savebutton, rotatebutton, deletebutton,
	mazeworldbutton, princessbutton, mazewarbutton, mazemakerbutton,
	
	botball, whiteball, redball, yellowball, purpleball, //mw balls
	soccergoal, backnet,
	settingsbg, infotext, title, settingstitle, clock,
	
	mw_confetti, pw_confetti, propellor, explosion, war_explosion, //gifs
	
	whiteparachute, botparachute, redparachute, yellowparachute, purpleparachute,
	icon, uploadbutton, modeicon, mazeicon,
	
	addhuman, addbot, addbutton, addplayer, clearbutton,
	awd, lur, gyj, ad, lr, gj, awdsq, lurdm, mouse,
	
	botprince, whiteprince, blackprince, redprince, yellowprince,
	creditsbg,
	grasswall, grassbg, grassborder, waterwall, waterbg, waterborder, cavewall, caveborder, cavebg,
	
	castlewall, castlebg1, castlebg2, castlemazebg, castleborder, castlecage, opendoor, closedoor, princess, water, castleblock, bridge, castleshore,
	christmaswall, christmasbg, christmasborder, closedbox, openbox, christmasblock, santa, jelly, //pw
	
	blankcell,
	
	whitewarball, redwarball, greywarball, greenwarball, cyanwarball, bluewarball, yellowwarball, pinkwarball,
	bullet, health_icon,
	
	settingsbutton, musiconbtn, musicoffbtn, soundsonbtn, soundsoffbtn, colorsonbtn, imagesonbtn, resetbtn
	
	;
	
	
	static {
		disablednextbutton = getImg("disablednextbutton.png");
		greennextbutton = getImg("greennextbutton.png");
		homebutton = getImg("homebutton.png");
		mazemakerbutton = getImg("mazemakerbutton.png");
		
		savebutton = getImg("savebutton.png");
		rotatebutton = getImg("rotatebutton.png");
		deletebutton = getImg("deletebutton.png");
		createbutton = getImg("createbutton.png");

		mazeworldbutton = getImg("mazeworldbutton.png");
		princessbutton = getImg("princessbutton.png");
		mazewarbutton = getImg("mazewarbutton.png");
		
		creditsbg = getImg("credits.png");
		
		clock = getImg("clock.png");
		settingstitle = getImg("settingstitle.png");
		title = getImg("title.png");
		
		settingsbutton = getImg("settingsbutton.png");

		mw_confetti = getImg("mw_confetti.gif");
		pw_confetti = getImg("pw_confetti.gif");
		
		icon = getImg("icon.png");
		modeicon = getImg("modeicon.png");
		mazeicon = getImg("mazeicon.png");
		
		uploadbutton = getImg("upload.png");
		
		addplayer = getImg("addplayer.png");
		addhuman = getImg("addhuman.png");
		addbot = getImg("addbot.png");
		addbutton = getImg("addbutton.png");
		clearbutton = getImg("clearbutton.png");
		
		botball = getImg("botsoccerball.png");
		whiteball = getImg("whitesoccerball.png");
		redball = getImg("redsoccerball.png");
		yellowball = getImg("yellowsoccerball.png");
		purpleball = getImg("purplesoccerball.png");
		

		propellor = getImg("propellor.gif");
		whiteparachute = getImg("whiteparachute.png");
		botparachute = getImg("botparachute.png");
		redparachute = getImg("redparachute.png");
		yellowparachute = getImg("yellowparachute.png");
		purpleparachute = getImg("purpleparachute.png");
		
		backnet = getImg("backnet.png");
		soccergoal = getImg("soccergoal.png");
		
		botprince = getImg("botprince.png");
		whiteprince = getImg("whiteprince.png");
		redprince = getImg("redprince.png");
		yellowprince = getImg("yellowprince.png");
		blackprince = getImg("blackprince.png");
		
		grassbg = getImg("grassbg2.jpg");		
		grasswall = getImg("grasswall.png");
		grassborder = getImg("grassborder.png");
		
		cavebg = getImg("cavebg.jpg");
		
		cavewall = getImg("cavewall.png");
		caveborder = getImg("caveborder.png");
		
		waterbg = getImg("waterbg.png");
		waterwall = getImg("waterwall.png");
		waterborder = getImg("waterborder.png");
		
		castlebg1 = getImg("castlebg1.jpg");
		castlebg2 = getImg("castlebg1.jpg");
		castlemazebg = getImg("castlemazebg.jpg");
		castlewall = getImg("castlewall.png");
		castleshore = getImg("castleshore2.png");
		castleborder = getImg("castleborder.png");
		opendoor = getImg("opendoor.png");
		closedoor = getImg("closedoor.jpg");
		castlecage = getImg("castlecage.png");
		castleblock = getImg("castleblock.jpg");
		princess = getImg("princess.png");
		water = getImg("water2.png");
		bridge = getImg("bridge.jpg");
		
		blankcell = getImg("blankcell.png");
		castlebg1 = getImg("castlebg1.jpg");
		castlebg2 = getImg("castlebg2.jpg");
		
		christmaswall = getImg("christmaswall.png"); 
		christmasbg = getImg("christmasbg.jpg"); 
		christmasborder = getImg("christmasborder.jpg");
		closedbox = getImg("closedbox.jpg");
		openbox = getImg("openbox.png"); 
		christmasblock = getImg("christmasblock.jpg"); 
		santa = getImg("santa.png");
		jelly = getImg("jelly.jpg");
		
		whitewarball = getImg("warballwhite.png");
		redwarball = getImg("warballred.png");
		greywarball = getImg("warballgrey.png");
		greenwarball = getImg("warballgreen.png");
		cyanwarball = getImg("warballcyan.png");
		bluewarball = getImg("warballblue.png");
		yellowwarball = getImg("warballyellow.png");
		pinkwarball = getImg("warballpink.png");
		
		bullet = getImg("bullet.png");

		awd = getImg("awd.png");
		gyj = getImg("gyj.png");
		lur = getImg("lur.png");
		mouse = getImg("mouse.png");
		
		ad = getImg("ad.png");
		gj = getImg("gj.png");
		lr = getImg("lr.png");
		
		awdsq = getImg("awdsq.png");
		lurdm = getImg("lurdm.png");
		
		musiconbtn = getImg("musiconbtn.png");
		musicoffbtn = getImg("musicoffbtn.png");
		soundsonbtn = getImg("soundsonbtn.png");
		soundsoffbtn = getImg("soundsoffbtn.png");
		colorsonbtn = getImg("colorsonbtn.png");
		imagesonbtn = getImg("imagesonbtn.png");
		resetbtn = getImg("resetbtn.png");
		
		war_explosion = getImg("explosion.gif");
		health_icon = getImg("healthicon.png");
		
	}
	
	public static ArrayList<Image> menubgs = new ArrayList<Image>();
	static {
		Image menubg = null;
		String exts[] = {".jpg", ".png", ".jpeg", ".gif"};
		int bg_idx = 1;
		do {
			int ext_idx = 0;
			while (ext_idx < exts.length && (menubg = getImg("menubg"+bg_idx+exts[ext_idx])) == null)
				ext_idx++;
			bg_idx += 1;
			if (menubg != null)
				menubgs.add(menubg);
		} while (menubg != null);
		
		menubgs.add(getImg("menubgsnow.gif"));
	}
	
	
	private static Image getImg(String name) {
		URL url = Images.class.getResource("images/"+name);
		if (url == null) {
			if (GameMenu.print_missing_imgs)
				Util.print(name +" image not found");
			return null;
		}
		return new Image(url.toString());
	}
	
	
	public static Image askImg() {
		try {
			FileChooser filechooser = new FileChooser();
			filechooser.setInitialDirectory(new File(System.getProperty("user.home")));
			filechooser.setTitle("Open Image (jpg/png/gif)");
			FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.gif");
			filechooser.getExtensionFilters().add(filter);
			File file = filechooser.showOpenDialog(null);
			
			return new Image(file.toURI().toString());
		}
		catch (Exception e) {
		}
		
		return null;
	}
	
}
