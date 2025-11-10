import requests
import json

# Define the Overpass API endpoint
OVERPASS_URL = "https://overpass-api.de/api/interpreter"

# Overpass QL query
query = """
[out:json][timeout:60];
area["name"="Macomb County"]["admin_level"="6"]["boundary"="administrative"]->.searchArea;
(
  node["amenity"="restaurant"](area.searchArea);
  way["amenity"="restaurant"](area.searchArea);
  relation["amenity"="restaurant"](area.searchArea);
);
out center;
"""

def main():
    print("Querying Overpass API for restaurants in Macomb County, Michigan...")
    response = requests.post(OVERPASS_URL, data={"data": query})
    response.raise_for_status()  # Raise error if API call failed

    data = response.json()
    elements = data.get("elements", [])
    print(f"Found {len(elements)} restaurant entries.\n")

    # Print a few example results
    for el in elements[:20]:  # limit to first 20 for readability
        name = el.get("tags", {}).get("name", "(Unnamed)")
        lat = el.get("lat") or el.get("center", {}).get("lat")
        lon = el.get("lon") or el.get("center", {}).get("lon")
        print(f"🍽️  {name} — ({lat}, {lon})")

    # Optionally, save all data to a local JSON file
    with open("macomb_restaurants.json", "w", encoding="utf-8") as f:
        json.dump(data, f, indent=2)
    print("\nResults saved to macomb_restaurants.json")

if __name__ == "__main__":
    main()
