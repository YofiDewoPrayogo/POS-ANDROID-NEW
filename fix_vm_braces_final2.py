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

content = content.replace(bad, good)

with open("app/src/main/java/com/yofidewo/pos/ui/PosViewModel.kt", "w") as f:
    f.write(content)
