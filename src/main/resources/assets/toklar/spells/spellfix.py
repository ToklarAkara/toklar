import json
import os

# Use the folder this script is in
FOLDER = os.path.dirname(os.path.abspath(__file__))

for filename in os.listdir(FOLDER):
    if filename.endswith(".json"):
        path = os.path.join(FOLDER, filename)

        with open(path, "r") as f:
            data = json.load(f)

        # Flip looting to false if it exists
        if "enabled" in data and "looting" in data["enabled"]:
            data["enabled"]["looting"] = False

        with open(path, "w") as f:
            json.dump(data, f, indent=2)

print("All spell JSONs updated.")
