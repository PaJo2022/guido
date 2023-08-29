package com.guido.app

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter


const val INITIAL_DELAY_FOR_MARQEE = 15
val SPEED_FOR_MARQEE = 10.dp



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExtraBoldTextView(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: TextUnit = 16.sp,
    textAlign: TextAlign ?= null,
    textColor: Color = colorResource(
        id = R.color.color_4
    )
) {
    Text(
        modifier = modifier.basicMarquee(initialDelayMillis = INITIAL_DELAY_FOR_MARQEE, velocity = SPEED_FOR_MARQEE),
        text = text,
        fontSize = fontSize,
        textAlign = textAlign,
        color = textColor,
        lineHeight = 0.sp,
        fontFamily = FontFamily(Font(R.font.font_extra_bold,FontWeight.ExtraBold))
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BoldTextView(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: TextUnit = 20.sp,
    textAlign: TextAlign ?= null,
    textColor: Color = colorResource(
        id = R.color.color_4
    )
) {
    Text(
        modifier = modifier.basicMarquee(initialDelayMillis = INITIAL_DELAY_FOR_MARQEE, velocity = SPEED_FOR_MARQEE),
        text = text,
        fontSize = fontSize,
        textAlign = textAlign,
        color = textColor,
        style = TextStyle(
            platformStyle = PlatformTextStyle(
                includeFontPadding = false,
            ),
        ),
        fontFamily = FontFamily(Font(R.font.font_bold, FontWeight.Bold))
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SemiBoldTextView(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: TextUnit = 20.sp,
    textAlign: TextAlign ?= null,
    textColor: Color = colorResource(
        id = R.color.color_4
    )
) {
    Text(
        modifier = modifier.basicMarquee(initialDelayMillis = INITIAL_DELAY_FOR_MARQEE, velocity = SPEED_FOR_MARQEE),
        text = text,
        fontSize = fontSize,
        textAlign = textAlign,
        color = textColor,
        style = TextStyle(
            platformStyle = PlatformTextStyle(
                includeFontPadding = false,
            ),
        ),
        lineHeight = 0.sp,
        fontFamily = FontFamily(Font(R.font.font_semi_bold,FontWeight.SemiBold))
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediumTextView(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: TextUnit = 16.sp,
    textAlign: TextAlign ?= null,
    textColor: Color = colorResource(
        id = R.color.color_4
    )
) {
    Text(
        modifier = modifier.basicMarquee(initialDelayMillis = INITIAL_DELAY_FOR_MARQEE, velocity = SPEED_FOR_MARQEE),
        text = text,
        fontSize = fontSize,
        textAlign = textAlign,
        color = textColor,
        style = TextStyle(
            platformStyle = PlatformTextStyle(
                includeFontPadding = false,
            ),
        ),
        lineHeight = 0.sp,
        fontFamily = FontFamily(Font(R.font.font_medium,FontWeight.SemiBold))
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NormalTextView(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: TextUnit = 12.sp,
    textAlign: TextAlign ?= null,
    textColor: Color = colorResource(
        id = R.color.color_4
    )
) {
    Text(
        modifier = modifier.basicMarquee(initialDelayMillis = INITIAL_DELAY_FOR_MARQEE, velocity = SPEED_FOR_MARQEE),
        text = text,
        textAlign = textAlign,
        fontSize = fontSize,
        color = textColor,
        style = TextStyle(
            platformStyle = PlatformTextStyle(
                includeFontPadding = false,
            ),
        ),
        lineHeight = 0.sp,
        fontFamily = FontFamily(Font(R.font.font_regular,FontWeight.Normal))
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LightTextView(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: TextUnit = 12.sp,
    textAlign: TextAlign ?= null,
    textColor: Color = colorResource(
        id = R.color.color_light_grey_80
    )
) {
    Text(
        modifier = modifier.basicMarquee(initialDelayMillis = INITIAL_DELAY_FOR_MARQEE, velocity = SPEED_FOR_MARQEE),
        text = text,
        fontSize = fontSize,
        textAlign = textAlign,
        color = textColor,
        style = TextStyle(
            platformStyle = PlatformTextStyle(
                includeFontPadding = false,
            ),
        ),
        lineHeight = 0.sp,
        fontFamily = FontFamily(Font(R.font.font_light,FontWeight.Light))
    )
}


@Composable
fun AlertDialogSample(
    showDialog: Boolean,
    action : () -> Unit,
    onDismiss: () -> Unit,
    title: String,
    message: String,
    confirmButtonText: String,
    dismissButtonText: String
) {
    val context = LocalContext.current

    if (showDialog) {
        AlertDialog(
            backgroundColor = colorResource(id = R.color.color_secondary),
            onDismissRequest = {
                // Handle dismiss action if needed
                onDismiss()
            },
            title = {
                SemiBoldTextView(text = title, fontSize = 12.sp)
            },
            text = {
                NormalTextView(text = message)
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.color_secondary)),
                    onClick = {
                        action()
                        onDismiss()
                    }
                ) {
                    NormalTextView(text = confirmButtonText)
                }
            },
            dismissButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.color_secondary)),
                    onClick = {
                        // Handle dismiss action if needed
                        onDismiss()
                    }
                ) {
                    NormalTextView(text = dismissButtonText)
                }
            }
        )
    }
}

@Composable
fun LoadImageFromLocalOrUrl(
    imageUrl: String,
    modifier: Modifier = Modifier,
    contentDescription: String ?= null
) {
    val context = LocalContext.current



    val imageLoader = ImageLoader.Builder(context).respectCacheHeaders(false).build()
    Image(modifier = modifier,
        painter = rememberAsyncImagePainter(imageUrl, imageLoader = imageLoader),
        contentScale = ContentScale.Crop,
        contentDescription = contentDescription)

}