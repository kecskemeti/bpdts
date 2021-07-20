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

import bpdts.gen.model.User;

/**
 * This class allows the identification of points on the surface of the earth
 * with the well known latitude and longitude pair. The program assumes a
 * completely spherical earth, so there is no need for elevation information.
 * The stored coordinates are immutable.
 * 
 * @author Gabor Kecskemeti
 *
 */
public class SphericalCoordinates {
	// The coordinates to work with
	public final double latitude, longitude;

	/**
	 * Allows constructing a basic spherical coordinate object with specifying
	 * latitude and longitude values. This constructor checks the inputs and if they
	 * don't look like proper latitude/longitude values it does not instantiate the
	 * object.
	 * 
	 * @param la The latitude
	 * @param lo The longitude
	 * @throws IllegalArgumentException If the latitude is not within [-90,90] or if
	 *                                  the longitude is not within [-180,180].
	 */
	public SphericalCoordinates(final double la, final double lo) {
		if (la > 90 || la < -90 || lo > 180 || lo < -180)
			throw new IllegalArgumentException(
					"Latitude acceptable range: [-90,90]; Longitude acceptable range: [-180,180]");
		latitude = la;
		longitude = lo;
	}

	/**
	 * Allows constructing spherical coordinates by extracting user details.
	 * 
	 * @param u The user for whom we need the coordinates.
	 */
	public SphericalCoordinates(final User u) {
		this(u.getLatitude().doubleValue(), u.getLongitude().doubleValue());
	}
}