with open("app/src/main/java/com/yofidewo/pos/ui/PosViewModel.kt", "r") as f:
    content = f.read()

bad = """        } else {
            "${String.format(Locale.US, "%.2f", valInCurr)} ${curr.symbol}"
        }
    }

    val customers"""

good = """        } else {
            "${String.format(Locale.US, "%.2f", valInCurr)} ${curr.symbol}"
        }
    }

    val customers"""

# Wait, the problem is it was inside the fun, meaning the fun is missing a closing brace or the class is missing a closing brace?
# Let's check how many braces are in `formatMoney`
import re
match = re.search(r'fun formatMoney[\s\S]*?val customers', content)
if match:
    print(match.group(0))
