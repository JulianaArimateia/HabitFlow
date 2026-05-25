import os
import re

directories = ["/home/diego/Development/HabitFlow/app/src/main/java/com/habitflow/ui"]

replacements = {
    r'\bPrimaryLight\b': 'MaterialTheme.colorScheme.primaryContainer',
    r'\bPrimaryDark\b': 'MaterialTheme.colorScheme.onPrimaryContainer'
}

for root, _, files in os.walk(directories[0]):
    for file in files:
        if file.endswith(".kt") and file not in ["Color.kt", "Theme.kt"]:
            filepath = os.path.join(root, file)
            with open(filepath, "r") as f:
                content = f.read()
            
            new_content = content
            for old, new in replacements.items():
                new_content = re.sub(old, new, new_content)
            
            if new_content != content:
                with open(filepath, "w") as f:
                    f.write(new_content)
                print(f"Updated {filepath}")
