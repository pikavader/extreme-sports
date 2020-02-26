package extremeServer;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.PriorityQueue;

public class Hierarchy {
	private class Country {
		private String parentSport;
		private String name;
		private HashMap<String, Region> regions;
		
		public Country(String name, String parentSport) {
			this.parentSport = parentSport;
			this.name = name;
			
			regions = new HashMap<>();
		}
		
		public String GetName() {
			return this.name;
		}
		
		public Region GetRegion(String regionName) {
			if (regions.containsKey(regionName)) {
				return regions.get(regionName);
			}
			return null;
		}
		
		public LinkedList<Region> GetRegions() {
			LinkedList<Region> regionList = new LinkedList<>();
			Iterator<Entry<String, Region>> it = regions.entrySet().iterator();
			while (it.hasNext()) {
				regionList.add(it.next().getValue());
			}
			return regionList;
		}
		
		public boolean RemoveRegion(String regionName) {
			if (regions.containsKey(regionName)) {
				regions.remove(regionName);
				return true;
			}
			return false;
		}
		
		public boolean isEmpty() {
			return regions.isEmpty();
		}
		
		public boolean CheckValid() {
			// TODO
			return true;
		}
		
		public int AddRegion(Region region) {
			if (!region.checkValid()) {
				return ResponseType.REGION_INV;
			}
			if (regions.containsKey(region.name)) {
				return ResponseType.REGION_TRUE;
			}
			regions.put(region.name, region);
			return ResponseType.OP_SUC;
		}
		
		public String toString() {
			String str = "\t" + name + "\n";
			Iterator<Entry<String, Region>> it = regions.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, Region> entry = it.next();
				str += "\t" + entry.getValue().toString() + "\n";
			}
			return str;
		}
	}
	
	private class Region {
		private Country parentCountry;
		private String name;
		private HashMap<String, City> cities;
		
		public Region(String name, Country parentCountry) {
			this.parentCountry = parentCountry;
			this.name = name;
			
			cities = new HashMap<>();
		}
		
		public String GetName() {
			return this.name;
		}
		
		public LinkedList<City> GetCities() {
			LinkedList<City> cityList = new LinkedList<>();
			Iterator<Entry<String,City>> it = cities.entrySet().iterator();
			while (it.hasNext()) {
				cityList.add(it.next().getValue());
			}
			return cityList;
		}
		
		public City GetCity(String cityName) {
			if (cities.containsKey(cityName)) {
				return cities.get(cityName);
			} else {
				return null;
			}
		}
		
		public boolean RemoveCity(String cityName) {
			if (cities.containsKey(cityName)) {
				cities.remove(cityName);
				return true;
			}
			return false;
		}
		
		public boolean checkValid() {
			// TODO
			return true;
		}
		
		public boolean isEmpty() {
			return cities.isEmpty();
		}
		
		public int AddCity(City city) {
			if (!city.checkValid()) {
				return ResponseType.CITY_INV;
			} else if (cities.containsKey(city.GetName())) {
				return ResponseType.CITY_TRUE;
			}
			cities.put(city.GetName(), city);
			return ResponseType.OP_SUC;
		}

		public String toString() {
			String str = "\t" + name + "\n";
			Iterator<Entry<String, City>> it = cities.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, City> entry = it.next();
				str += "\t\t\t" + entry.getValue().toString();
			}
			return str;
		}
	}
	
	private class City {
		private Region parentRegion;
		private String name;
		private LinkedList<Interval> intervals;
		
		public City (String name, Region parentRegion) {
			this.name = name;
			this.parentRegion = parentRegion;
			intervals = new LinkedList<>();
		}
		
		public boolean checkValid() {
			// TODO
			return true;
		}
		
		public boolean isEmpty() {
			return intervals.isEmpty();
		}
		
		public String GetName() {
			return name;
		}
		
		public String toString() {
			return new String(name + intervals.toString());
		}
		
		public String GetPeriodsString() {
			String str = "";
			Iterator<Interval> it = intervals.listIterator();
			while (it.hasNext()) {
				Interval per = it.next();
				str += per.toString();
				if (it.hasNext()) {
					str += ExtremeServer.DELIM_SERIAL;
				} else {
					break;
				}
			}
			return str;
		}
		
		public LocalDate GetEarliest() {
			if (intervals.size() == 0) {
				return null;
			}
			return intervals.getFirst().GetStart();
		}
		
		public LocalDate GetLatest() {
			if (intervals.size() == 0) {
				return null;
			}
			return intervals.getLast().GetEnd();
		}
		
		/*
		 * TODO: make a proper search
		 */
		
		/*
		 * finds intervals that combined match the given interval
		 * 			[start  	        				interval 		     						end]
		 * [start  intervals[i]   end=intervals[i+1].start-1day]...[start=intervals[i+k-1].end-1day 		intervals[i+k]  		end]
		 */
		public LinkedList<Interval> FindExactOverlap(Interval interval) {
			LinkedList<Interval> intervalsList = new LinkedList<>();
			
			ListIterator<Interval> it = intervals.listIterator();
			Interval listInterval;
			while (it.hasNext()) {
				listInterval = it.next();
				if (listInterval.CheckOverlap(interval) == 0) {
					Interval[] temporaryIntervals = listInterval.SubtractAndGetIntersection(interval);
					/*
					 * if interval.start is not after listInterval.start, there is no perfect overlap
					 */
					if (temporaryIntervals == null) {
						return null;
					}
					/*
					 * we don't need the before part, only current overlap
					 */
					intervalsList.add(temporaryIntervals[1]);
					/*
					 * continue checking using the after part
					 */
					if (temporaryIntervals[2] == null) {
						return intervalsList;
					}
					interval = temporaryIntervals[2];
				} else if (listInterval.CheckOverlap(interval) < 0) {
					/*
					 * could not find a perfect overlap for the given interval
					 */
					return null;
				}
			}
			
			/*
			 * could not find any matching intervals, returning null
			 */
			return null;
		}
		
		/*
		 * adds a interval different from the others to list of intervals
		 * if there are any overlaps, the method returns a PERIOD_OVERLAP error
		 */
		public int AddToPeriods(Interval interval) {
			ListIterator<Interval> it = intervals.listIterator();
			Interval listPeriod;
			while (it.hasNext()) {
				listPeriod = it.next();
				if (listPeriod.CheckOverlap(interval) == 0) {
					/*
					 * given interval overlaps this one
					 * abort add
					 */
					return ResponseType.PERIOD_OVERLAP;
				} else if (listPeriod.CheckOverlap(interval) < 0) {
					/*
					 * there were no errors and the given interval ends before this one
					 * add before this interval - before 'it' 's position
					 */
					it.previous();
					it.add(interval);
					return ResponseType.OP_SUC;
				}
			}
			/*
			 * interval happens after the other intervals in this list
			 * add interval at the end of the list
			 */
			intervals.add(interval);
			return ResponseType.OP_SUC;
		}
		
		/*
		 * updates intervals that overlap to the price from the given interval
		 */
		public int UpdatePeriods(Interval interval) {
			ListIterator<Interval> it = intervals.listIterator();
			Interval listPeriod;
			boolean changed = false;
			while (it.hasNext()) {
				listPeriod = it.next();
				if (listPeriod.CheckOverlap(interval) == 0) {
					/*
					 * given interval overlaps this one
					 * remove this interval
					 * add updated intervals
					 */
					Interval[] intervalsToAdd = listPeriod.UpdatePriceForOverlappingInterval(interval);
					if (it.hasPrevious()) {
						it.previous();
					}
					it.remove();
					for (int i = 0; i < intervalsToAdd.length; ++i) {
						it.add(intervalsToAdd[i]);
					}
					changed = true;
				} else if (listPeriod.CheckOverlap(interval) < 0) {
					/*
					 * interval out of bounds
					 */
					return ResponseType.OP_SUC;
				}
			}
			if (changed) {
				return ResponseType.OP_SUC;
			}
			/*
			 * interval is not defined
			 */
			return ResponseType.PERIOD_NONE;
		}
		
		/*
		 * adds new interval and updates other intervals only if the given interval overlaps any other interval
		 */
		public int AddToIntervalsAndUpdate(Interval interval) {
			ListIterator<Interval> it = intervals.listIterator();
			Interval listInterval;
			boolean addNext = false;
			while (it.hasNext()) {
				listInterval = it.next();
				if (listInterval.CheckOverlap(interval) == 0) {
					/*
					 * given interval overlaps this one
					 * remove this interval
					 * add updated intervals
					 * change interval to the last interval in the returned array and check for other overlaps
					 */
					Interval[] intervalsToAdd = listInterval.CreateNewIntervalsFromOverlapsAndUpdatePrice(interval);
					it.remove();
					for (int i = 0; i < intervalsToAdd.length - 1; ++i) {
						it.add(intervalsToAdd[i]);
					}
					interval = intervalsToAdd[intervalsToAdd.length - 1];
					addNext = true;
				} else if (listInterval.CheckOverlap(interval) < 0 && addNext) {
					/*
					 * there were no errors and the given interval ends before this one
					 * add before this interval - before 'it' 's position
					 */
					if (it.hasPrevious()) {
						it.previous();
					}
					it.add(interval);
					return ResponseType.OP_SUC;
				}
			}
			/*
			 * interval is not defined
			 */
			return ResponseType.PERIOD_NONE;
		}

	}
	
	private class Interval {
		private LocalDate start;
		private LocalDate end;
		private int price;
		
		public Interval(LocalDate start, LocalDate end, int price) {
			this.start = start;
			this.end = end;
			this.price = price;
		}
		
		public Interval(Interval interval) {
			/*
			 * LocalDate is immutable, no need to clone
			 */
			this.start = interval.GetStart();
			this.end = interval.GetEnd();
			this.price = interval.GetPrice();
		}
		
		public String toString() {
			return new String(start.toString() + "->" + end.toString() + "@" + price);
		}
		
		public void SetPrice(int price) {
			this.price = price;
		}

		/*
		 * return values: 
		 * -1 if the given interval is before this one
		 * 0 if the given interval overlaps this one
		 * 1 if the given interval is after this one
		 */
		public int CheckOverlap(Interval interval) {
			if (interval.GetStart().isAfter(end)) {
				return 1;
			} else if (interval.GetEnd().isBefore(start)) {
				return -1;
			}
			/*
			 * interval.start - between this.start and this.end
			 * or
			 * this.start - between interval.start and interval.end
			 */
			return 0;
		}
		
		/*
		 * returns a Period array of size 3
		 * Period[0] = before overlap/intersection, a subinterval of this
		 * Period[1] = overlap between this and the given interval
		 * Period[2] = after overlap/intersection, a subinterval of the given interval
		 * 
		 * only checks these specific cases:
		 * 
		 * [start      this   		    end]
		 * 			[start 		interval 		end]
		 * 
		 * [start 	   this 	  		end]
		 * 			[start interval end]
		 * 
		 * if this case does not happen, the method will return null
		 * 
		 * if there is no before/after, in their place in the array there will be a null value
		 */
		public Interval[] SubtractAndGetIntersection(Interval interval) {
			if (!((interval.GetStart().isAfter(this.start) || interval.GetStart().isEqual(this.start))
					&& (interval.start.isBefore(this.end) || interval.GetStart().isEqual(this.end)))) {
				return null;
			}
			
			Interval[] intervals = new Interval[3];
			
			// check the before
			if (this.start.isBefore(interval.GetStart())) {
				intervals[0] = new Interval(this.start, interval.GetStart().minusDays(1), this.price);
			} else {
				intervals[0] = null;
			}
			
			// check the overlap/intersection and after
			if (this.end.isBefore(interval.GetEnd())) {
				intervals[1] = new Interval(interval.GetStart(), this.end, this.price);
				intervals[2] = new Interval(this.end.plusDays(1), interval.GetEnd(), this.price);
			} else {
				intervals[1] = new Interval(interval.GetStart(), interval.GetEnd(), this.price);
				intervals[2] = null;
			}
			
			return intervals;
		}
		
		/*
		 * combines the two intervals and updates the overlapping intervals with the given interval's price
		 * 
		 * DOES NOT CHECK IF THE PERIODS DO ACTUALLY OVERLAP
		 * 
		 * 9 overlap cases:
		 *           [start         <interval>         end]
		 * 1.          
		 *      [start		<this>			end]
		 * 2.	
		 * 		[start 		<this>					 end]
		 * 3.     
		 *      [start		<this>							end]
		 * 4.     
		 *   		 [start 	<this>		end]
		 * 5.
		 * 			 [start 	<this>			 	 end]
		 * 6.
		 * 			 [start		<this>						end]  
		 * 7.     
		 *      			[start	<this>	end]
		 * 8.
		 * 			 		[start 	<this>		 	 end]
		 * 9.     
		 *      			[start	<this>					end]
		 * 
		 * Case 1: returns {[this.start    , interval.start - 1;   this.price]
		 * 					[interval.start  , interval.end      ; interval.price]}
		 * 
		 * Case 2: returns {[this.start    , interval.start - 1;   this.price]
		 * 					[interval.start  , interval.end      ; interval.price]}
		 * 
		 * Case 3: returns {[this.start	   , interval.start - 1;   this.price]
		 * 				    [interval.start  , interval.end	     ; interval.price]
		 * 					[interval.end + 1, this.end		 ;   this.price]}
		 * 
		 * Case 4: returns {[interval.start  , interval.end      ; interval.price]}
		 * 
		 * Case 5: returns {[interval.start  , interval.end		 ; interval.price]}
		 * 
		 * Case 6: returns {[interval.start  , interval.end		 ; interval.price]
		 * 					[interval.end + 1, this.end		 ;   this.price]}
		 * 
		 * Case 7: returns {[interval.start  , interval.end      ; interval.price]}
		 * 
		 * Case 8: returns {[interval.start  , interval.end      ; interval.price]}
		 * 
		 * Case 9: returns {[interval.start  , interval.end		 ; interval.price]
		 * 					[interval.end + 1, this.end        ;   this.price]}
		 * 
		 * similar cases: 1, 2; 
		 * 				  4, 5, 7, 8
		 * 				  6, 9
		 * 
		 */
		public Interval[] CreateNewIntervalsFromOverlapsAndUpdatePrice(Interval interval) {
			Interval[] newDates = null;
			if (start.isBefore(interval.GetStart())) {
				if (end.isBefore(interval.GetEnd()) || end.isEqual(interval.GetEnd())) {
					// case 1 & 2
					newDates = new Interval[2];
					newDates[0] = new Interval(start, interval.GetStart().minusDays(1), price);
					newDates[1] = new Interval(interval.GetStart(), interval.GetEnd(), interval.GetPrice());
				} else {
					// case 3
					newDates = new Interval[3];
					newDates[0] = new Interval(start, interval.GetStart().minusDays(1), price);
					newDates[1] = new Interval(interval.GetStart(), interval.GetEnd(), interval.GetPrice());
					newDates[2] = new Interval(interval.GetEnd().plusDays(1), end, price);
				}
			} else if (start.isEqual(interval.GetStart())) {
				if (end.isBefore(interval.GetEnd()) || end.isEqual(interval.GetEnd())) {
					// case 4 & 5
					newDates = new Interval[1];
					newDates[0] = new Interval(interval.GetStart(), interval.GetEnd(), interval.GetPrice());
				} else {
					// case 6
					newDates = new Interval[2];
					newDates[0] = new Interval(interval.GetStart(), interval.GetEnd(), interval.GetPrice());
					newDates[1] = new Interval(interval.GetEnd().plusDays(1), end, price);
				}
			} else {
				if (end.isBefore(interval.GetEnd()) || end.isEqual(interval.GetEnd())) {
					// case 7 & 8
					newDates = new Interval[1];
					newDates[0] = new Interval(interval.GetStart(), interval.GetEnd(), interval.GetPrice());
				} else {
					// case 9
					newDates = new Interval[2];
					newDates[0] = new Interval(interval.GetStart(), interval.GetEnd(), interval.GetPrice());
					newDates[1] = new Interval(interval.GetEnd().plusDays(1), end, price);
				}
			}
			return newDates;
		}
		
		/*
		 * updates overlapping interval with the given interval's price
		 * 
		 * DOES NOT CHECK IF THE INTERVALS DO ACTUALLY OVERLAP
		 * 
		 * 9 overlap cases:
		 *           [start         <interval>         end]
		 * 1.          
		 *      [start		<this>			end]
		 * 2.	
		 * 		[start 		<this>					 end]
		 * 3.     
		 *      [start		<this>							end]
		 * 4.     
		 *   		 [start 	<this>		end]
		 * 5.
		 * 			 [start 	<this>			 	 end]
		 * 6.
		 * 			 [start		<this>						end]  
		 * 7.     
		 *      			[start	<this>	end]
		 * 8.
		 * 			 		[start 	<this>		 	 end]
		 * 9.     
		 *      			[start	<this>					end]
		 * 
		 * Case 1: returns {[this.start    , interval.start - 1;   this.price]
		 * 					[interval.start  , this.end        ; interval.price]}
		 * 
		 * Case 2: returns {[this.start    , interval.start - 1;   this.price]
		 * 					[interval.start  , this.end        ; interval.price]}
		 * 
		 * Case 3: returns {[this.start	   , interval.start - 1;   this.price]
		 * 				    [interval.start  , interval.end	     ; interval.price]
		 * 					[interval.end + 1, this.end		 ;   this.price]}
		 * 
		 * Case 4: returns {[this.start    , this.end        ; interval.price]}
		 * 
		 * Case 5: returns {[this.start    , this.end		 ; interval.price]}
		 * 
		 * Case 6: returns {[this.start    , interval.end		 ; interval.price]
		 * 					[interval.end + 1, this.end		 ;   this.price]}
		 * 
		 * Case 7: returns {[this.start    , this.end        ; interval.price]}
		 * 
		 * Case 8: returns {[this.start    , this.end        ; interval.price]}
		 * 
		 * Case 9: returns {[this.start    , interval.end		 ; interval.price]
		 * 					[interval.end + 1, this.end        ;   this.price]}
		 * 
		 * similar cases: 1, 2; 
		 * 				  4, 5, 7, 8
		 * 				  6, 9
		 * 
		 */
		public Interval[] UpdatePriceForOverlappingInterval(Interval interval) {
			Interval[] newDates = null;
			if (start.isBefore(interval.GetStart())) {
				if (end.isBefore(interval.GetEnd()) || end.isEqual(interval.GetEnd())) {
					// case 1 & 2
					newDates = new Interval[2];
					newDates[0] = new Interval(start, interval.GetStart().minusDays(1), price);
					newDates[1] = new Interval(interval.GetStart(), end, interval.GetPrice());
				} else {
					// case 3
					newDates = new Interval[3];
					newDates[0] = new Interval(start, interval.GetStart().minusDays(1), price);
					newDates[1] = new Interval(interval.GetStart(), interval.GetEnd(), interval.GetPrice());
					newDates[2] = new Interval(interval.GetEnd().plusDays(1), end, price);
				}
			} else if (start.isEqual(interval.GetStart())) {
				if (end.isBefore(interval.GetEnd()) || end.isEqual(interval.GetEnd())) {
					// case 4 & 5
					newDates = new Interval[1];
					newDates[0] = new Interval(start, end, interval.GetPrice());
				} else {
					// case 6
					newDates = new Interval[2];
					newDates[0] = new Interval(start, interval.GetEnd(), interval.GetPrice());
					newDates[1] = new Interval(interval.GetEnd().plusDays(1), end, price);
				}
			} else {
				if (end.isBefore(interval.GetEnd()) || end.isEqual(interval.GetEnd())) {
					// case 7 & 8
					newDates = new Interval[1];
					newDates[0] = new Interval(start, end, interval.GetPrice());
				} else {
					// case 9
					newDates = new Interval[2];
					newDates[0] = new Interval(start, interval.GetEnd(), interval.GetPrice());
					newDates[1] = new Interval(interval.GetEnd().plusDays(1), end, price);
				}
			}
			return newDates;
		}
		
		public LocalDate GetStart() {
			return start;
		}
		
		public LocalDate GetEnd() {
			return end;
		}
		
		public int GetPrice() {
			return price;
		}
	}
	
	private class Sport {
		private String name;
		private HashMap<String, Country> countries;
		
		public Sport(String sportName) {
			this.name = sportName;
			countries = new HashMap<>();
		}
		
		public Country GetCountry(String countryName) {
			if (!countries.containsKey(countryName)) {
				return null;
			} else {
				return countries.get(countryName);
			}
		}
		
		public LinkedList<Country> GetCountries() {
			Iterator<Entry<String, Country>> it = countries.entrySet().iterator();
			LinkedList<Country> countryList = new LinkedList<>();
			while (it.hasNext()) {
				countryList.add(it.next().getValue());
			}
			return countryList;
		}
		
		public boolean CheckValid() {
			// TODO;
			return true;
		}
		
		public boolean isEmpty() {
			return countries.isEmpty();
		}
		
		public boolean AddCountry(Country country) {
			if (country.CheckValid()) {
				countries.put(country.name, country);
				return true;
			}
			return false;
		}
		
		public boolean RemoveCountry(String countryName) {
			if (countries.containsKey(countryName)) {
				countries.remove(countryName);
				return true;
			}
			return false;
		}
		
		public String GetName() {
			return name;
		}
		
		public String toString() {
			String str = name + "\n";
			Iterator<Entry<String, Country>> it = countries.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, Country> entry = it.next();
				str += entry.getValue().toString() + "\n";
			}
			return str;
		}
	}

	HashMap<String, Sport> sports;
	
	public Hierarchy() {
		sports = new HashMap<>();
	}
	
	private Sport GetSport(String sportName) {
		if (sports.containsKey(sportName)) {
			return sports.get(sportName);
		}
		return null;
	}
	
	private boolean RemoveSport(String sportName) {
		if (sports.containsKey(sportName)) {
			sports.remove(sportName);
			return true;
		}
		return false;
	}
	
	private boolean AddSport(Sport sport) {
		if (sports.containsKey(sport.GetName()) || !sport.CheckValid()) {
			return false;
		}
		sports.put(sport.GetName(), sport);
		return true;
	}
	
	public int Add(String countryName, String regionName, String cityName, String sportName, LocalDate start, LocalDate end, int price) {
		if (price < 0) {
			return ResponseType.PRICE_INV;
		}

		Interval interval = new Interval(start, end, price);
		Sport sport = GetSport(sportName);

		if (sport != null) {
			Country country = sport.GetCountry(countryName);
			if (country != null) {
				Region region = country.GetRegion(regionName);
				if (region != null) {
					City city = region.GetCity(cityName);
					if (city != null) {
						if (city.AddToPeriods(interval) == ResponseType.PERIOD_OVERLAP) {
							city.AddToIntervalsAndUpdate(interval);
							return ResponseType.PERIOD_OVERLAP_OK;
						}
						return ResponseType.OP_SUC;
					} else {
						city = new City(cityName, region);
						city.AddToPeriods(interval);
						region.AddCity(city);
						
						return ResponseType.OP_SUC;
					}
				} else {
					region = new Region(regionName, country);
					City city = new City(cityName, region);
					
					city.AddToPeriods(interval);
					region.AddCity(city);
					country.AddRegion(region);
					
					return ResponseType.OP_SUC;
				}
			} else {
				country = new Country(countryName, sportName);
				Region region = new Region(regionName, country);
				City city = new City(cityName, region);
				
				city.AddToPeriods(interval);
				region.AddCity(city);
				country.AddRegion(region);
				
				sport = new Sport(sportName);
				sport.AddCountry(country);
				
				return ResponseType.OP_SUC;
			}
		} else {
			Country country = new Country(countryName, sportName);
			Region region = new Region(regionName, country);
			City city = new City(cityName, region);
			
			city.AddToPeriods(interval);
			region.AddCity(city);
			country.AddRegion(region);
			
			sport = new Sport(sportName);
			sport.AddCountry(country);
			
			AddSport(sport);
			
			return ResponseType.OP_SUC;
		}
	}
	
	/*
	 * format: country/region/city/sport=interval;...;interval/.../sport=interval;...;interval
	 * / = ExtremeServer.DELIM
	 * = = ExtremeServer.DELIM_LIST
	 * ; = ExtremeServer.DELIM_SERIAL 
	 */
	public String Get(String countryName, String regionName, String cityName) {
		String str = "";
		boolean none = true;
		Iterator<Entry<String, Sport>> it = sports.entrySet().iterator();
		
		Country country;
		Region region;
		City city;
		
		while (it.hasNext()) {
			Sport sport = it.next().getValue();
			country = sport.GetCountry(countryName);
			if (country != null) {
				region = country.GetRegion(regionName);
				if (region != null) {
					city = region.GetCity(cityName);
					if (city != null) {
						none = false;
						str += ExtremeServer.DELIM + sport.GetName() + ExtremeServer.DELIM_LIST + city.GetPeriodsString();
					}
				}
			}
		}

		if (none) {
			str = Integer.toString(ResponseType.LOCATION_INV);
		} else {
			str = countryName + ExtremeServer.DELIM + regionName + ExtremeServer.DELIM + cityName + str;
		}
		return str;
	}
	
	/*
	 * removes a sport from a city
	 */
	public int Remove(String countryName, String regionName, String cityName, String sportName) {
		Sport sport = GetSport(sportName);
		
		if (sport != null) {
			Country country = sport.GetCountry(countryName);
			if (country != null) {
				Region region = country.GetRegion(regionName);
				if (region != null) {
					City city = region.GetCity(cityName);
					if (city != null) {
						region.RemoveCity(cityName);
						if (region.isEmpty()) {
							country.RemoveRegion(regionName);
							if (country.isEmpty()) {
								sport.RemoveCountry(countryName);
								if (sport.isEmpty()) {
									RemoveSport(sportName);
								}
							}
						}
						return ResponseType.OP_SUC;
					} else {
						return ResponseType.CITY_INV;
					}
				} else {
					return ResponseType.REGION_INV;
				}
			} else {
				return ResponseType.COUNTRY_INV;
			}
		} else {
			return ResponseType.SPORT_INV;
		}
	}
	
	/*
	 * update either:
	 * - price of sport for earliest date to endStr - startStr = ExtremeServer.SameData, endStr = date, price is positive
	 * - price of sport for startStr to latest date - startStr = date, endStr = ExtremeServer.Limit, price is positive
	 * - price of sport for earliest date to latest date - startStr & endStr = ExtremeServer.SameDate,  price is positive
	 * - price of sport for overlapping intervals     - startStr & endStr = dates, price is positive
	 */
	public int Update(String countryName, String regionName, String cityName, String sportName, int price, String startStr, String endStr) {
		Sport sport = GetSport(sportName);
		
		if (sport != null) {
			Country country = sport.GetCountry(countryName);
			if (country != null) {
				Region region = country.GetRegion(regionName);
				if (region != null) {
					City city = region.GetCity(cityName);
					if (city != null) {
						if (price < 0) {
							return ResponseType.PRICE_INV;
						}
						LocalDate start, end;
						if (startStr.equals(OperationTypeAdmin.EXTR)) {
							start = city.GetEarliest();
						} else {
							try {
								start = LocalDate.parse(startStr);
							} catch (DateTimeParseException e) {
								return ResponseType.PERIOD_FORMAT_ERR;
							}
						}
						
						if (endStr.equals(OperationTypeAdmin.EXTR)) {
							end = city.GetLatest();
						} else {
							try {
								end = LocalDate.parse(endStr);
							} catch (DateTimeParseException e) {
								return ResponseType.PERIOD_FORMAT_ERR;
							}
						}
						return city.UpdatePeriods(new Interval(start, end, price));
					} else {
						return ResponseType.CITY_INV;
					}
				} else {
					return ResponseType.REGION_INV;
				}
			} else {
				return ResponseType.COUNTRY_INV;
			}
		} else {
			return ResponseType.SPORT_INV;
		}
	}
	
	/*
	 * used only in the next function
	 */
	class Element implements Comparable<Element> {
		public int price;
		private String country;
		private String region;
		private String city;
		
		public Element(int price, String country, String region, String city) {
			this.price = price;
			this.country = country;
			this.region = region;
			this.city = city;
		}

		@Override
		public int compareTo(Element e) {
			if (this.price > e.price) {
				return 1;
			}
			if (this.price < e.price) {
				return -1;
			}
			return 0;
		}
		
		public String toString() {
			return new String(country + ExtremeServer.DELIM
					+ region + ExtremeServer.DELIM 
					+ city + ExtremeServer.DELIM + price);
		}
	}
	
	/*
	 * gets every city in which this sport can be practiced only if the given interval
	 * completely overlaps the intervals in which the sport is practiced
	 */
	public String GetAndSort(String sportName, LocalDate start, LocalDate end) {
		String str = "";
		boolean changed = false;
		PriorityQueue<Element> pq = new PriorityQueue<>();
		Sport sport = GetSport(sportName);
		Interval interval = new Interval(start, end, 0);
		
		if (sport != null) {
			LinkedList<Country> countries = sport.GetCountries();
			Iterator<Country> co = countries.listIterator();
			
			while (co.hasNext()) {
				Country country = co.next();
				LinkedList<Region> regions = country.GetRegions();
				Iterator<Region> re = regions.listIterator();
				
				while (re.hasNext()) {
					Region region = re.next();
					LinkedList<City> cities = region.GetCities();
					Iterator<City> ci = cities.listIterator();
					
					while (ci.hasNext()) {
						City city = ci.next();
						LinkedList<Interval> intervals = city.FindExactOverlap(interval);
						
						if (intervals != null && !intervals.isEmpty()) {
							changed = true;
							int price = 0;
							Iterator<Interval> in = intervals.iterator();

							while (in.hasNext()) {
								Interval currentInterval = in.next();
								price += 
										(1 + Duration.between(currentInterval.GetStart().atStartOfDay(),
												currentInterval.GetEnd().atStartOfDay()).toDays())
										* currentInterval.GetPrice();
							}
							
							pq.add(new Element(price, country.name, region.name, city.name));
						}
					}
				}
			}
		}
		
		while (!pq.isEmpty()) {
			Element e = pq.poll();
			str += e.toString();
			if (!pq.isEmpty()) {
				str += ExtremeServer.DELIM_SERIAL;
			}
		}
		
		if (!changed) {
			return ResponseType.EMPTY_STR;
		}
		return str;
	}

	public String toString() {
		String str = "";
		Iterator<Entry<String, Sport>> it = sports.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Sport> entry = it.next();
			str += entry.getValue().toString();
		}
		return str;
	}
}
