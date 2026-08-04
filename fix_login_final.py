import re

with open("app/src/main/java/com/yofidewo/pos/ui/screens/LoginScreen.kt", "r") as f:
    content = f.read()

# Remove generate by
content = content.replace('                Spacer(modifier = Modifier.height(8.dp))\n                Text("generate by Yofi Dewo Prayogo", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)\n', '')

# Update first text field
old_email_field = """                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Kasir") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )"""

new_email_field = """                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Kasir") },
                    placeholder = { Text("contoh@email.com") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )"""

content = content.replace(old_email_field, new_email_field)

old_pin_field = """                OutlinedTextField(
                    value = pin,
                    onValueChange = { pin = it },
                    label = { Text("PIN / Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )"""

new_pin_field = """                OutlinedTextField(
                    value = pin,
                    onValueChange = { pin = it },
                    label = { Text("PIN / Password") },
                    placeholder = { Text("••••••••") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )"""

content = content.replace(old_pin_field, new_pin_field)

with open("app/src/main/java/com/yofidewo/pos/ui/screens/LoginScreen.kt", "w") as f:
    f.write(content)
