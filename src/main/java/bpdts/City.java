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

import static bpdts.Belonging.ALIEN;
import static bpdts.Belonging.CITIZEN;
import static bpdts.Belonging.UNKNOWN;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import com.google.gson.GsonBuilder;

import bpdts.gen.model.User;

/**
 * This class allows to specify a city and its perimeter. It also enables easy
 * checking if a user is a citizen or located nearby.
 * 
 * @author Gabor Kecskemeti
 *
 */
public class City {
	public final String name;
	// This list is unmodifiable as cities rarely change their boundaries
	public final List<EarthCentredCoordinates> perimeter;

	/**
	 * This constructor allows to programmatically instantiate a city object with
	 * coordinates and a name.
	 * 
	 * @param name   The name of the city that is represented by this object.
	 * @param coords The perimeter of the city in
	 */
	public City(final String name, final SphericalCoordinates[] coords) {
		this.name = name;
		this.perimeter = transformCoords(Arrays.stream(coords));
	}

	/**
	 * A helper method for the two main constructors that transforms all received
	 * spherical coordinates (i.e., latitude and longitude values) to earth centred
	 * ones for the perimeter. Storing the perimeter this way allows faster
	 * calculation of user distances.
	 * 
	 * @param coords The stream of spherical coordinates to convert before storing
	 *               the perimeter.
	 * @return The converted list of coordinates ready to be used as the city's
	 *         perimeter.
	 */
	private List<EarthCentredCoordinates> transformCoords(final Stream<SphericalCoordinates> coords) {
		var perPrep = new ArrayList<EarthCentredCoordinates>();
		coords.forEach(c -> perPrep.add(new EarthCentredCoordinates(c)));
		return Collections.unmodifiableList(perPrep);
	}

	/**
	 * This class allows easy loading of data from json files that can be written
	 * and read by the gson library rapidly. It depicts the same data that the outer
	 * class does but it only has strings, so needs further processing.
	 * 
	 * @author Gabor Kecskemeti
	 *
	 */
	public static class CityHelper {
		public String name;
		public ArrayList<String> perimeter;
	}

	/**
	 * This constructor provides a way to load city definitions from storage without
	 * the need to program (like one would need to do with the other constructor).
	 * 
	 * @param json The path to the json file that we need to read
	 * @throws IOException If the specified path is not readable by the program or
	 *                     if the json's format is incorrect.
	 */
	public City(final Path json) throws IOException {
		var reader = new GsonBuilder().create();
		var jsonData = Files.readString(json);
		var helper = reader.fromJson(jsonData, CityHelper.class);
		if (helper.perimeter == null || helper.name == null) {
			throw new IOException("Incorrect json format for city: no perimeter or name!");
		}
		if (helper.perimeter.size() == 0 || helper.perimeter.size() % 2 == 1) {
			throw new IOException("Input " + json + " misses the last longitude data");
		}

		// Json correctly loaded, spherical coordinates are still to be checked and
		// transofrmed to something digestable by the API

		var it = helper.perimeter.iterator();
		var tempList = new ArrayList<SphericalCoordinates>(helper.perimeter.size() / 2);
		while (it.hasNext()) {
			String latitude = it.next();
			String longitude = it.next();
			try {
				tempList.add(new SphericalCoordinates(Double.parseDouble(latitude), Double.parseDouble(longitude)));
			} catch (NumberFormatException nex) {
				throw new IOException(
						"Input " + json + " does not disclose latitude/longitude data as a simple decimal. See: Lat: "
								+ latitude + " Lon:" + longitude);
			}
		}

		// Conversion complete, we are doing the same thing that we do with the other
		// constructor

		this.name = helper.name;
		this.perimeter = transformCoords(tempList.stream());

	}

	/**
	 * The first user facing component of the class. Helps to determine the distance
	 * on the surface of the earth between the user and the city's closest perimeter
	 * point.
	 * 
	 * @param u The user to investigate
	 * @return The straight line distance of the user on Earth's surface.
	 */
	public double distanceOfUser(final User u) {
		var userLoc = new EarthCentredCoordinates(new SphericalCoordinates(u));
		return perimeter.stream().parallel().mapToDouble(c -> EarthCentredCoordinates.distance(c, userLoc)).min()
				.getAsDouble();
	}

	/**
	 * The other user facing component of the class allowing the query if the user
	 * has the same city details that we store in this city.
	 * 
	 * @param u The user to investigate
	 * @return
	 *         <ul>
	 *         <li>CITIZEN: the user lives in the city</li>
	 *         <li>ALIEN: the user does not live in the city</li>
	 *         <li>UNKNOWN: there is no residence information about the user in the
	 *         user's record</li>
	 *         </ul>
	 */
	public Belonging userBelongsToCity(final User u) {
		var usersCity = u.getCity();
		if (usersCity != null) {
			return usersCity.equals(name) ? CITIZEN : ALIEN;
		} else {
			return UNKNOWN;
		}
	}
}
