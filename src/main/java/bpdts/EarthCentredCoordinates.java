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

/**
 * 
 * This class offers a very simplistic representation of an earth centred 3D
 * coordinate system. The data in the objects of this class is immutable. The
 * class also allows transforming from spherical coordinates as well as
 * calculating distances between two points on the surface of the earth.
 * 
 * @author Gabor Kecskemeti
 *
 */
public class EarthCentredCoordinates {
	public static final double earthRadiusMiles = 3958.8;
	public static final double earthRadiusSquared = earthRadiusMiles * earthRadiusMiles;
	public static final double radPerDegree = Math.PI / 180;

	// The actual coordinates:
	public final double x, y, z;

	/**
	 * This constructor allows the more human readable spherical coordinates to be
	 * transformed to the earth centred ones.
	 * 
	 * @param sc The spherical coordinates to transform.
	 */
	public EarthCentredCoordinates(final SphericalCoordinates sc) {
		var latRad = radPerDegree * sc.latitude;
		var lonRad = radPerDegree * sc.longitude;
		this.x = earthRadiusMiles * Math.cos(latRad) * Math.cos(lonRad);
		this.y = earthRadiusMiles * Math.cos(latRad) * Math.sin(lonRad);
		this.z = earthRadiusMiles * Math.sin(latRad);
	}

	/**
	 * This method calculates distances between two points on the surface of the
	 * earth represented in earth centred coordinates. This assumes a spherical
	 * globe model (gets gradually worse as we get closer to the equator or to the
	 * poles). The calculations are done with straight lines (as the crow flies)
	 * between the two given points.
	 * 
	 * @param p1 The first coordinate to check distance from
	 * @param p2 The second coordinate to check distance to
	 * @return The distance between the coordinates.
	 */
	public static double distance(final EarthCentredCoordinates p1, final EarthCentredCoordinates p2) {
		return earthRadiusMiles
				* Math.acos(Math.max(-1, Math.min(1, (p1.x * p2.x + p1.y * p2.y + p1.z * p2.z) / earthRadiusSquared)));
	}

}