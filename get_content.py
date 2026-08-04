with open("app/src/main/java/com/yofidewo/pos/ui/screens/ReportsTransactionsScreen.kt", "r") as f:
    content = f.read()

import re
match = re.search(r'items\(filtered.*?\) \{ tx ->(.*?)\}\n\s*\}', content, re.DOTALL)
if match:
    print(match.group(0))
