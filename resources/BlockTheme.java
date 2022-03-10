package resources;

import javafx.scene.image.Image;

public class BlockTheme {

	protected Image wall;
	protected String wall_color;
	
	public BlockTheme(Image wall, String wall_color) {
		this.wall = wall;
		this.wall_color = wall_color;
	}
	
	public Image getWall() {
		return wall;
	}
	
	public String getWallColor() {
		return wall_color;
	}
	
	public static class MWBlockTheme extends BlockTheme {
		
		public MWBlockTheme(Image wall, String wall_color) {
			super(wall, wall_color);
		}

	}
	
	public static class PWBlockTheme extends BlockTheme {

		private Image shore, liquid, block, opendoor, closedoor, bridge; //liquid shore
		private String shore_color, liquid_color, block_color, door_color, bridge_color;

		
		public PWBlockTheme(Image wall, Image shore, Image liquid, 
				Image opendoor, Image closedoor, Image block, Image bridge,
				String wall_color, String shore_color, String door_color, 
				String liquid_color, String block_color, String bridge_color) {
			super(wall, wall_color);
			
			this.shore = shore;
			this.liquid = liquid;
			this.opendoor = opendoor;
			this.closedoor = closedoor;
			this.block = block; 
			this.bridge = bridge;
			
			this.shore_color = shore_color;
			this.liquid_color = liquid_color;
			this.door_color = door_color;
			this.block_color = block_color;
			this.bridge_color = bridge_color;
		}
		
		public Image getShore() {
			return shore;
		}
		
		public Image getLiquid() {
			return liquid;
		}
		
		public Image getOpenDoor() {
			return opendoor;
		}
		
		public Image getClosedoor() {
			return closedoor;
		}
		
		public Image getBlock() {
			return block;
		}
		
		public Image getBridge() {
			return bridge;
		}
		
		public String getShoreColor() {
			return shore_color;
		}
		
		public String getLiquidColor() {
			return liquid_color;
		}
		
		public String getDoorColor() {
			return door_color;
		}

		public String getBlockColor() {
			return block_color;
		}
		
		public String getBridgeColor() {
			return bridge_color;
		}
	}
	
	public static class WarBlockTheme extends BlockTheme {
		
		private Image ground1, ground2; 
		private String ground_color1, ground_color2;
		
		public WarBlockTheme(Image wall, Image ground1, Image ground2, 
				String wall_color, String ground_color1, String ground_color2) {
			super(wall, wall_color);
			this.ground1 = ground1;
			this.ground2 = ground2;
			this.ground_color1 = ground_color1;
			this.ground_color2 = ground_color2;
		}
		
		public Image getGround1() {
			return ground1;
		}
		
		public Image getGround2() {
			return ground2;
		}
		
		public String getGroundColor1() {
			return ground_color1;
		}
		
		public String getGroundColor2() {
			return ground_color2;
		}
		
	}
	
}
