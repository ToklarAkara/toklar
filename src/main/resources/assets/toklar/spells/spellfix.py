import os, json

for filename in os.listdir("."):
    if filename.endswith(".json"):
        try:
            with open(filename, "r", encoding="utf-8") as f:
                data = json.load(f)

            if "base_properties" in data:
                original = data["base_properties"]
                data["base_properties"] = {}

                with open(filename, "w", encoding="utf-8") as f:
                    json.dump(data, f, indent=2, ensure_ascii=False)

                print(f"{filename}: cleared base_properties (was {len(original)} entries)")
            else:
                print(f"{filename}: no base_properties key")
        except Exception as e:
            print(f"{filename}: error {e}")