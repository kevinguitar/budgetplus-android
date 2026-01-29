import os

core_dir = "/Users/kevin/Documents/budgetplus-android/core"

for module_name in os.listdir(core_dir):
    module_path = os.path.join(core_dir, module_name)
    if not os.path.isdir(module_path):
        continue

    build_gradle = os.path.join(module_path, "build.gradle.kts")
    if not os.path.exists(build_gradle):
        continue

    print(f"Processing {module_name}...")

    # Modify build.gradle.kts
    with open(build_gradle, 'r') as f:
        content = f.read()

    original_content = content
    content = content.replace("alias(budgetplus.plugins.android.library)", "alias(budgetplus.plugins.kotlin.multiplatform)")
    content = content.replace("alias(budgetplus.plugins.compose.multiplatform)", "")

    if content != original_content:
        with open(build_gradle, 'w') as f:
            f.write(content)
        print(f"  Updated build.gradle.kts")
    else:
        print(f"  No changes needed in build.gradle.kts")

    # Rename directories
    src_dir = os.path.join(module_path, "src")
    if os.path.exists(src_dir):
        renames = {
            "main": "androidMain",
            "test": "androidTest",
            "testFixtures": "androidTestFixtures"
        }
        for old, new in renames.items():
            old_path = os.path.join(src_dir, old)
            new_path = os.path.join(src_dir, new)
            if os.path.exists(old_path):
                if not os.path.exists(new_path):
                    os.rename(old_path, new_path)
                    print(f"  Renamed src/{old} to src/{new}")
                else:
                    print(f"  Skipping rename src/{old} -> src/{new} (target exists)")
