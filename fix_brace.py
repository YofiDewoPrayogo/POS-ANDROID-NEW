import re

with open("app/src/main/java/com/yofidewo/pos/ui/PosViewModel.kt", "r") as f:
    lines = f.readlines()

new_lines = []
skip_next = False
for i, line in enumerate(lines):
    if skip_next:
        skip_next = False
        continue
    new_lines.append(line)
    if "repository.insertRole(RoleEntity(name = name, canViewDashboard = canViewDashboard, canViewCashier = canViewCashier, canViewProducts = canViewProducts, canViewReports = canViewReports, canViewSettings = canViewSettings))" in line:
        pass
    if "fun addRole(name: String, canViewDashboard: Boolean, canViewCashier: Boolean, canViewProducts: Boolean, canViewReports: Boolean, canViewSettings: Boolean) {" in line:
        pass
    if line.strip() == "}":
        # check if it's the extra one
        if i > 0 and lines[i-1].strip() == "}":
            if i > 2 and "repository.insertRole(RoleEntity(name = name" in lines[i-3]:
                # This is the extra one! Pop the current line and don't append it
                new_lines.pop()

with open("app/src/main/java/com/yofidewo/pos/ui/PosViewModel.kt", "w") as f:
    f.writelines(new_lines)

