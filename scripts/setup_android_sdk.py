import os
import sys
import urllib.request
import zipfile
import shutil
import subprocess

SDK_DIR = r"C:\Users\QuorLum\AppData\Local\Android\Sdk"
TOOLS_URL = "https://dl.google.com/android/repository/commandlinetools-win-11076708_latest.zip"
ZIP_PATH = os.path.join(os.environ.get("TEMP", r"C:\TEMP"), "cmdline-tools.zip")
JAVA_HOME = r"C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot"

def main():
    print(f"Ensuring SDK dir at {SDK_DIR}...")
    os.makedirs(os.path.join(SDK_DIR, "cmdline-tools"), exist_ok=True)
    
    latest_dir = os.path.join(SDK_DIR, "cmdline-tools", "latest")
    sdkmanager_bat = os.path.join(latest_dir, "bin", "sdkmanager.bat")
    
    if not os.path.exists(sdkmanager_bat):
        print(f"Downloading {TOOLS_URL}...")
        urllib.request.urlretrieve(TOOLS_URL, ZIP_PATH)
        print("Extracting cmdline-tools...")
        with zipfile.ZipFile(ZIP_PATH, 'r') as zip_ref:
            zip_ref.extractall(os.path.join(SDK_DIR, "cmdline-tools"))
        
        extracted_cmdline = os.path.join(SDK_DIR, "cmdline-tools", "cmdline-tools")
        if os.path.exists(extracted_cmdline):
            if os.path.exists(latest_dir):
                shutil.rmtree(latest_dir)
            os.rename(extracted_cmdline, latest_dir)
        if os.path.exists(ZIP_PATH):
            os.remove(ZIP_PATH)
        print("Cmdline-tools installed successfully!")
    else:
        print("Cmdline-tools already exists.")

    env = os.environ.copy()
    env["JAVA_HOME"] = JAVA_HOME
    env["ANDROID_HOME"] = SDK_DIR
    env["ANDROID_SDK_ROOT"] = SDK_DIR
    env["PATH"] = f"{os.path.join(JAVA_HOME, 'bin')};{os.path.join(latest_dir, 'bin')};{env.get('PATH', '')}"

    print("Accepting licenses...")
    p = subprocess.Popen([sdkmanager_bat, "--licenses"], stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True, env=env)
    stdout, stderr = p.communicate(input="y\ny\ny\ny\ny\ny\ny\ny\ny\ny\n")
    print("Licenses accepted.")

    print("Installing build-tools and platform...")
    subprocess.run([sdkmanager_bat, "platforms;android-34", "build-tools;34.0.0", "platform-tools"], env=env, check=False)
    print("Android SDK setup complete!")

if __name__ == "__main__":
    main()
