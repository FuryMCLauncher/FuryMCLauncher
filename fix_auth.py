import sys

path = 'ZalithLauncher/src/main/java/fury/mc/launcher/ui/screens/content/elements/AccountElements.kt'
with open(path, 'r') as f:
    lines = f.readlines()

with open(path, 'w') as f:
    for line in lines:
        if 'stringResource(R.string.account_type_microsoft)' in line:
            # Found the Microsoft login item
            f.write('                            /*\n')
            f.write(line)
        elif 'onDismissRequest()' in line and 'onMicrosoftLogin()' in lines[lines.index(line)-1]:
             f.write(line)
             f.write('                            */\n')
        else:
            f.write(line)
