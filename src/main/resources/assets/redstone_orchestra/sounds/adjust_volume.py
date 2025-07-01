#!/usr/bin/env python3
"""
lower_volume.py
───────────────
Usage
-----
    python lower_volume.py <folder> [factor]

Arguments
---------
  folder   Path that contains the .ogg files.
  factor   Optional linear gain. 1.0 keeps the volume unchanged,
           0.8 ≈ -2 dB, 0.5 = -6 dB, 2.0 = +6 dB, ...  (default: 0.8)

The script calls FFmpeg for the heavy lifting, writes each file to a
temporary copy, and then atomically replaces the original so you keep
the same filenames.
"""

import subprocess
import sys
from pathlib import Path

def main():
    if len(sys.argv) < 2:
        print("Usage: python lower_volume.py <folder> [factor]")
        sys.exit(1)

    folder  = Path(sys.argv[1]).expanduser().resolve()
    factor  = float(sys.argv[2]) if len(sys.argv) > 2 else 0.8

    if not folder.is_dir():
        sys.exit(f"{folder} is not a directory.")

    ogg_files = sorted(folder.glob("*.ogg"))
    if not ogg_files:
        sys.exit("No .ogg files found.")

    for ogg in ogg_files:
        tmp = ogg.with_suffix(".tmp.ogg")
        print(f"{ogg.name}  →  {factor}×")
        subprocess.run([
            "ffmpeg", "-y", "-loglevel", "error",
            "-i", str(ogg),
            "-af", f"volume={factor}",
            str(tmp)
        ], check=True)
        tmp.replace(ogg)          # overwrite original atomically

    print("\nDone – all .ogg files updated!")

if __name__ == "__main__":
    main()
