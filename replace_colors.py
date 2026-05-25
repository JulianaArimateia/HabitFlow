import os
import re

directories = ["/home/diego/Development/HabitFlow/app/src/main/java/com/habitflow/ui"]

replacements = {
    r'\bPrimary\b': 'MaterialTheme.colorScheme.primary',
    r'\bSecondary\b': 'MaterialTheme.colorScheme.secondary',
    r'\bBackground\b': 'MaterialTheme.colorScheme.background',
    r'\bSurface\b': 'MaterialTheme.colorScheme.surface',
    r'\bOnPrimary\b': 'MaterialTheme.colorScheme.onPrimary',
    r'\bOnBackground\b': 'MaterialTheme.colorScheme.onBackground',
    r'\bOnSurface\b': 'MaterialTheme.colorScheme.onSurface',
    r'\bOnSurfaceVar\b': 'MaterialTheme.colorScheme.onSurfaceVariant',
    r'\bError\b': 'MaterialTheme.colorScheme.error'
}

for root, _, files in os.walk(directories[0]):
    for file in files:
        if file.endswith(".kt") and file not in ["Color.kt", "Theme.kt", "CategoryUtils.kt"]:
            filepath = os.path.join(root, file)
            with open(filepath, "r") as f:
                content = f.read()
            
            new_content = content
            for old, new in replacements.items():
                new_content = re.sub(old, new, new_content)
            
            if new_content != content:
                # Add MaterialTheme import if not present
                if "import androidx.compose.material3.MaterialTheme" not in new_content:
                    new_content = new_content.replace(
                        "import androidx.compose.runtime.Composable",
                        "import androidx.compose.material3.MaterialTheme\nimport androidx.compose.runtime.Composable"
                    )
                with open(filepath, "w") as f:
                    f.write(new_content)
                print(f"Updated {filepath}")
