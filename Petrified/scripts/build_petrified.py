#!/usr/bin/env python3
"""
Petrified Plugin Build Script
Self-contained CI/CD engine using only Python standard library.
Builds the multi-module Maven project and produces shaded JARs.
"""

import argparse
import os
import shutil
import subprocess
import sys
import zipfile
from pathlib import Path

# Version mapping: MC version -> {java_release, api_coords}
VERSION_MAP = {
    "1_21_11": {"java": 21, "api": "io.papermc:paper-api:1.21.1-R0.1-SNAPSHOT"},
    "1_21_10": {"java": 21, "api": "io.papermc:paper-api:1.21.1-R0.1-SNAPSHOT"},
    "1_21_9": {"java": 21, "api": "io.papermc:paper-api:1.21.1-R0.1-SNAPSHOT"},
    "1_21_8": {"java": 21, "api": "io.papermc:paper-api:1.21.1-R0.1-SNAPSHOT"},
    "1_21_7": {"java": 21, "api": "io.papermc:paper-api:1.21.1-R0.1-SNAPSHOT"},
    "1_21_6": {"java": 21, "api": "io.papermc:paper-api:1.21.1-R0.1-SNAPSHOT"},
    "1_21_5": {"java": 21, "api": "io.papermc:paper-api:1.21.1-R0.1-SNAPSHOT"},
    "1_21_4": {"java": 21, "api": "io.papermc:paper-api:1.21.1-R0.1-SNAPSHOT"},
    "1_21_3": {"java": 21, "api": "io.papermc:paper-api:1.21.1-R0.1-SNAPSHOT"},
    "1_21_1": {"java": 21, "api": "io.papermc:paper-api:1.21.1-R0.1-SNAPSHOT"},
    "1_21": {"java": 21, "api": "io.papermc:paper-api:1.21-R0.1-SNAPSHOT"},
    "1_20_6": {"java": 21, "api": "io.papermc:paper-api:1.20.6-R0.1-SNAPSHOT"},
    "1_20_4": {"java": 17, "api": "io.papermc:paper-api:1.20.4-R0.1-SNAPSHOT"},
    "1_20_1": {"java": 17, "api": "io.papermc:paper-api:1.20.1-R0.1-SNAPSHOT"},
    "1_19_4": {"java": 17, "api": "io.papermc:paper-api:1.19.4-R0.1-SNAPSHOT"},
    "1_18_2": {"java": 17, "api": "io.papermc:paper-api:1.18.2-R0.1-SNAPSHOT"},
    "1_17_1": {"java": 17, "api": "io.papermc:paper-api:1.17.1-R0.1-SNAPSHOT"},
    "1_16_5": {"java": 8, "api": "org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT"},
    "1_12_2": {"java": 8, "api": "org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT"},
    "1_8_8": {"java": 8, "api": "org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT"},
}

DEFAULT_TARGET = "1_21_11"


def detect_jdk():
    """Detect JDK installation."""
    java_home = os.environ.get("JAVA_HOME")
    if java_home and os.path.exists(os.path.join(java_home, "bin", "java")):
        return java_home
    
    # Try common locations
    common_paths = [
        "/usr/lib/jvm/default-java",
        "/usr/lib/jvm/java-17-openjdk",
        "/usr/lib/jvm/java-21-openjdk",
        "/opt/jdk",
        "C:\\Program Files\\Java\\jdk",
    ]
    for path in common_paths:
        if os.path.exists(os.path.join(path, "bin", "java")):
            return path
    
    # Check if java is in PATH
    try:
        result = subprocess.run(["java", "-version"], capture_output=True, text=True)
        if result.returncode == 0:
            return None  # Java found but don't know home
    except FileNotFoundError:
        pass
    
    return None


def find_maven_wrapper(project_root):
    """Find Maven wrapper script."""
    if os.name == "nt":
        wrapper = project_root / "mvnw.cmd"
    else:
        wrapper = project_root / "mvnw"
    
    if wrapper.exists():
        return str(wrapper)
    
    # Fallback to system maven
    try:
        result = subprocess.run(["mvn", "--version"], capture_output=True, text=True)
        if result.returncode == 0:
            return "mvn"
    except FileNotFoundError:
        pass
    
    return None


def run_build(project_root, target_version, offline=False, include_legacy=False):
    """Run Maven build with appropriate profile."""
    mvn = find_maven_wrapper(project_root)
    if not mvn:
        print("ERROR: No Maven or Maven wrapper found!")
        print("Please ensure Maven is installed or run from project root with mvnw.")
        return False
    
    cmd = [mvn, "-q", "clean", "package"]
    
    if offline:
        cmd.append("-o")
    
    cmd.extend([f"-Dmc.target={target_version}", f"-Pmc-{target_version}"])
    
    print(f"Running: {' '.join(cmd)}")
    
    try:
        result = subprocess.run(
            cmd,
            cwd=project_root,
            capture_output=True,
            text=True,
            timeout=600
        )
        
        if result.returncode != 0:
            print(f"BUILD FAILED (exit code {result.returncode})")
            print("STDOUT:", result.stdout[-500:] if len(result.stdout) > 500 else result.stdout)
            print("STDERR:", result.stderr[-500:] if len(result.stderr) > 500 else result.stderr)
            return False
        
        print("BUILD SUCCESS")
        return True
        
    except subprocess.TimeoutExpired:
        print("BUILD TIMEOUT (exceeded 10 minutes)")
        return False
    except Exception as e:
        print(f"BUILD ERROR: {e}")
        return False


def relocate_artifacts(project_root, output_dir, include_legacy=False):
    """Copy built JARs to output directory."""
    output_dir = Path(output_dir)
    output_dir.mkdir(parents=True, exist_ok=True)
    
    plugin_target = project_root / "petrified-plugin" / "target"
    
    # Find main JAR
    main_jar = None
    legacy_jar = None
    
    for jar in plugin_target.glob("*.jar"):
        if "original" not in jar.name:
            if "legacy" in jar.name.lower() or "Legacy" in jar.name:
                legacy_jar = jar
            else:
                main_jar = jar
    
    if main_jar and main_jar.exists():
        dest = output_dir / "Petrified.jar"
        shutil.copy2(main_jar, dest)
        print(f"Copied: {main_jar.name} -> {dest}")
    else:
        print("WARNING: Main JAR not found!")
        return False
    
    if include_legacy and legacy_jar and legacy_jar.exists():
        dest = output_dir / "Petrified-Legacy.jar"
        shutil.copy2(legacy_jar, dest)
        print(f"Copied: {legacy_jar.name} -> {dest}")
    
    return True


def verify_jar(jar_path):
    """Verify JAR is valid and non-empty."""
    if not os.path.exists(jar_path):
        return False
    
    size = os.path.getsize(jar_path)
    if size < 1000:
        return False
    
    try:
        with zipfile.ZipFile(jar_path, 'r') as zf:
            if 'plugin.yml' not in zf.namelist():
                return False
        return True
    except zipfile.BadZipfile:
        return False


def main():
    parser = argparse.ArgumentParser(
        description="Build Petrified plugin",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
  %(prog)s                      # Build with default settings
  %(prog)s --target 1_20_1      # Build for MC 1.20.1
  %(prog)s --include-legacy     # Also build legacy JAR
  %(prog)s --offline            # Build without network
  %(prog)s --out ./dist         # Output to specific directory
        """
    )
    
    parser.add_argument(
        "--target", "-t",
        default=DEFAULT_TARGET,
        help=f"Target Minecraft version (default: {DEFAULT_TARGET})"
    )
    
    parser.add_argument(
        "--include-legacy",
        action="store_true",
        help="Also build legacy JAR for 1.8.8-1.16.5"
    )
    
    parser.add_argument(
        "--offline", "-o",
        action="store_true",
        help="Build in offline mode"
    )
    
    parser.add_argument(
        "--out", "-O",
        default="./dist",
        help="Output directory for built JARs"
    )
    
    parser.add_argument(
        "--skip-build",
        action="store_true",
        help="Skip build, only relocate existing artifacts"
    )
    
    parser.add_argument(
        "--project-root", "-p",
        default=".",
        help="Project root directory"
    )
    
    args = parser.parse_args()
    
    # Validate target version
    if args.target not in VERSION_MAP:
        print(f"ERROR: Unknown target version '{args.target}'")
        print(f"Valid versions: {', '.join(VERSION_MAP.keys())}")
        return 1
    
    project_root = Path(args.project_root).resolve()
    
    if not (project_root / "pom.xml").exists():
        print(f"ERROR: Not a Maven project (no pom.xml in {project_root})")
        return 1
    
    # Detect JDK
    jdk_path = detect_jdk()
    if jdk_path:
        print(f"Found JDK at: {jdk_path}")
    else:
        print("Using system Java (JAVA_HOME not set)")
    
    # Set JAVA_HOME if detected
    if jdk_path:
        os.environ["JAVA_HOME"] = jdk_path
    
    info = VERSION_MAP[args.target]
    print(f"\n=== Petrified Build ===")
    print(f"Target MC: {args.target}")
    print(f"Java Release: {info['java']}")
    print(f"API: {info['api']}")
    print(f"Include Legacy: {args.include_legacy}")
    print(f"Output Dir: {args.out}")
    print()
    
    # Run build
    if not args.skip_build:
        success = run_build(
            project_root,
            args.target,
            offline=args.offline,
            include_legacy=args.include_legacy
        )
        if not success:
            return 1
    
    # Relocate artifacts
    if not relocate_artifacts(project_root, args.out, args.include_legacy):
        return 1
    
    # Verify JARs
    main_jar = Path(args.out) / "Petrified.jar"
    if verify_jar(main_jar):
        print(f"\n✓ Built successfully: {main_jar}")
    else:
        print(f"\n✗ JAR verification failed: {main_jar}")
        return 1
    
    if args.include_legacy:
        legacy_jar = Path(args.out) / "Petrified-Legacy.jar"
        if legacy_jar.exists() and verify_jar(legacy_jar):
            print(f"✓ Built successfully: {legacy_jar}")
    
    print(f"\nBuild complete! JARs are in: {Path(args.out).resolve()}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
