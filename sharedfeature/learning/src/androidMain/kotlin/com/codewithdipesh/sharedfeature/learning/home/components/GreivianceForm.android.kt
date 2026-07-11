package com.codewithdipesh.sharedfeature.learning.home.components

import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import com.codewithdipesh.kanasensei.ui.components.buttons.customClickable
import com.codewithdipesh.kanasensei.ui.components.textfield.KanaBoxTextField
import com.codewithdipesh.kanasensei.ui.theme.KanaColors
import com.codewithdipesh.sharedfeature.learning.home.uistates.GrievienceState

@Composable
actual fun GreivianceForm(
    state: GrievienceState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onMediaSelected: (ImageBitmap) -> Unit,
    onRemoveMedia: (Int) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(context.contentResolver, it)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                }
                onMediaSelected(bitmap.asImageBitmap())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = KanaColors.learningBackground,
        title = {
            Text(
                text = "Report & Feedback",
                style = MaterialTheme.typography.displaySmall.copy(
                    color = KanaColors.onLearningBackground
                )
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Anything You want to Report or Suggest or Just Yap>_<",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = KanaColors.onLearningBackground.copy(0.6f)
                    )
                )

                // Title
                KanaBoxTextField(
                    value = state.title,
                    onValueChange = onTitleChange,
                    label = "Title",
                    textColor = KanaColors.onLearningBackground,
                    borderColor = KanaColors.onLearningBackground.copy(0.2f),
                    placeholderColor = KanaColors.onLearningBackground.copy(0.4f)
                )

                // Description
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp)
                        .border(
                            width = 0.5.dp,
                            color = KanaColors.onLearningBackground.copy(0.2f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp)
                ) {
                    BasicTextField(
                        value = state.description,
                        onValueChange = onDescriptionChange,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = KanaColors.onLearningBackground
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        decorationBox = { innerTextField ->
                            if (state.description.isEmpty()) {
                                Text(
                                    text = "Description (Optional)",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = KanaColors.onLearningBackground.copy(0.4f)
                                    )
                                )
                            }
                            innerTextField()
                        }
                    )
                }

                // Media Attachments
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Attachments",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = KanaColors.onLearningBackground
                        )
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        item {
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(KanaColors.onLearningBackground.copy(0.1f))
                                    .clickable { launcher.launch("image/*") },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add media",
                                    tint = KanaColors.onLearningBackground
                                )
                            }
                        }

                        itemsIndexed(state.attachedMedia) { index, bitmap ->
                            Box(modifier = Modifier.size(70.dp)) {
                                Image(
                                    bitmap = bitmap,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = 4.dp, y = (-4).dp)
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(Color.Red)
                                        .clickable { onRemoveMedia(index) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove",
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Text(
                text = "Send",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = Bold,
                    color = KanaColors.onLearningBackground,
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .customClickable(onClick = onConfirm)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            )
        },
        dismissButton = {
            Text(
                text = "Cancel",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = KanaColors.onLearningBackground.copy(0.7f),
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .customClickable(onClick = onDismiss)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
    )
}
