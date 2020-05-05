import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 * Auto-generated code below aims at helping you parse the standard input
 * according to the problem statement.
 **/
class Player {

	static class Entity {
		int originalId;
		int id;
		int x, y;
		int dir;
		int rum;
		String type;
		int[] distanceFromShip = new int[3];
		int beforeImpact;
		int whoFiredIt;
		int speed;
		boolean targeted;
		int owner;
		boolean hasBarrelTarget;

		Entity(int originalId, int id, String type, int x, int y, int dir, int rum, int beforeImpact, int whoFiredIt,
				int speed) {
			this.originalId = originalId;
			this.id = id;
			this.type = type;
			this.x = x;
			this.y = y;
			this.dir = dir;
			this.rum = rum;
			this.distanceFromShip[0] = 100;
			this.distanceFromShip[1] = 100;
			this.distanceFromShip[2] = 100;
			this.beforeImpact = beforeImpact;
			this.whoFiredIt = whoFiredIt;
			this.speed = speed;
			this.targeted = false;
			this.owner = 100;
			this.hasBarrelTarget = false;
		}

		public String getType() {
			return type;
		}

		public int getRum() {
			return rum;
		}

		public void setRum(int rum) {
			this.rum = rum;
		}
	}

	static class Cube {
		int x, y, z;

		public Cube(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

	}

	public static Cube offset_to_cube(int x0, int y0) {
		int x = x0 - (int) ((y0 - (y0 & 1)) / 2);
		int y = y0;
		int z = -x - y;
		return new Cube(x, y, z);
	}

	public static int offset_distance(int x1, int y1, int x2, int y2) {
		Cube h1 = offset_to_cube(x1, y1);
		Cube h2 = offset_to_cube(x2, y2);
		return cube_distance(h1, h2);
	}

	public static int cube_distance(Cube a, Cube b) {
		// return Math.max(Math.abs(a.x - b.x), Math.max(Math.abs(a.y - b.y),
		// Math.abs(a.z - b.z)));
		return (int) ((Math.abs(a.x - b.x) + Math.abs(a.y - b.y) + Math.abs(a.z - b.z)) / 2);
	}

	public static int[] hex_neighbor(int[] start_hex, int dir, int step) {
		int[] outhex = new int[2];
		if (start_hex[1] % 2 == 0) {
			if (dir == 0) {
				outhex[0] = start_hex[0] + 1;
				outhex[1] = start_hex[1];
			} else if (dir == 1) {
				outhex[0] = start_hex[0];
				outhex[1] = start_hex[1] - 1;
			} else if (dir == 2) {
				outhex[0] = start_hex[0] - 1;
				outhex[1] = start_hex[1] - 1;
			} else if (dir == 3) {
				outhex[0] = start_hex[0] - 1;
				outhex[1] = start_hex[1];
			} else if (dir == 4) {
				outhex[0] = start_hex[0] - 1;
				outhex[1] = start_hex[1] + 1;
			} else if (dir == 5) {
				outhex[0] = start_hex[0];
				outhex[1] = start_hex[1] + 1;
			}
		} else {
			if (dir == 0) {
				outhex[0] = start_hex[0] + 1;
				outhex[1] = start_hex[1];
			} else if (dir == 1) {
				outhex[0] = start_hex[0] + 1;
				outhex[1] = start_hex[1] - 1;
			} else if (dir == 2) {
				outhex[0] = start_hex[0];
				outhex[1] = start_hex[1] - 1;
			} else if (dir == 3) {
				outhex[0] = start_hex[0] - 1;
				outhex[1] = start_hex[1];
			} else if (dir == 4) {
				outhex[0] = start_hex[0];
				outhex[1] = start_hex[1] + 1;
			} else if (dir == 5) {
				outhex[0] = start_hex[0] + 1;
				outhex[1] = start_hex[1] + 1;
			}
		}
		if (step > 1) {
			step--;
			return hex_neighbor(outhex, dir, step);
		} else
			return outhex;
	}

	public static int[] fireAlgorithm(int eshipX, int eshipY, int edir, int distanceTargetShip, int espeed,
			List<Entity> entities, int i, boolean selfkill) {
		int[] eshipStart = new int[2];
		int[] eshipEnd = new int[2];
		int[] myshipStart = new int[2];
		int[] myshipEnd = new int[2];
		int fireX = 0;
		int fireY = 0;
		int mynextX = 0;
		int mynextY = 0;

		eshipStart[0] = eshipX;
		eshipStart[1] = eshipY;
		eshipEnd = hex_neighbor(eshipStart, edir, ((int) Math.round((double) distanceTargetShip / 3) + 1) * espeed);
		fireX = eshipEnd[0];
		fireY = eshipEnd[1];
		System.err.println("edir: " + edir);
		System.err.println("distanceTargetShip: " + distanceTargetShip);
		System.err.println("espeed: " + espeed);
		System.err.println("Normal shoot: " + fireX + " " + fireY);

		// don't shoot out of the map
		if ((fireX < 0) || (fireX > 22))
			fireX = eshipX;
		if ((fireY < 0) || (fireY > 20))
			fireY = eshipY;

		if (!selfkill) {
			// me don't shoot on me
			for (Entity entity : entities) {
				if (entity.type.equals("mySHIP") && entity.id == i) {
					if ((entity.x == fireX) && (entity.y == fireY) && (entity.speed == 0)) {
						fireX = eshipX;
						fireY = eshipY;
					}

					myshipStart[0] = entity.x;
					myshipStart[1] = entity.y;
					myshipEnd = hex_neighbor(myshipStart, entity.dir * entity.speed, 1);
					mynextX = myshipEnd[0];
					mynextY = myshipEnd[1];

					if ((fireX == mynextX) && (fireY == mynextY) && (entity.speed > 0)) {
						fireX = eshipX;
						fireY = eshipY;
					}

					myshipEnd = hex_neighbor(myshipStart, entity.dir * entity.speed, 2);

					mynextX = myshipEnd[0];
					mynextY = myshipEnd[1];

					if ((fireX == mynextX) && (fireY == mynextY) && (entity.speed > 0)) {
						fireX = eshipX;
						fireY = eshipY;
					}

					myshipEnd = hex_neighbor(myshipStart, entity.dir * entity.speed, 3);

					mynextX = myshipEnd[0];
					mynextY = myshipEnd[1];

					if ((fireX == mynextX) && (fireY == mynextY) && (entity.speed > 0)) {
						fireX = eshipX;
						fireY = eshipY;
					}

					myshipEnd = hex_neighbor(myshipStart, entity.dir * entity.speed, 4);

					mynextX = myshipEnd[0];
					mynextY = myshipEnd[1];

					if ((fireX == mynextX) && (fireY == mynextY) && (entity.speed > 0)) {
						fireX = eshipX;
						fireY = eshipY;
					}

				}
			}
		}
		int fireTo[] = new int[2];
		fireTo[0] = fireX;
		fireTo[1] = fireY;
		System.err.println("FireAlg: " + fireX + " " + fireY);
		return fireTo;
	}

	public static String isCannonComing(List<Entity> entities, List<Integer> enemyShips, int shipX, int shipY, int i,
			int speed, int mydir) {
		String cannonAlertCommand = "";
		int[] myshipStart = new int[2];
		int[] myshipEnd = new int[2];
		int mynextX = 0;
		int mynextY = 0;
		for (Entity entity : entities) {
			if (entity.type.equals("CANNONBALL")) {

				// System.err.println("====BUMMMM====");
				if (entity.distanceFromShip[i] < 5) {
					/*
					 * System.err.println("=====Cannons"); if (enemyShips.indexOf(entity.whoFiredIt)
					 * != -1) { System.err.println("EnemyFired: " + entity.whoFiredIt); }else {
					 * System.err.println("WeFired: " + entity.whoFiredIt); }
					 * System.err.println("Distance: " + entity.distanceFromShip[i]);
					 * System.err.println("Impact: " + entity.beforeImpact);
					 * System.err.println("Myship: " + shipX + " " + shipY);
					 * System.err.println("MyshipSpeed: " + speed);
					 * System.err.println("Cannon target: " + entity.x + " " + entity.y);
					 */
					if (entity.beforeImpact == 2) {

						// Standing
						// middle of the ship
						if ((entity.x == shipX) && (entity.y == shipY) && (speed == 0)) {
							cannonAlertCommand = "FASTER";
							System.err.println("Impact 2, Speed 0, shipxy, middle");
						}

						// Speed=1
						// Middle
						myshipStart[0] = shipX;
						myshipStart[1] = shipY;
						myshipEnd = hex_neighbor(myshipStart, mydir, 2);
						mynextX = myshipEnd[0];
						mynextY = myshipEnd[1];

						if ((entity.x == mynextX) && (entity.y == mynextY) && (speed == 1)) {
							// cannonAlertCommand = "PORT";
							cannonAlertCommand = "SLOWER";
							System.err.println("Impact 2, Speed 1, Step 2, Middle");
						}

						// Front
						myshipStart[0] = shipX;
						myshipStart[1] = shipY;
						myshipEnd = hex_neighbor(myshipStart, mydir, 3);
						mynextX = myshipEnd[0];
						mynextY = myshipEnd[1];

						if ((entity.x == mynextX) && (entity.y == mynextY) && (speed == 1)) {
							cannonAlertCommand = "SLOWER";
							System.err.println("Impact 2, Speed 1, Step 3, front");
						}

						// Back
						myshipStart[0] = shipX;
						myshipStart[1] = shipY;
						myshipEnd = hex_neighbor(myshipStart, mydir, 1);
						mynextX = myshipEnd[0];
						mynextY = myshipEnd[1];

						if ((entity.x == mynextX) && (entity.y == mynextY) && (speed == 1)) {
							cannonAlertCommand = "FASTER";
							System.err.println("Impact 2, Speed 1, Step 1, back");
						}

						// Speed=2
						// Middle
						myshipStart[0] = shipX;
						myshipStart[1] = shipY;
						myshipEnd = hex_neighbor(myshipStart, mydir, 1);
						mynextX = myshipEnd[0];
						mynextY = myshipEnd[1];

						if ((entity.x == mynextX) && (entity.y == mynextY) && (speed == 2)) {
							// cannonAlertCommand = "PORT";
							System.err.println("Impact 2, Speed 2, Step 1, middle");
							cannonAlertCommand = "SLOWER";
						}

						// Front
						myshipStart[0] = shipX;
						myshipStart[1] = shipY;
						myshipEnd = hex_neighbor(myshipStart, mydir, 3);
						mynextX = myshipEnd[0];
						mynextY = myshipEnd[1];

						if ((entity.x == mynextX) && (entity.y == mynextY) && (speed == 2)) {
							System.err.println("Impact 2, Speed 2, Step 3, Front");
							cannonAlertCommand = "FASTER";
						}

						// Back
						myshipStart[0] = shipX;
						myshipStart[1] = shipY;
						myshipEnd = hex_neighbor(myshipStart, mydir, 1);
						mynextX = myshipEnd[0];
						mynextY = myshipEnd[1];

						if ((entity.x == mynextX) && (entity.y == mynextY) && (speed == 2)) {
							System.err.println("Impact 2, Speed 2, Step 1, back");
							cannonAlertCommand = "FASTER";
						}
					} else if (entity.beforeImpact == 1) {
						// Standing
						// middle of the ship
						if ((entity.x == shipX) && (entity.y == shipY) && (speed == 0)) {
							System.err.println("Impact 1, Speed 0, shipxy, middle");
							cannonAlertCommand = "FASTER";
						}

						// Front
						myshipStart[0] = shipX;
						myshipStart[1] = shipY;
						myshipEnd = hex_neighbor(myshipStart, mydir, 1);
						mynextX = myshipEnd[0];
						mynextY = myshipEnd[1];
						if ((entity.x == mynextX) && (entity.y == mynextY) && (speed == 0)) {
							System.err.println("Bef1 Speed0 Step1 Port");
							cannonAlertCommand = "PORT";
						}

						// Front+1 don't go if cannon is coming
						myshipStart[0] = shipX;
						myshipStart[1] = shipY;
						myshipEnd = hex_neighbor(myshipStart, mydir, 2);
						mynextX = myshipEnd[0];
						mynextY = myshipEnd[1];
						if ((entity.x == mynextX) && (entity.y == mynextY) && (speed == 0)) {
							System.err.println("Impact 1, Speed 0, Step 2, Front+1");
							cannonAlertCommand = "SLOWER";
						}

						// Back
						myshipStart[0] = shipX;
						myshipStart[1] = shipY;
						myshipEnd = hex_neighbor(myshipStart, (mydir + 3) % 6, 1);
						mynextX = myshipEnd[0];
						mynextY = myshipEnd[1];
						if ((entity.x == mynextX) && (entity.y == mynextY) && (speed == 0)) {
							System.err.println("Impact 1, Speed 0, Step 1, back");
							cannonAlertCommand = "PORT";
						}

						// Speed=1
						// Middle
						myshipStart[0] = shipX;
						myshipStart[1] = shipY;
						myshipEnd = hex_neighbor(myshipStart, mydir, 1);
						mynextX = myshipEnd[0];
						mynextY = myshipEnd[1];

						if ((entity.x == mynextX) && (entity.y == mynextY) && (speed == 1)) {
							// cannonAlertCommand = "PORT";
							System.err.println("Impact 1, Speed 1, Step 1, middle");
							cannonAlertCommand = "FASTER";
						}

						// Front
						myshipStart[0] = shipX;
						myshipStart[1] = shipY;
						myshipEnd = hex_neighbor(myshipStart, mydir, 2);
						mynextX = myshipEnd[0];
						mynextY = myshipEnd[1];

						if ((entity.x == mynextX) && (entity.y == mynextY) && (speed == 1)) {
							System.err.println("Impact 1, Speed 1, Step 2, Front");
							cannonAlertCommand = "SLOWER";
						}

						// Back
						if ((entity.x == shipX) && (entity.y == shipY) && (speed == 1)) {
							System.err.println("Impact 1, Speed 1, shipxy, Back");
							cannonAlertCommand = "FASTER";
						}

						// Speed=2
						// Middle
						myshipStart[0] = shipX;
						myshipStart[1] = shipY;
						myshipEnd = hex_neighbor(myshipStart, mydir, 2);
						mynextX = myshipEnd[0];
						mynextY = myshipEnd[1];

						if ((entity.x == mynextX) && (entity.y == mynextY) && (speed == 2)) {
							System.err.println("Impact 1, Speed 2, Step 2, middle");
							cannonAlertCommand = "PORT";
						}

						// Front
						myshipStart[0] = shipX;
						myshipStart[1] = shipY;
						myshipEnd = hex_neighbor(myshipStart, mydir, 3);
						mynextX = myshipEnd[0];
						mynextY = myshipEnd[1];

						if ((entity.x == mynextX) && (entity.y == mynextY) && (speed == 2)) {
							System.err.println("Impact 1, Speed 2, Step 3, Front");
							cannonAlertCommand = "PORT";
						}

						// Back
						myshipStart[0] = shipX;
						myshipStart[1] = shipY;
						myshipEnd = hex_neighbor(myshipStart, mydir, 1);
						mynextX = myshipEnd[0];
						mynextY = myshipEnd[1];

						if ((entity.x == mynextX) && (entity.y == mynextY) && (speed == 2)) {
							System.err.println("Impact 1, Speed 2, Step 1, Back");
							cannonAlertCommand = "FASTER";
						}
					} else if (entity.beforeImpact == 0) {
						// Front
						myshipStart[0] = shipX;
						myshipStart[1] = shipY;
						myshipEnd = hex_neighbor(myshipStart, mydir, 1);
						mynextX = myshipEnd[0];
						mynextY = myshipEnd[1];

						if ((entity.x == mynextX) && (entity.y == mynextY) && (speed == 1)) {
							System.err.println("Impact 0: " + mynextX + " " + mynextY + " " + speed);
							cannonAlertCommand = "SLOWER";
						}

					}
				}
			}
		}
		return cannonAlertCommand;
	}

	public static void main(String args[]) {
		Scanner in = new Scanner(System.in);
		int shipX = 0, shipY = 0, eshipX = 0, eshipY = 0;
		int toX, toY, fireX, fireY, mynextX, mynextY;
		int mineX = 0, mineY = 0;
		int[] prevX = new int[3];
		int[] prevY = new int[3];
		int[] eprevX = new int[3];
		int[] eprevY = new int[3];
		double minHeuristic;
		double nearestMine;
		int rum = 0;
		List<Entity> entities = new ArrayList<>();
		List<Entity> myShips = new ArrayList<>();

		int counter = 0;
		int myShipNum, eShipNum;
		int distanceShips, minDistanceShips;
		int distanceTargetShip;
		int speed = 0;
		boolean mineAlert;
		double minDistanceEnemyToAll = 0;
		double sumDistanceEnemyToAll = 0;
		int targetX = 0, targetY = 0;
		int minRum, minRumStanding;
		int dir = 0;
		int mydir = 0;
		int edir = 0;
		String action = "";
		int targetBarrelId = 0;
		double nearestBarellDistanceFromShip = 0;
		int barrelcount;
		int espeed = 0;
		int[] eshipStart = new int[2];
		int[] eshipEnd = new int[2];
		int[] myshipStart = new int[2];
		int[] myshipEnd = new int[2];
		boolean cannonAlert;
		boolean standingShip;
		String lastMoveCommand;
		String cannonAlertCommand = "";
		boolean stucked;
		int random;
		int[] fireTo = new int[2];

		List<Integer> enemyShips = new ArrayList<>();
		List<Integer> targetBarrelIds = new ArrayList<>();
		int minDistanceEshipBarrel;
		int barrelToFireX = 0, barrelToFireY = 0;
		boolean barrelCannoned;
		int nearestEnemy, nearestFriend;
		boolean safe = false;
		int shipFrontX, shipFrontY;
		boolean[] selfkill = new boolean[] { false, false, false };

		// game loop
		while (true) {
			// System.err.println("========SELF: "+selfkill[0]);
			// System.err.println("========SELF: "+selfkill[1]);
			// System.err.println("========SELF: "+selfkill[2]);
			stucked = false;
			barrelcount = 0;
			minRum = 100;
			counter++;
			toX = 0;
			toY = 0;
			myShipNum = 0;
			eShipNum = 0;
			minDistanceEnemyToAll = 1000;

			entities.clear();
			enemyShips.clear();
			targetBarrelIds.clear();
			myShips.clear();

			int myShipCount = in.nextInt(); // the number of remaining ships
			int entityCount = in.nextInt(); // the number of entities (e.g. ships, mines or cannonballs)
			for (int i = 0; i < entityCount; i++) {
				int entityId = in.nextInt();
				String entityType = in.next();
				int x = in.nextInt();
				int y = in.nextInt();
				int arg1 = in.nextInt();
				int arg2 = in.nextInt();
				int arg3 = in.nextInt();
				int arg4 = in.nextInt();
				// adding ships
				if (entityType.equals("SHIP")) {
					if (arg4 == 1) {
						Entity myShip = new Entity(entityId, myShipNum, "mySHIP", x, y, arg1, arg3, 0, 0, arg2);
						entities.add(myShip);
						myShips.add(myShip);
						myShipNum++;
					} else {
						entities.add(new Entity(entityId, eShipNum, "enemySHIP", x, y, arg1, arg3, 0, 0, arg2));
						eShipNum++;
						enemyShips.add(entityId);
					}
					// adding other entities
				} else if (entityType.equals("BARREL")) {
					entities.add(new Entity(entityId, 0, "BARREL", x, y, 0, arg1, 0, 0, 0));
				} else if (entityType.equals("CANNONBALL")) {
					entities.add(new Entity(entityId, 0, "CANNONBALL", x, y, 0, 0, arg2, arg1, 0));
				} else if (entityType.equals("MINE")) {
					entities.add(new Entity(entityId, 0, "MINE", x, y, 0, 0, 0, 0, 0));
				}
			}

			// calculating entity distances to each of my ships
			for (Entity entity : entities) {
				if (entity.type.equals("BARREL")) {
					barrelcount++;
					for (Entity entity2 : entities) {
						if (entity2.type.equals("mySHIP")) {
							entity.distanceFromShip[entity2.id] = offset_distance(entity2.x, entity2.y, entity.x,
									entity.y);
						}
					}
				}

				if (entity.type.equals("CANNONBALL")) {
					for (Entity entity2 : entities) {
						if (entity2.type.equals("mySHIP")) {
							entity.distanceFromShip[entity2.id] = offset_distance(entity2.x, entity2.y, entity.x,
									entity.y);
						}
					}
				}

				if (entity.type.equals("MINE")) {
					for (Entity entity2 : entities) {
						if (entity2.type.equals("mySHIP")) {
							entity.distanceFromShip[entity2.id] = offset_distance(entity2.x, entity2.y, entity.x,
									entity.y);
						}
					}
				}

				// set the weakest enemy ship
				if (entity.type.equals("enemySHIP")) {
					if (entity.rum < minRum && entity.rum > 0) {
						minRum = entity.rum;
						targetX = entity.x;
						targetY = entity.y;
					}
				}

			}

			standingShip = false;

			// sort the ships regarding to rum
			myShips.sort(Comparator.comparing(Entity::getRum));

			for (Entity myShip : myShips) {
				myShip.hasBarrelTarget = true;
				// System.err.println("Actual ship: " + myShip.originalId);

				// int minDistanceShipBarrel = 100;
				minHeuristic = 0;
				for (Entity entity : entities) {
					if (entity.type.equals("BARREL")) {
						/*
						 * if (entity.distanceFromShip[myShip.id] < minDistanceShipBarrel &&
						 * targetBarrelIds.indexOf(entity.originalId) == -1) { minDistanceShipBarrel =
						 * entity.distanceFromShip[myShip.id]; entity.owner = myShip.id; targetBarrelId
						 * = entity.originalId; }
						 */
						// don't check cannontargets
						/*
						 * System.err.println("Actual barrel in check: " + entity.originalId);
						 * System.err.println("B: "+entity.originalId);
						 * System.err.println("D: "+entity.distanceFromShip[myShip.id]);
						 * System.err.println("R: "+entity.rum); System.err.println("O: "+entity.owner);
						 * System.err.println("TBIDs: "+targetBarrelIds.toString());
						 */
						barrelCannoned = false;

						for (Entity cannonBall : entities) {
							if (cannonBall.type.equals("CANNONBALL")) {
								if ((cannonBall.x == entity.x) && (cannonBall.y == entity.y))
									barrelCannoned = true;
							}
						}
						// System.err.println(barrelCannoned);

						if ((!barrelCannoned) && (targetBarrelIds.indexOf(entity.originalId) == -1)
								&& ((((double) 1.0 / entity.distanceFromShip[myShip.id])
										* entity.rum) >= minHeuristic)) {
							minHeuristic = ((double) 1.0 / entity.distanceFromShip[myShip.id]) * entity.rum;
							// minDistanceShipBarrel = entity.distanceFromShip[myShip.id];
							entity.owner = myShip.id;
							targetBarrelId = entity.originalId;
							// System.err.println("Actual barrel is targetedOptionally: " +
							// entity.originalId);
						}
					}
				}

				// System.err.println("Actual target: " + targetBarrelId);

				targetBarrelIds.add(targetBarrelId);

				// System.err.println("Actual targetbarrellistcount: " +
				// targetBarrelIds.size());

				for (Entity entity : entities) {
					if (entity.type.equals("BARREL")) {
						if (targetBarrelIds.indexOf(entity.originalId) == -1) {
							entity.owner = 100;
						}
					}
				}

				/*
				 * System.err.println("=============Barrels=============="); for (Entity entity
				 * : entities) { if (entity.type.equals("BARREL")) {
				 * 
				 * System.err.println("\nB: "+entity.originalId);
				 * System.err.println("D: "+entity.distanceFromShip[0]+";"+entity.
				 * distanceFromShip[1]+";"+entity.distanceFromShip[2]);
				 * System.err.println("R: "+entity.rum); System.err.println("O: "+entity.owner);
				 * } }
				 */

			}

			// go over my ships
			for (int i = 0; i < myShipCount; i++) {
				// System.err.println("=======Ship: " + i);

				minHeuristic = 1;
				nearestMine = 11;
				cannonAlert = false;
				mineAlert = false;
				distanceTargetShip = 100;
				boolean faster = false;
				shipFrontX = 0;
				shipFrontY = 0;
				// go over the entities
				for (Entity entity : entities) {

					// set the next target barrel for the actual ship
					if (entity.type.equals("BARREL") && entity.owner == i) {
						minHeuristic = ((double) 1.0 / entity.distanceFromShip[i]) * entity.rum;
						toX = entity.x;
						toY = entity.y;
						targetBarrelId = entity.originalId;
						nearestBarellDistanceFromShip = entity.distanceFromShip[i];
					}

					// get the actual ship parameters
					if (entity.type.equals("mySHIP") && entity.id == i) {
						shipX = entity.x;
						shipY = entity.y;
						speed = entity.speed;
						mydir = entity.dir;
						rum = entity.rum;
						myshipStart[0] = shipX;
						myshipStart[1] = shipY;
						myshipEnd = hex_neighbor(myshipStart, mydir, 1 * speed);
						shipFrontX = myshipEnd[0];
						shipFrontY = myshipEnd[1];
					}

				}

				// check if cannonball is coming
				cannonAlertCommand = isCannonComing(entities, enemyShips, shipX, shipY, i, speed, mydir);

				// Mine section

				for (Entity entity : entities) {
					if (entity.type.equals("MINE") && (entity.distanceFromShip[i] < nearestMine)) {
						nearestMine = entity.distanceFromShip[i];
						mineX = entity.x;
						mineY = entity.y;
						mineAlert = true;
					}
				}

				// get the weakest enemy in shoot distance
				minRum = 100;
				minRumStanding = 100;

				for (Entity entity : entities) {
					if (entity.type.equals("enemySHIP")) {
						distanceShips = offset_distance(entity.x, entity.y, shipFrontX, shipFrontY);
						entity.distanceFromShip[i] = distanceShips;
						// System.err.println("S: "+i+" D: "+distanceShips);
						if (distanceShips <= 10 && entity.rum <= minRum && entity.rum > 0) {
							minRum = entity.rum;
							eshipX = entity.x;
							eshipY = entity.y;
							edir = entity.dir;
							espeed = entity.speed;
							distanceTargetShip = distanceShips;
						}
						if (distanceShips <= 10 && entity.speed == 0 && entity.rum <= minRumStanding) {
							minRumStanding = entity.rum;
							standingShip = true;
							eshipX = entity.x;
							eshipY = entity.y;
							break;
						}

					}
				}
				minDistanceEshipBarrel = 100;
				barrelToFireX = 0;
				barrelToFireY = 0;
				// get the nearest barrel of the weakest enemy in shoot distance
				for (Entity entity : entities) {
					if (entity.type.equals("BARREL")) {
						if ((offset_distance(entity.x, entity.y, eshipX, eshipY) < minDistanceEshipBarrel)
								&& (distanceTargetShip <= 10) && entity.originalId != targetBarrelId) {
							minDistanceEshipBarrel = offset_distance(entity.x, entity.y, eshipX, eshipY);
							barrelToFireX = entity.x;
							barrelToFireY = entity.y;
							// System.err.println("Eship: " + eshipX + " " + eshipY);
							// System.err.println("Mind: " + minDistanceEshipBarrel);
						}
					}
				}

				stucked = false;
				for (Entity ship : myShips) {
					if (offset_distance(shipX, shipY, ship.x, ship.y) < 3 && ship.speed == 0 && speed == 0
							&& ship.id != i) {
						stucked = true;
					}
				}

				// System.err.println("BarrelToFire: " + barrelToFireX + " " + barrelToFireY);

				if (stucked) {
					System.err.println("Stuck move " + i);
					if (shipX == 0) {
						System.out.println("MOVE 22 " + toY);
					} else if (shipX == 22) {
						System.out.println("MOVE 0 " + toY);
					} else if (shipY == 0) {
						System.out.println("MOVE " + toX + " 20");
					} else if (shipY == 20) {
						System.out.println("MOVE " + toX + " 0");
					} else {
						myshipStart[0] = shipX;
						myshipStart[1] = shipY;
						myshipEnd = hex_neighbor(myshipStart, mydir, 5);
						mynextX = myshipEnd[0];
						mynextY = myshipEnd[1];
						if (counter % 2 == 0)
							System.out.println("STARBOARD");
						else
							System.out.println("MOVE " + mynextX + " " + mynextY);
					}
				} else
				// cannonBall is near, so run
				if (!cannonAlertCommand.equals("")) {
					System.err.println("CannonAlert");
					System.out.println(cannonAlertCommand);

				} /*
					 * else if (offset_distance(eshipX, eshipY, toX, toY) <
					 * nearestBarellDistanceFromShip && nearestBarellDistanceFromShip <= 10 &&
					 * counter % 2 == 0) { // System.err.println("H: " +
					 * nearestBarellDistanceFromShip); // System.err.println("E: " +
					 * offset_distance(eshipX, eshipY, toX, toY)); System.out.println("FIRE " + toX
					 * + " " + toY); }
					 */
				else if (standingShip) {
					// System.err.println("StandingShip");
					System.out.println("FIRE " + eshipX + " " + eshipY);
				} else if ((distanceTargetShip <= 10) && (counter % 3 == 0)) {
					System.err.println("distance<10 " + distanceTargetShip);
					fireTo = fireAlgorithm(eshipX, eshipY, edir, distanceTargetShip, espeed, entities, i, false);
					fireX = fireTo[0];
					fireY = fireTo[1];
					System.err.println("T1: " + fireX + " " + fireY);
					System.out.println("FIRE " + fireX + " " + fireY);
				} else {

					if ((barrelcount == 0) || (minHeuristic < 1)) {
						toX = (int) (Math.random() * 8) + 8;
						toY = (int) (Math.random() * 7) + 7;

						// System.err.println("Random move");
						System.out.println("MOVE " + toX + " " + toY);

					} else {
						if (shipX == 0) {
							System.out.println("MOVE 22 " + toY);
						} else if (shipX == 22) {
							System.out.println("MOVE 0 " + toY);
						} else if (shipY == 0) {
							System.out.println("MOVE " + toX + " 20");
						} else if (shipY == 20) {
							System.out.println("MOVE " + toX + " 0");
						} else {
							System.err.println("Basic move");
							if (toX == 0 && toY == 0) {
								toX = (int) (Math.random() * 8) + 8;
								toY = (int) (Math.random() * 7) + 7;
							}
							System.out.println("MOVE " + toX + " " + toY);
						}
					}
				}
			}
		}
	}
}
