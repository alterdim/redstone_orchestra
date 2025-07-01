#!/usr/bin/env python3
"""
generate_sounds_json.py
───────────────────────
Usage:
    # one instrument
    python generate_sounds_json.py triangle

    # several instruments
    python generate_sounds_json.py triangle electric_guitar flute

By default it writes ./sounds.json next to the script.
"""

import json
import sys
from pathlib import Path
from typing import List, Dict

SOUNDS_PER_INSTRUMENT = 21      # 0-20 inclusive
CATEGORY                = "record"
NAMESPACE_PREFIX        = "redstone_orchestra"

def build_entries(instruments: List[str]) -> Dict[str, dict]:
    """Return the complete mapping for the requested instruments."""
    result = {}
    for inst in instruments:
        inst = inst.lower()
        for n in range(SOUNDS_PER_INSTRUMENT):
            key = f"{inst}_note_{n}"
            result[key] = {
                "category": CATEGORY,
                "sounds": [f"{NAMESPACE_PREFIX}:{inst}/{n}"]
            }
    return result

def main():
    if len(sys.argv) < 2:
        print("Give at least one instrument name. Example:\n"
              "  python generate_sounds_json.py triangle electric_guitar")
        sys.exit(1)

    instruments = sys.argv[1:]
    data        = build_entries(instruments)

    out_path = Path("sounds.json")
    out_path.write_text(json.dumps(data, indent=2) + "\n", encoding="utf-8")
    print(f"Wrote {out_path} with {len(data)} entries "
          f"({SOUNDS_PER_INSTRUMENT} per instrument).")

if __name__ == "__main__":
    main()
