

/**
 * 
 */
package com.bitBusy.goingg.events;

import java.util.ArrayList;
import java.util.HashMap;

import android.location.Location;

import com.bitBusy.goingg.fromLocation.FromLocation;
import com.bitBusy.goingg.webServices.EventsData.DataExtractor;
import com.google.android.gms.maps.model.LatLng;

/**
 * @author SumaHarsha
 *
 */
public final class DataSources {


	public static enum Categories
	{
		Academic,
		Business,
		Community,
		Entertainment,
		Family,
		Movies,
		Pets,
		Recreation,
		Sports,
		Other;
	}
	
	public static final String AITEMNAME = "itemName()";
	public static final String AWSSELECTSTATEMENT = "Select * from ";
	//+ DataSources.WHERE + " ";
				//+ " CHAR_LENGTH("+AITEMNAME + ") = \"1024\" and "; 
	public static final String AENDPNT = "sdb.us-west-1.amazonaws.com";
	public static final String ANAME = "eventName";
	public static final String ADESC = "eventDescription";
	public static final String ASTART = "eventStart";
	public static final String AEND = "eventEnd";
	public static final String ALINK = "eventInfoLink";
	public static final String AGPLACE = "eventIsGPlace";
	public static final String ALAT = "eventLatitude";
	public static final String ALNG = "eventLongitude";
	public static final String APRICE = "eventPrice";
	public static final String AIMG = "eventImage";
	public static final String ACREATEDBY = "createdBy";
	public static final String ACREATEDON = "createdOn";
	public static final String ASTREETADD = "eventStreetAddress";
	public static final String ACITYADD = "eventCityAddress";
	public static final String ASTATEADD = "eventStateAddress";
	public static final String ACOUNTRYADD = "eventCountryAddress";
	public static final String AZIPADD = "eventZipAddress";
	public static final String ASPAM1 = "eventSpamReporter1";
	public static final String ASPAM2 = "eventSpamReporter2";
	public static final String ASPAM3 = "eventSpamReporter3";
	public static final String ASPAM4 = "eventSpamReporter4";
	public static final String ASPAM5 = "eventSpamReporter5";
	public static final String ASPAM6 = "timetodelete";
	public static final String ATIME = "Time";
	public static final String AUSERSTATS = "UserStats";
	public static final String ASPLCHAR = "SplChar";
	public static final String AEVENTSSUBMITTED = "eventsSubmitted";
	public static final String ASPAMGENERATED = "spamGenerated";
	public static final String ASPAMREPORTED = "spamReported";
	public static final String ANUMGOING = "numGoing";
	public static final String ANUMNOPE = "numNope";
	public static final String ANUMMAYBE = "numMaybe";
	public static final String SOURCE = "Source";
	public static final String AND = "&";
	public static final String EQUALS = "=";
	public static final String COMMA = ",";
	public static final String WHERE="where";
	public static final String WITHIN = "within";
	public static final String DATE = "date";
	public static final String NULL = "null";
	public static final String DOLLAR = "$";
	public static final String APOSTROPHE = "'";
	public static final String QUESTIONMARK = "?";
	public static final String GREATER = ">";
	public static final String LESSER = "<";
	public static final String AUSER = "User";
	public static final String AULCK = "Thaala";
	public static final String AUNAME = "Username";
	public static final String AFRIENDNAME = "FriendName";
	public static final String ASECKEYWORD = "SecurityWord";
	public static final String AFNAME = "FirstName";
	public static final String ALNAME = "LastName";
	public static final String ACITY = "City";
	private static final String FOOD = "food";
	public static final String AUID = "uuid";
	public static final String AGID = "gid";

	//Google Places
	public static final String GOOGLEPLACEPRIMARY = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=AIzaSyBy3Hq5Or7FYHByGwstHqlvxUJE2vz_MVc" +
			"&sensor=true&types=";
	public static final String GOOGLEPLACEPAGETOKEN = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=AIzaSyBy3Hq5Or7FYHByGwstHqlvxUJE2vz_MVc" +
			"&sensor=true&pagetoken=";

	private static final String AMUSEMENTPARK = "amusement_park";
	private static final String AQUARIUM = "aquarium";
	private static final String ARTGALLERY = "art_gallery";
	private static final String BAR = "bar";
	private static final String BICYCLESTORE = "bicycle_store";
	private static final String BOOKSTORE = "book_store";
	private static final String BOWLINGALLEY = "bowling_alley";
	private static final String CAFE = "cafe";
	private static final String CAMPGROUND = "campground";
	private static final String CARDEALER = "car_dealer";
	private static final String CARRENTAL = "car_rental";
	private static final String CASINO = "casino";
	private static final String CHURCH = "church";
	private static final String CLOTHINGSTORE = "clothing_store";
	private static final String CONVENIENCESTORE = "convenience_store";
	private static final String DEPARTMENT_STORE = "department_store";
	private static final String ELECTRICIAN_STORE = "electrician_store";
	private static final String FLORIST = "florist";
	//private static final String FOODPLACE = "food";
	private static final String FURNITURE_STORE = "furniture_store";
	private static final String GROCERYSUPERMARKET = "grocery_or_supermarket";
	private static final String HARDWARESTORE = "hardware_store";
	private static final String HINDUTEMPLE = "hindu_temple";
	private static final String HOMEGOODSSTORE = "home_goods_store";
	private static final String JEWELERYSTORE = "jewelry_store";
	private static final String LIBRARY = "library";
	private static final String LIQUORSTORE = "liquor_store";
	private static final String MEALDELIVERY = "meal_delivery";
	private static final String MEALTAKEAWAY = "meal_takeaway";
	private static final String MOSQUE = "mosque";
	private static final String MOVIERENTAL = "movie_rental";
	private static final String MOVIETHEATER = "movie_theater";
	private static final String MUSEUM = "museum";
	private static final String NIGHTCLUB = "night_club";
	private static final String PARK = "park";
	private static final String PETSTORE = "pet_store";
	private static final String PLACEOFWORSHIP = "place_of_worship";
	private static final String RESTAURANT = "restaurant";
	private static final String SHOESTORE = "shoe_store";
	private static final String SHOPPINGMALL = "shopping_mall";
	private static final String STADIUM = "stadium";
	private static final String STORE = "store";
	private static final String SYNAGOGUE = "synagogue";
	private static final String VETCARE = "veterinary_care";
	private static final String ZOO = "zoo";
	public static final String[] GOOGLEPLACECATEGORIES = new String[]{AMUSEMENTPARK,AQUARIUM,ARTGALLERY,BAR,BICYCLESTORE,BOOKSTORE,BOWLINGALLEY,
		CAFE,CAMPGROUND,CARDEALER,CARRENTAL,CASINO,CHURCH,CLOTHINGSTORE,CONVENIENCESTORE,DEPARTMENT_STORE,ELECTRICIAN_STORE,FLORIST,FOOD,FURNITURE_STORE,
		GROCERYSUPERMARKET,HARDWARESTORE,HINDUTEMPLE,HOMEGOODSSTORE,JEWELERYSTORE,LIBRARY,LIQUORSTORE,MEALDELIVERY,MEALTAKEAWAY,MOSQUE,MOVIERENTAL,
		MOVIETHEATER,MUSEUM,NIGHTCLUB,PARK,PETSTORE,PLACEOFWORSHIP,RESTAURANT,SHOESTORE,SHOPPINGMALL,STADIUM,STORE,SYNAGOGUE,VETCARE,ZOO};
	// SCD
	public static final String SEATTLELINKS = "https://data.seattle.gov/resource";
	public static final String STARTTIME = "start_time";	
	public static final String ENDTIME = "end_time";
	private static final String SEATTLECITYBUSINESS= "https://data.seattle.gov/resource/8ia9-etqi.json";
	private static final String SEATTLECITYARTS = "https://data.seattle.gov/resource/r9tf-vftw.json";
	private static final String SEATTLECITYCOMMUNITY = "https://data.seattle.gov/resource/mmhs-cjev.json";
	private static final String SEATTLECITYCOMMSUBMITTED = "https://data.seattle.gov/resource/t5mx-wchj.json";
	private static final String SEATTLECITYSPONSORED = "https://data.seattle.gov/resource/7m6y-k25v.json";
	private static final String SEATTLEPUBOUTREACH = "https://data.seattle.gov/resource/8pec-7ugc.json";
	private static final String SEATTLEOTHER = "https://data.seattle.gov/resource/kjgy-var8.json";
	private static final String SEATTLECITYRECREATION = "https://data.seattle.gov/resource/pa7z-i6ib.json";
	private static final String SEATTLECITY = "https://data.seattle.gov/resource/cprz-jsz8.json";
	private static final String SEATTLESPECIAL = "https://data.seattle.gov/resource/fva2-7c82.json";
	private static final String SEATTLECIVIL = "https://data.seattle.gov/resource/tta6-nr36.json";
	private static final String SEATTLEEDUCATION = "https://data.seattle.gov/resource/adee-kbwa.json";
	

	private static final String MUSIC = "music";
	private static final String FUNDRAISERS = "fundraisers";
	private static final String SALES = "sales";
	private static final String SPORTS = "sports";
	private static final String OTHER = "other";
	
	//EVENTBRITE7
	public static final String EVENTBRITEPRIMARYLINK = "https://www.eventbrite.com/json/event_search?app_key=TRFHXGPL3E7ZJ6IOPS&max=100" +
			"&sort_by=id&category=";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String SPACE = "u0020";
	private static final String CONFERENCES = "conferences";
	private static final String CONVENTIONS = "conventions";
	private static final String ENTERTAINMENT = "entertainment";
	private static final String MEETINGS = "meetings";
	private static final String PERFORMANCES = "performances";
	private static final String REUNIONS = "reunions";
	private static final String SEMINARS = "seminars";
	private static final String SOCIAL = "social";
	private static final String TRADESHOWS = "tradeshows";
	private static final String TRAVEL = "travel";
	private static final String RELIGION = "religion";
	private static final String FAIRS = "fairs";
	private static final String RECREATION = "recreation";
	public static final String[] EVENTBRITECATEGORIES = new String[] {OTHER, SPORTS,ENTERTAINMENT, PERFORMANCES,TRAVEL, TRADESHOWS, FAIRS, SOCIAL, RECREATION, MUSIC, FOOD, CONVENTIONS, FUNDRAISERS, 
		MEETINGS, SALES, SEMINARS, CONFERENCES,REUNIONS, RELIGION};
	
	//GATEKRASH
	//private static final String GATEKRASHPRIMARYLINK = "http://gatekrash.com/api/v1?t=geolocate";
	//private static final String LAT="lat";
	//private static final String LNG = "lon";
	
	//SKIDDLE
/*	private static final String SKIDDLEPRIMARYLINK = "http://www.skiddle.com/api/v1/events?api_key=14ee38395215e2d52d9e140f6a3e9482";
	private static final String FEST = "FEST";
	private static final String LIVEMUSIC = "LIVE";
	private static final String CLUB = "CLUB";
	private static final String THEATRE = "Theatre";
	private static final String EXHIBITION = "EXHIB";
	private static final String FAMILYKIDSEVENT = "KIDS";
	private static final String BARPUB = "BARPUB";
	private static final String LGB = "LGB";
	private static final String SPORT = "SPORT";
	private static final String ARTS = "ARTS";*/
	
	//active
	//private static final String ACTIVEPRIMARYLINK = ""
	
	//City of Madison
	//public static final String CITYOFMADISON = "https://data.cityofmadison.com/Events/City-Calendar-of-Events/gxhk-44q9";
	
	//Seattle geek
//	private static final String SEATGEEK = "http://api.seatgeek.com/2/events";
	
	//king county
	//private static final String KINGCOUNTY = "https://data.kingcounty.gov/Operations/King-County-master-calendar/rnmi-uwsb";
	
	//king county pet
	//private static final String KINGCOUNTYPET = "https://data.kingcounty.gov/dataset/Pet-Events/3rpu-sz4q";
	
	private static final LatLng SEATTLELATLNG = new LatLng(47.609722, -122.333056);

	//private static final LatLng MADISONLATLNG = new LatLng(47.609722, -122.333056);

	
	private static ArrayList<HashMap<Object, ArrayList<String>>> myMapList;
	
	private static HashMap<Categories, ArrayList<String>> myCategorySourcecategoryMap;
	
	
	public static ArrayList<HashMap<Object, ArrayList<String>>> getCategorySourceCategoryMaps(int the_distance)
	{
		 initializeMapList(the_distance);
		 return myMapList;
	}
	
	public static HashMap<Categories, ArrayList<String>> getCategorySubcategoryMapping()
	{
		return myCategorySourcecategoryMap;
	}
	private static void initializeMapList(int the_distance)
	{
		myMapList = new ArrayList<HashMap<Object, ArrayList<String>>>();
		myCategorySourcecategoryMap = new HashMap<Categories, ArrayList<String>>();
		myMapList.add(getEventbriteMap());
		myMapList.add(getGooglePlacesMap());
		
		LatLng current = FromLocation.getInstance().getFromLoc();
		if (current != null)
		{
			if (computeDistance(current.latitude, current.longitude, SEATTLELATLNG.latitude, SEATTLELATLNG.longitude) <= (2* the_distance))
			{
				myMapList.add(getSeattleMap());
				//myMapList.add(getKingCountyMap());
			}
		/*	if (computeDistance(current.latitude, current.longitude, MADISONLATLNG.latitude, MADISONLATLNG.longitude) <= (2* the_distance))
			{
				myMapList.add(getMadisonMap());
			}*/
		}
				
	}
	
	/**
	 * @return
	 */
/*	private static HashMap<Object, ArrayList<String>> getMadisonMap() {
		HashMap<Object, ArrayList<String>> map = new HashMap<Object, ArrayList<String>>();
		ArrayList<String> list = new ArrayList<String>();
		list.add(CITYOFMADISON);
		map.put(SOURCE, list);
		addMadisonCommunity(map);
		return map;
	}*/

	/**
	 * @param map
	 */
/*	private static void addMadisonCommunity(HashMap<Object, ArrayList<String>> the_map) {
			ArrayList<String> the_list = new  ArrayList<String>();
			the_list.add(CITYOFMADISON);
			the_map.put(Categories.Community, the_list);	
			addToCategorySubcategoryMapping(Categories.Community, the_list);										
		}*/

	/**
	 * @return
	 */
	private static HashMap<Object, ArrayList<String>> getSeattleMap() {
		HashMap<Object, ArrayList<String>> map = new HashMap<Object, ArrayList<String>>();
		ArrayList<String> list = new ArrayList<String>();
		list.add(SEATTLELINKS);
		map.put(SOURCE, list);
		addSeattleAcademic(map);
		addSeattleBusiness(map);
		addSeattleCommunity(map);
		addSeattleRecreation(map);
		addSeattleOthers(map); 
		return map;
	}

	/**
	 * @param map
	 */
	private static void addSeattleOthers(HashMap<Object, ArrayList<String>> the_map) {
		ArrayList<String> the_list = new  ArrayList<String>();
		the_list.add(SEATTLEOTHER);
		the_map.put(Categories.Other, the_list);	
		addToCategorySubcategoryMapping(Categories.Other, the_list);												
	}

	/**
	 * @param map
	 */
	private static void addSeattleRecreation(
			HashMap<Object, ArrayList<String>> the_map) {
		ArrayList<String> the_list = new  ArrayList<String>();
		the_list.add(SEATTLECITYRECREATION);
		the_map.put(Categories.Recreation, the_list);	
		addToCategorySubcategoryMapping(Categories.Recreation, the_list);										
	}

	/**
	 * @param map
	 */
	private static void addSeattleCommunity(
			HashMap<Object, ArrayList<String>> the_map) {
		ArrayList<String> the_list = new  ArrayList<String>();
		the_list.add(SEATTLECITYCOMMUNITY);
		the_list.add(SEATTLECITYCOMMSUBMITTED);
		the_list.add(SEATTLECITYSPONSORED);
		the_list.add(SEATTLEPUBOUTREACH);
		the_list.add(SEATTLECITY);
		the_list.add(SEATTLESPECIAL);
		the_list.add(SEATTLECIVIL);
		the_map.put(Categories.Community, the_list);	
		addToCategorySubcategoryMapping(Categories.Community, the_list);										
	}

	/**
	 * @param map
	 */
	private static void addSeattleBusiness(
			HashMap<Object, ArrayList<String>> the_map) {
		ArrayList<String> the_list = new  ArrayList<String>();
		the_list.add(SEATTLECITYBUSINESS);
		the_map.put(Categories.Business, the_list);	
		addToCategorySubcategoryMapping(Categories.Business, the_list);								
	}

	/**
	 * @param map
	 */
	private static void addSeattleAcademic(
			HashMap<Object, ArrayList<String>> the_map) {
			ArrayList<String> the_list = new  ArrayList<String>();
			the_list.add(SEATTLECITYARTS);
			the_list.add(SEATTLEEDUCATION);
			the_map.put(Categories.Academic, the_list);	
			addToCategorySubcategoryMapping(Categories.Academic, the_list);						
	}

	/**
	 * @return
	 */
	private static HashMap<Object, ArrayList<String>> getGooglePlacesMap() {
		HashMap<Object, ArrayList<String>> map = new HashMap<Object, ArrayList<String>>();
		ArrayList<String> list = new ArrayList<String>();
		list.add(GOOGLEPLACEPRIMARY);
		map.put(SOURCE, list);
		addGooglePlaceAcademic(map);
		addGooglePlaceBusiness(map);
		addGooglePlaceCommunity(map);
		addGooglePlaceEntertainment(map);
		addGooglePlaceFamily(map);
		addGooglePlaceMovies(map);
		addGooglePlacePets(map);
		addGooglePlaceRecreation(map);
		addGooglePlaceSports(map);
	//	addGooglePlaceOthers(map); */
		return map;
	}

	/**
	 * @param map
	 */
	private static void addGooglePlaceSports(
			HashMap<Object, ArrayList<String>> the_map) {
		ArrayList<String> the_list = new  ArrayList<String>();
		the_list.add(BOWLINGALLEY);
		the_list.add(STADIUM);
		the_map.put(Categories.Sports, the_list);	
		addToCategorySubcategoryMapping(Categories.Sports, the_list);				
	}

	/**
	 * @param map
	 */
	private static void addGooglePlaceRecreation(
			HashMap<Object, ArrayList<String>> the_map) {
		ArrayList<String> the_list = new  ArrayList<String>();
		the_list.add(AMUSEMENTPARK);
		the_list.add(BOWLINGALLEY);
		the_list.add(PARK);
		the_list.add(CAMPGROUND);
		the_list.add(STADIUM);
		the_map.put(Categories.Recreation, the_list);	
		addToCategorySubcategoryMapping(Categories.Recreation, the_list);					
	}

	/**
	 * @param map
	 */
	private static void addGooglePlacePets(
			HashMap<Object, ArrayList<String>> the_map) {
		ArrayList<String> the_list = new  ArrayList<String>();
		the_list.add(PETSTORE);
		the_list.add(VETCARE);
		the_map.put(Categories.Pets, the_list);
		addToCategorySubcategoryMapping(Categories.Pets, the_list);						
		
	}

	/**
	 * @param map
	 */
	private static void addGooglePlaceMovies(
			HashMap<Object, ArrayList<String>> the_map) {
		ArrayList<String> the_list = new  ArrayList<String>();
		the_list.add(MOVIERENTAL);
		the_list.add(MOVIETHEATER);
		the_map.put(Categories.Movies, the_list);
		addToCategorySubcategoryMapping(Categories.Movies, the_list);								
	}

	/**
	 * @param map
	 */
	private static void addGooglePlaceFamily(
			HashMap<Object, ArrayList<String>> the_map) {
		ArrayList<String> the_list = new  ArrayList<String>();
		the_list.add(AMUSEMENTPARK);
		the_list.add(AQUARIUM);
		the_list.add(PARK);
		the_list.add(ZOO);
		the_map.put(Categories.Family, the_list);
		addToCategorySubcategoryMapping(Categories.Family, the_list);						
	}

	/**
	 * @param map
	 */
	private static void addGooglePlaceEntertainment(
			HashMap<Object, ArrayList<String>> the_map) {
		ArrayList<String> the_list = new  ArrayList<String>();
		the_list.add(AMUSEMENTPARK);
		the_list.add(AQUARIUM);
		the_list.add(BOWLINGALLEY);
		the_list.add(BAR);
		the_list.add(CAFE);
		the_list.add(CASINO);
		the_list.add(FOOD);		
		the_list.add(MEALDELIVERY);
		the_list.add(MEALTAKEAWAY);
		the_list.add(MOVIERENTAL);
		the_list.add(MOVIETHEATER);
		the_list.add(NIGHTCLUB);
		the_list.add(PARK);
		the_list.add(RESTAURANT);
		the_list.add(SHOPPINGMALL);
		the_list.add(STADIUM);
		the_list.add(ZOO);
		the_map.put(Categories.Entertainment, the_list);	
		addToCategorySubcategoryMapping(Categories.Entertainment, the_list);			
	}

	/**
	 * @param map
	 */
	private static void addGooglePlaceCommunity(
			HashMap<Object, ArrayList<String>> the_map) {
		ArrayList<String> the_list = new  ArrayList<String>();
		the_list.add(CHURCH);
		the_list.add(HINDUTEMPLE);
		the_list.add(PLACEOFWORSHIP);
		the_list.add(MOSQUE);
		the_list.add(SYNAGOGUE);
		the_map.put(Categories.Community, the_list);	
		addToCategorySubcategoryMapping(Categories.Community, the_list);	
	}

	/**
	 * @param map
	 */
	private static void addGooglePlaceBusiness(
			HashMap<Object, ArrayList<String>> the_map) {
		 ArrayList<String> the_list = new  ArrayList<String>();
			the_list.add(BICYCLESTORE);
			the_list.add(BOOKSTORE);
			the_list.add(CARDEALER);
			the_list.add(CARRENTAL);
			the_list.add(CLOTHINGSTORE);
			the_list.add(CONVENIENCESTORE);
			the_list.add(DEPARTMENT_STORE);
			the_list.add(ELECTRICIAN_STORE);
			the_list.add(FLORIST);
			the_list.add(FURNITURE_STORE);
			the_list.add(GROCERYSUPERMARKET);
			the_list.add(HARDWARESTORE);
			the_list.add(HOMEGOODSSTORE);
			the_list.add(JEWELERYSTORE);
			the_list.add(LIQUORSTORE);
			the_list.add(PETSTORE);
			the_list.add(SHOESTORE);
			the_list.add(SHOPPINGMALL);
			the_list.add(STORE);
			the_map.put(Categories.Business, the_list);	
			addToCategorySubcategoryMapping(Categories.Business, the_list);	
	}

	/**
	 * @param map
	 */
	private static void addGooglePlaceAcademic(
			HashMap<Object, ArrayList<String>> the_map) {
			 ArrayList<String> the_list = new  ArrayList<String>();
				the_list.add(AQUARIUM);
				the_list.add(ARTGALLERY);
				the_list.add(BOOKSTORE);
				the_list.add(LIBRARY);
				the_list.add(MUSEUM);
				the_list.add(ZOO);
				the_map.put(Categories.Academic, the_list);	
				addToCategorySubcategoryMapping(Categories.Academic, the_list);		
			}

	/**
	 * @return
	 */
	private static HashMap<Object, ArrayList<String>> getEventbriteMap() {
		HashMap<Object, ArrayList<String>> map = new HashMap<Object, ArrayList<String>>();
		ArrayList<String> list = new ArrayList<String>();
		list.add(EVENTBRITEPRIMARYLINK);
		map.put(SOURCE, list);
		addEventbriteAcademic(map);
		addEventbriteBusiness(map);
		addEventbriteCommunity(map);
		addEventbriteEntertainment(map);
		addEventbriteFamily(map);
		//addEventbriteMovies(map);
		//addEventbritePets(map);
		addEventbriteRecreation(map);
		addEventbriteSports(map);
		addEventbriteOthers(map);
		return map;
	}

	

	/**
	 * @param map
	 */
	private static void addEventbriteOthers(
			HashMap<Object, ArrayList<String>> the_map) {
		ArrayList<String> the_list = new  ArrayList<String>();
		the_list.add(OTHER);
		the_map.put(Categories.Other, the_list);	
		addToCategorySubcategoryMapping(Categories.Other, the_list);		
	}

	/**
	 * @param map
	 */
	private static void addEventbriteSports(
			HashMap<Object, ArrayList<String>> the_map) {
		ArrayList<String> the_list = new  ArrayList<String>();
		the_list.add(SPORTS);
		the_map.put(Categories.Sports, the_list);	
		addToCategorySubcategoryMapping(Categories.Sports, the_list);
		
	}
	/**
	 * @param map
	 */
	private static void addEventbriteRecreation(
			HashMap<Object, ArrayList<String>> the_map) {
		ArrayList<String> the_list = new  ArrayList<String>();
		the_list.add(ENTERTAINMENT);
		the_list.add(PERFORMANCES);
		the_list.add(TRAVEL);
		the_list.add(TRADESHOWS);
		the_list.add(FAIRS);
		the_list.add(SOCIAL);
		the_list.add(RECREATION);
		the_map.put(Categories.Recreation, the_list);	
		addToCategorySubcategoryMapping(Categories.Recreation, the_list);		
	}

	/**
	 * @param map
	 */
	private static void addEventbriteFamily(
			HashMap<Object, ArrayList<String>> the_map) {
		ArrayList<String> the_list = new  ArrayList<String>();
		the_list.add(FAIRS);
		the_list.add(SPORTS);
		the_list.add(RECREATION);
		the_map.put(Categories.Family, the_list);
		addToCategorySubcategoryMapping(Categories.Family, the_list);
			
	}

	/**
	 * @param map
	 */
	private static void addEventbriteEntertainment(
			HashMap<Object, ArrayList<String>> the_map) {
		ArrayList<String> the_list = new ArrayList<String>();
		the_list.add(MUSIC);
		the_list.add(ENTERTAINMENT);
		the_list.add(PERFORMANCES);
		the_list.add(FAIRS);
		the_list.add(FOOD);
		the_list.add(SOCIAL);		
		the_map.put(Categories.Entertainment, the_list);
		addToCategorySubcategoryMapping(Categories.Entertainment, the_list);		
	}

	/**
	 * @param map
	 */
	private static void addEventbriteCommunity(
			HashMap<Object, ArrayList<String>> the_map) {
		ArrayList<String> the_list = new ArrayList<String>();
		the_list.add(CONVENTIONS);
		the_list.add(FAIRS);
		the_list.add(FUNDRAISERS);
		the_list.add(SOCIAL);		
		the_list.add(MEETINGS);
		the_list.add(SALES);
		the_list.add(SEMINARS);
		the_map.put(Categories.Community, the_list);
		addToCategorySubcategoryMapping(Categories.Community, the_list);
	}
	
	/**
	 * @param map
	 */
	private static void addEventbriteBusiness(
			HashMap<Object, ArrayList<String>> the_map) {
		 ArrayList<String> the_list = new  ArrayList<String>();
			the_list.add(CONFERENCES);
			the_list.add(MEETINGS);
			the_list.add(CONVENTIONS);
			the_list.add(SEMINARS);
			the_map.put(Categories.Business, the_list);
			addToCategorySubcategoryMapping(Categories.Business, the_list);	
	}
	
	/**
	 * @param map
	 */
	private static void addEventbriteAcademic(
			HashMap<Object, ArrayList<String>> the_map) {
		 ArrayList<String> the_list = new  ArrayList<String>();
			the_list.add(REUNIONS);
			the_list.add(RELIGION);
			addToCategorySubcategoryMapping(Categories.Academic, the_list);		
	}

	/**
	 * @param academic
	 * @param the_list
	 */
	private static void addToCategorySubcategoryMapping(Categories the_category,
			ArrayList<String> the_list) {
		if (the_category != null && the_list != null)
		{
			if (myCategorySourcecategoryMap.get(the_category) == null)
			{
				myCategorySourcecategoryMap.put(the_category, new ArrayList<String>());
			}
			ArrayList<String> existinglist = myCategorySourcecategoryMap.get(the_category);
			existinglist.addAll(the_list);
		}
	}

	
	private static double computeDistance(double the_eventLatitude,
			double the_eventLongitude, double the_fromLatitude, double the_fromLongitude) {

		float[] res = new float[3];
		try
		{
		Location.distanceBetween(the_eventLatitude, the_eventLongitude, the_fromLatitude, the_fromLongitude,res );
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return res[0] * DataExtractor.METERTOMILE;
	}
	
}


	


