package codinggame.hypersonic;

import java.util.Random;
import java.util.Scanner;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
public class Player {

   private static int width = 13;
   private static int height = 11;

   public static void main(String args[]) {
      Scanner in = new Scanner(System.in);
      width = in.nextInt();
      height = in.nextInt();
      int myId = in.nextInt();
      System.err.println(myId);
      System.err.println();
      int[][] map = new int[height][width];
      Position destPos = new Position(0, 0);
      boolean mapBuilt = false;

      // game loop
      while (true) {
         for (int i = 0; i < height; i++) {
            String row = in.next();
            System.err.println(row);
            if (!mapBuilt) {
               buildMap(map, i, row);
            }

         }
         System.err.println();
         if (!mapBuilt) {
            mapBuilt = true;
            calculateMap(map);
            System.err.println("Done for build map");

         }
         printMap(map);
         System.err.println();
         int entities = in.nextInt();
         for (int i = 0; i < entities; i++) {
            int entityType = in.nextInt();
            int owner = in.nextInt();
            int y = in.nextInt();
            int x = in.nextInt();

            int param1 = in.nextInt();
            int param2 = in.nextInt();


            System.err.println(entityType + " " + owner + " " + x + " " + y + " " + param1 + " " + param2);

            if (entityType == 1) {
               removeBox(map, x, y);
//               printMap(map);
            }

            if (entityType == 0 && owner == myId) {

               System.err.println("x = " + x);
               System.err.println("y = " + y);
               System.err.println("destx = " + destPos.getX());
               System.err.println("desty = " + destPos.getY());
               if (destPos.getX() == x && destPos.getY() == y) {
                  boolean putBomb = false;

                  if ((x != 0 || y != 0) && map[x][y] > 0) {
                     System.err.println("put a bomb on (" + y + "," + x + ")");
                     removeBox(map, x, y);
                     printMap(map);
                     putBomb = true;
                  }

                  System.err.println("search for solutions");
                  Strategy.init();
                  destPos = Strategy.searchForBestPosLargeFirst(destPos, 10, map);
                  if (destPos != null) {
                     System.err.println("Found solution x = " + destPos.getX() + " y = " + destPos.getY());
                  } else {
                     System.err.println("I didn't find the solution");
                  }
                  if (destPos == null || (destPos.getX() == x && destPos.getY() == y)) {
                     destPos = getRamPosition(x, y, map);


                     System.err.println("Random solution x = " + destPos.getX() + " y = " + destPos.getY());
                  }


                  if (putBomb) {
                     System.out.println("BOMB " + destPos.getY() + " " + destPos.getX());
                  } else {
                     System.out.println("MOVE " + destPos.getY() + " " + destPos.getX());
                  }
               } else {
                  System.out.println("MOVE " + destPos.getY() + " " + destPos.getX());
               }
            }

         }

         // Write an action using System.out.println()
         // To debug: System.err.println("Debug messages...");

         System.err.println();

      }
   }

   private static Position getRamPosition(int x, int y, int[][] map) {

      Position position = new Position(x + 1, y);
      if (position.checkBoarder() && map[x + 1][y] != -1) {
         return position;
      }

      position = new Position(x, y + 1);
      if (position.checkBoarder() && map[x][y + 1] != -1) {
         return position;
      }

      Random randomGenerator = new Random();
      return new Position(randomGenerator.nextInt(height), randomGenerator.nextInt(width));

   }

   public static void printMap(int[][] map) {
      for (int i = 0; i < height; i++) {
         for (int j = 0; j < width; j++) {
            System.err.print(map[i][j] + " ");
         }
         System.err.println();
      }
   }

   private static int calculateValue(int[][] map, int x, int y) {

      int value = 0;
      for (int i = -2; i < 3; i ++) {
         int x1 = x + i;
         if (x1 >= 0 && x1 < height && map[x1][y] == -1) {
            value = 1 + value;
         }

      }

      for (int i = -2; i < 3; i ++) {

         int y1 = y + i;
         if (y1 >= 0 && y1 < width && map[x][y1] == -1) {
            value = 1 + value;
         }

      }

      return value;
   }

   public static void buildMap(int[][] map, int index, String row) {
      for (int j = 0; j < row.length(); j++) {
         map[index][j] = (byte) (row.charAt(j) == '.' ? 0 : -1);
      }

   }

   public static void calculateMap(int[][] map) {
      for (int i = 0; i < height; i++) {
         for (int j = 0; j < width; j++) {
            if (map[i][j] == 0) {
               map[i][j] = calculateValue(map, i, j);
            }
         }
      }
   }

   public static void removeBox(int[][] map, int x, int y) {
      for (int i = -2; i < 3; i ++) {
         int x1 = x + i;
         if (x1 >= 0 && x1 < height && map[x1][y] == -1) {
            map[x1][y] = 0;
            removeValue(map, x1, y);
         }

      }

      for (int i = -2; i < 3; i ++) {

         int y1 = y + i;
         if (y1 >= 0 && y1 < width) {
            map[x][y1] = 0;
            removeValue(map, x, y1);
         }

      }
   }

   private static void removeValue(int[][] map, int x, int y) {
      for (int i = -2; i < 3; i ++) {
         int x1 = x + i;
         if (x1 >= 0 && x1 < height && map[x1][y] > 0) {
            map[x1][y]--;
         }

      }

      for (int i = -2; i < 3; i ++) {

         int y1 = y + i;
         if (y1 >= 0 && y1 < width && map[x][y1] > 0) {
            map[x][y1]--;
         }

      }
   }

   public static final class Position {
      int x;
      int y;

      public Position(int x, int y) {
         this.x = x;
         this.y = y;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         }
         if (o == null || getClass() != o.getClass()) {
            return false;
         }

         Position position = (Position) o;

         if (x != position.x) {
            return false;
         }
         return y == position.y;

      }

      @Override
      public int hashCode() {
         int result = x;
         result = 31 * result + y;
         return result;
      }

      public int getX() {
         return x;
      }

      public int getY() {
         return y;
      }

      public boolean checkBoarder() {
         return x >= 0 && x < height && y >= 0 && y < width;
      }


   }

   public static class Strategy{

      static boolean[][] colorMap;

      public static void init() {
         colorMap = new boolean[height][width];
      }

      public static Position searchForBestPosDeepFirst(Position currPos, int lastSteps, int[][] map) {

         if (checkCondition(currPos, lastSteps, map)) {
            return null;
         }
         colorMap[currPos.getX()][currPos.getY()] = true;

         if (map[currPos.getX()][currPos.getY()] > 4) {
            return currPos;
         }

         Position solution = currPos;

         Position position = searchForBestPosDeepFirst(new Position(currPos.getX() - 1, currPos.getY()), lastSteps - 1, map);
         if (position != null && map[position.getX()][position.getY()] > map[currPos.getX()][currPos.getY()]
                 ) {
            solution = position;
         }
         position = searchForBestPosDeepFirst(new Position(currPos.getX() + 1, currPos.getY()), lastSteps - 1, map);
         if (position != null && map[position.getX()][position.getY()] > map[currPos.getX()][currPos.getY()]) {
            solution = position;
         }
         position = searchForBestPosDeepFirst(new Position(currPos.getX(), currPos.getY() + 1), lastSteps - 1,  map);
         if (position != null && map[position.getX()][position.getY()] > map[currPos.getX()][currPos.getY()]) {
            solution = position;
         }
         position = searchForBestPosDeepFirst(new Position(currPos.getX(), currPos.getY() - 1), lastSteps - 1, map);
         if (position != null && map[position.getX()][position.getY()] > map[currPos.getX()][currPos.getY()]) {
            solution = position;
         }

         return solution;
      }

      public static Position searchForBestPosLargeFirst(Position currPos, int lastSteps, int[][] map) {

         if (checkCondition(currPos, lastSteps, map)) {
            return null;
         }
         colorMap[currPos.getX()][currPos.getY()] = true;

         if (map[currPos.getX()][currPos.getY()] > 4) {
            return currPos;
         }
         Position solution = currPos;
         Position pos1 = new Position(currPos.getX() - 1, currPos.getY());
         if (!checkCondition(pos1, lastSteps - 1, map) && map[pos1.getX()][pos1.getY()] > map[currPos.getX()][currPos.getY()]) {
            solution = pos1;
         }
         Position pos2 = new Position(currPos.getX() + 1, currPos.getY());
         if (!checkCondition(pos2, lastSteps - 1, map) && map[pos2.getX()][pos2.getY()] > map[currPos.getX()][currPos.getY()]) {
            solution = pos2;
         }
         Position pos3 = new Position(currPos.getX(), currPos.getY() + 1);
         if (!checkCondition(pos3, lastSteps - 1, map) && map[pos3.getX()][pos3.getY()] > map[currPos.getX()][currPos.getY()]) {
            solution = pos3;
         }
         Position pos4 = new Position(currPos.getX(), currPos.getY() - 1);
         if (!checkCondition(pos4, lastSteps - 1, map) && map[pos4.getX()][pos4.getY()] > map[currPos.getX()][currPos.getY()]) {
            solution = pos4;
         }


         Position position = searchForBestPosLargeFirst(pos1, lastSteps - 1, map);
         if (position != null && map[position.getX()][position.getY()] > map[solution.getX()][solution.getY()]) {
            solution = position;
         }
         position = searchForBestPosLargeFirst(pos2,lastSteps - 1, map);
         if (position != null && map[position.getX()][position.getY()] > map[solution.getX()][solution.getY()]) {
            solution = position;
         }
         position = searchForBestPosLargeFirst(pos3, lastSteps - 1,  map);
         if (position != null && map[position.getX()][position.getY()] > map[solution.getX()][solution.getY()]) {
            solution = position;
         }
         position = searchForBestPosLargeFirst(pos4, lastSteps - 1, map);
         if (position != null && map[position.getX()][position.getY()] > map[solution.getX()][solution.getY()]) {
            solution = position;
         }

         return solution;
      }

      private static boolean checkCondition(Position currPos, int lastSteps, int[][] map) {
         return !currPos.checkBoarder() || map[currPos.getX()][currPos.getY()] == -1 || lastSteps < 0 || colorMap[currPos.getX()][currPos.getY()];
      }
   }
}