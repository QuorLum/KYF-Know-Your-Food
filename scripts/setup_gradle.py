import os
import urllib.request
import zipfile
import shutil

GRADLE_ZIP_URL = "https://services.gradle.org/distributions/gradle-8.4-bin.zip"
TEMP_DIR = os.environ.get("TEMP", r"C:\TEMP")
GRADLE_ZIP = os.path.join(TEMP_DIR, "gradle-8.4-bin.zip")
TARGET_DIR = r"C:\Users\QuorLum\.gradle\wrapper\dists\gradle-8.4-bin"

def main():
    print("Setting up Gradle...")
    os.makedirs(r"C:\Users\QuorLum\.gradle\wrapper\dists", exist_ok=True)
    if not os.path.exists(GRADLE_ZIP):
        print(f"Downloading {GRADLE_ZIP_URL}...")
        urllib.request.urlretrieve(GRADLE_ZIP_URL, GRADLE_ZIP)
    
    print("Extracting Gradle...")
    extract_dir = os.path.join(TEMP_DIR, "gradle-8.4-extracted")
    with zipfile.ZipFile(GRADLE_ZIP, 'r') as zip_ref:
        zip_ref.extractall(extract_dir)
    
    gradle_bin = os.path.join(extract_dir, "gradle-8.4", "bin", "gradle.bat")
    print(f"Gradle bin ready at: {gradle_bin}")

if __name__ == "__main__":
    main()
