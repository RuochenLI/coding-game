package codinggame.hypersonic;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ruli on 9/27/2016.
 */
public class HyperSonicTest {

   @Test
   public void testBuildMap() {

      int[][] map = new int[11][13];

      String gris =
          ".....0.0..0..\n"
        + ".............\n"
        + "0...0.0.0...0\n"
        + ".............\n"
        + "0.0.0...0.0.0\n"
        + "......0......\n"
        + "0.0.0...0.0.0\n"
        + ".............\n"
        + "0...0.0.0...0\n"
        + ".............\n"
        + "..0..0.0..0..";

      String[] rows = gris.split("\n");
      for (int i = 0; i < rows.length; i++) {
         Player.buildMap(map, i, rows[i]);
      }
      Player.calculateMap(map);

      Assert.assertNotNull(map);
      Assert.assertEquals(2, map[0][4]);
   }

   @Test
   public void testPutBomb() {

      int[][] map = new int[11][13];

      String gris =
          "...0.0.0..0..\n"
        + "00..000......\n"
        + "0..00.0.0...0\n"
        + "...0.........\n"
        + "0.000...0.0.0\n"
        + "......0......\n"
        + "0.0.0...0.0.0\n"
        + ".............\n"
        + "0...0.0.0...0\n"
        + ".............\n"
        + "..0..0.0..0..";

      String[] rows = gris.split("\n");
      for (int i = 0; i < rows.length; i++) {
         Player.buildMap(map, i, rows[i]);
      }
      Player.calculateMap(map);
      Assert.assertNotNull(map);
      Assert.assertEquals(6, map[1][3]);

      Player.removeBox(map, 1, 3);

      Assert.assertNotNull(map);
      Assert.assertEquals(2, map[0][0]);
      Assert.assertEquals(0, map[1][3]);
   }

   @Test
   public void testFindBestPos() {
      int[][] map = new int[11][13];

      String gris =
            ".....0.0..0..\n"
          + "000.000......\n"
          + "0..00.0.0...0\n"
          + "...0.........\n"
          + "0.000...0.0.0\n"
          + "......0......\n"
          + "0.0.0...0.0.0\n"
          + ".............\n"
          + "0...0.0.0...0\n"
          + ".............\n"
          + "..0..0.0..0..";

      String[] rows = gris.split("\n");
      for (int i = 0; i < rows.length; i++) {
         Player.buildMap(map, i, rows[i]);
      }
      Player.calculateMap(map);

      Player.Strategy.init();
      Player.Position bestPos = Player.Strategy.searchForBestPosDeepFirst(new Player.Position(0, 0), 8, map);

      Assert.assertEquals(0, bestPos.getX());
      Assert.assertEquals(4, bestPos.getY());

   }

   @Test
   public void testFindBestPos2() {
      int[][] map = new int[11][13];

      String gris =
            "..0.0...0.0..\n"
          + ".............\n"
          + ".0..0.0.0..0.\n"
          + ".............\n"
          + "..0.0.0.0.0..\n"
          + "0...........0\n"
          + "..0.0.0.0.0..\n"
          + ".............\n"
          + ".0..0.0.0..0.\n"
          + ".............\n"
          + "..0.0...0.0..";

      String[] rows = gris.split("\n");
      for (int i = 0; i < rows.length; i++) {
         Player.buildMap(map, i, rows[i]);
      }
      Player.calculateMap(map);

      Player.Strategy.init();
      Player.Position bestPos = Player.Strategy.searchForBestPosDeepFirst(new Player.Position(0, 0), 8, map);

      Assert.assertEquals(0, bestPos.getX());
      Assert.assertEquals(4, bestPos.getY());
   }

   @Test
   public void testFindBestPos3() {
      int[][] map = new int[11][13];

      String gris =
             ".....0.0.0...\n" +
             "...........0.\n" +
             ".............\n" +
             "....0.0.0....\n" +
             ".0.........0.\n" +
             "....0.0.0....\n" +
             ".0.........0.\n" +
             "....0.0.0....\n" +
             ".............\n" +
             ".0.........0.\n" +
             "...0.0.0.0...";

      String[] rows = gris.split("\n");
      for (int i = 0; i < rows.length; i++) {
         Player.buildMap(map, i, rows[i]);
      }
      Player.calculateMap(map);

      Player.Strategy.init();
      Player.Position bestPos = Player.Strategy.searchForBestPosLargeFirst(new Player.Position(0, 1), 10, map);

      Assert.assertEquals(5, bestPos.getX());
      Assert.assertEquals(1, bestPos.getY());
   }
}
