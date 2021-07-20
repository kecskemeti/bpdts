/*
 *  ========================================================================
 *  BPDTS
 *  ========================================================================
 *  
 *  This file is part of BPDTS.
 *  
 *  BPDTS is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free
 *  Software Foundation, either version 3 of the License, or (at your option)
 *  any later version.
 *  
 *  BPDTS is distributed in the hope that it will be useful, but WITHOUT ANY
 *  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 *  FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 *  details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with BPDTS.  If not, see <http://www.gnu.org/licenses/>.
 *  
 *  (C) Copyright 2021, Gabor Kecskemeti (kecskemeti@iit.uni-miskolc.hu)
 */

package bpdts;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * This class checks the correctness of the conversion of several pre-determined
 * and pre-converted latitude and longitude pairs.
 * 
 * @author Gabor Kecskemeti
 *
 */
@RunWith(Parameterized.class)
public class TestCoordsTransformation {
	private double lat, lon;
	private double x, y, z;

	public TestCoordsTransformation(double lat, double lon, double x, double y, double z) {
		this.lat = lat;
		this.lon = lon;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Parameterized.Parameters
	public static List<Double[]> params() {
		return Arrays.asList(new Double[][] {
				{ -80.65751133729341, -2.632302917359932, 641.9760528934322, -29.514658773121994, -3906.288913077075 },
				{ -71.5847526130728, -70.78459929649506, 411.5951086644542, -1180.9180058104862, -3756.077684244042 },
				{ 35.480035606603856, -10.005441914598073, 3174.6925985242788, -560.0948708867129, 2297.763712935158 },
				{ -32.19264117357945, -86.30681586573239, 215.79716963431943, -3343.2230600320577,
						-2109.1203361704374 },
				{ 76.80739104341677, 100.23806830081416, -160.58652686793272, 889.1124918270881, 3854.3207422664664 },
				{ 30.498026630037852, 36.97723488879365, 2725.030415900427, 2051.760669690849, 2009.1253885029928 },
				{ -67.313279878983, 95.43606366632903, -144.6487116173632, 1520.0112290614761, -3652.4977828541473 },
				{ 20.745395306495812, 60.64167093322107, 1815.041281701417, 3226.6642048131043, 1402.2698367637722 },
				{ 80.35147853277547, 97.11885292138095, -82.22739724295538, 658.3946224754478, 3902.8006118988533 },
				{ 11.010838027511811, -105.08755665159372, -1011.4855739160755, -3751.971809727278,
						756.1097227066209 }, });
	}

	@Test(timeout = 100)
	public void transformCheck() {
		var toEval = new EarthCentredCoordinates(new SphericalCoordinates(lat, lon));
		assertEquals("Mismatch at the x coordinate after conversion", x, toEval.x,
				TestSphericalCoords.doubleAcceptanceLimit);
		assertEquals("Mismatch at the y coordinate after conversion", y, toEval.y,
				TestSphericalCoords.doubleAcceptanceLimit);
		assertEquals("Mismatch at the z coordinate after conversion", z, toEval.z,
				TestSphericalCoords.doubleAcceptanceLimit);
	}

}
