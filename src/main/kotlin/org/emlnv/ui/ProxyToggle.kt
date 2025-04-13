package org.emlnv.ui


import androidx.compose.foundation.layout.*
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProxyToggleUI(
    useProxy: Boolean,
    onProxyToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Use Proxy:")
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = useProxy,
            onCheckedChange = { onProxyToggle(it) }
        )
    }
}
