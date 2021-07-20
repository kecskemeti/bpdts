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

package bpdts.demo;

import java.nio.file.Path;

import bpdts.BpdtsAPI;
import bpdts.City;

/**
 * A demo application that can be run on the console and presents a simple,
 * single city query to the restful service with the help of the BpdtsAPI. In
 * order to run this program, you need to specify the url of the restful service
 * as the program's first and only command line argument. If you don't do so the
 * program will give a very brief help message.
 * 
 * In the resources folder, you can find several example json files which can be
 * used to test the behaviour with other cities.
 * 
 * NOTE: the application assumes that it is ran from the folder where you have
 * cloned the git repository.
 * 
 * @author Gabor Kecskemeti
 *
 */
public class DemoBPDTSApi {
	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.err.println("Usage: DemoBPDTSApi <url of user listing service>");
			System.exit(1);
		}
		var api = new BpdtsAPI(args[0]);
		var city = new City(Path.of(args.length >= 2 ? args[1] : "src/main/resources/GreaterLondon.json"));
		api.setCityToCheckAgainst(city);
		api.setMaxDistanceInMiles(50);
		System.out.println("Citizens of the city of " + city.name + ":");
		System.out.println(api.getUsersOfCity());
		System.out.println("\n\nUsers close to (or within) the city of " + city.name + ":");
		System.out.println(api.getUsersNearCity());
	}
}
