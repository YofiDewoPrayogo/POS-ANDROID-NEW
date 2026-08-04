with open("app/src/main/java/com/yofidewo/pos/ui/PosViewModel.kt", "r") as f:
    content = f.read()

bad_format_money = """        } else {
            "${String.format(Locale.US, "%.2f", valInCurr)} ${curr.symbol}"
            }"""

good_format_money = """        } else {
            "${String.format(Locale.US, "%.2f", valInCurr)} ${curr.symbol}"
        }
    }"""

content = content.replace(bad_format_money, good_format_money)

with open("app/src/main/java/com/yofidewo/pos/ui/PosViewModel.kt", "w") as f:
    f.write(content)

