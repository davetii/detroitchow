import requests
import json
import sys

OVERPASS_URL = "https://overpass-api.de/api/interpreter"

def build_query(state_name: str, city_name: str) -> str:
    """
    Build an Overpass QL query to find restaurants within a given city and state.
    """
    return f"""
    [out:json][timeout:60];

    // Find the state area
    area["name"="{state_name}"]["admin_level"="4"]["boundary"="administrative"]->.state;

    // Find the city within that state
    area["name"="{city_name}"]["admin_level"="8"]["boundary"="administrative"](area.state)->.searchArea;

    // Find restaurants within the city boundary
    (
      node["amenity"="restaurant"](area.searchArea);
      way["amenity"="restaurant"](area.searchArea);
      relation["amenity"="restaurant"](area.searchArea);
    );

    out center;
    """

def fetch_restaurants(state_name: str, city_name: str):
    query = build_query(state_name, city_name)

    print(f"🔎 Querying restaurants in {city_name}, {state_name}...")
    response = requests.post(OVERPASS_URL, data={"data": query})
    response.raise_for_status()

    data = response.json()
    elements = data.get("elements", [])
    print(f"✅ Found {len(elements)} restaurant entries.\n")

    # Print first few results
    for el in elements[:20]:
        tags = el.get("tags", {})
        name = tags.get("name", "(Unnamed)")
        lat = el.get("lat") or el.get("center", {}).get("lat")
        lon = el.get("lon") or el.get("center", {}).get("lon")
        print(f"🍽️  {name} — ({lat}, {lon})")

    # Save all results to JSON file
    filename = f"{city_name.replace(' ', '_')}_{state_name.replace(' ', '_')}_restaurants.json"
    with open(filename, "w", encoding="utf-8") as f:
        json.dump(data, f, indent=2)
    print(f"\n💾 Results saved to {filename}")

def main():
    if len(sys.argv) != 3:
        print("Usage: python get_restaurants_by_city.py '<State Name>' '<City Name>'")
        print("Example: python get_restaurants_by_city.py 'Michigan' 'Sterling Heights'")
        sys.exit(1)

    state_name = sys.argv[1]
    city_name = sys.argv[2]

    fetch_restaurants(state_name, city_name)

if __name__ == "__main__":
    main()
