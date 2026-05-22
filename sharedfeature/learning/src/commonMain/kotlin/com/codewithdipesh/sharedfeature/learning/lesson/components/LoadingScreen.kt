package com.codewithdipesh.sharedfeature.learning.lesson.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codewithdipesh.kanasensei.ui.components.buttons.AppButton3D
import com.codewithdipesh.kanasensei.ui.resources.Res
import com.codewithdipesh.kanasensei.ui.resources.lesson_loading
import com.codewithdipesh.kanasensei.ui.resources.ramenbowl_shadow
import com.codewithdipesh.kanasensei.ui.theme.KanaColors
import com.codewithdipesh.kanasensei.ui.theme.KanaSenseiTypography
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import org.jetbrains.compose.resources.painterResource

@Composable
fun LoadingScreen(
    modifier : Modifier = Modifier
){
    val composition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(
            Res.readBytes("files/ramen.json").decodeToString()
        )
    }

    var funFact by remember { mutableStateOf(funFacts.random()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KanaColors.background)
            .padding(24.dp)
            .padding(top = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ){
        Icon(
            painter = painterResource(Res.drawable.lesson_loading),
            contentDescription = null,
            tint = Color(0xFF00280F),
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
        val lottieAlpha by animateFloatAsState(
            targetValue = if (composition != null) 1f else 0f,
            animationSpec = tween(durationMillis = 250)
        )
        Icon(
            painter = painterResource(Res.drawable.ramenbowl_shadow),
            contentDescription = null,
            modifier = Modifier
                .alpha(lottieAlpha)
                .offset(x = 18.dp,y = 160.dp)
                .scale(0.7f)
        )
        Image(
            painter = rememberLottiePainter(
                composition = composition,
                iterations = Compottie.IterateForever
            ),
            contentDescription = null,
            modifier = Modifier
                .size(300.dp)
                .offset(y = (-130).dp )
                .alpha(lottieAlpha)
        )
        Box(
            modifier = Modifier.fillMaxWidth()
                .offset(y = (-100).dp)
                .height(12.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(Color.White.copy(0.3f))
        )
        AppButton3D(
            modifier = Modifier
                .width(150.dp),
            label = "Tap for FunFact" ,
            labelSize = 16,
            onClick = {
                funFact = funFacts.random()
            },
            backgroundColor = Color.White,
            shadowColor = KanaColors.learningSecondary.copy(0.6f),
            contentColor = KanaColors.onOverlayedContainer,
            cornerRadius = 20
        )
        Spacer( Modifier.height(30.dp))
        Text(
            text = funFact,
            style = KanaSenseiTypography.bodyMedium.copy(
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
                color = Color.White.copy(0.6f),
                textAlign = TextAlign.Center
            )
        )

    }
}



val funFacts = listOf(
    "Hiragana was developed over 1,000 years ago from simplified Chinese characters.",
    "Katakana was originally created by Buddhist monks as shorthand symbols.",
    "Japanese children first learn Hiragana before Katakana and Kanji.",
    "The kana 'ん' is the only Japanese kana without a vowel sound.",
    "Japanese has fewer sounds than English, making pronunciation very rhythmic.",
    "The word 'karaoke' means 'empty orchestra' in Japanese.",
    "In Japanese, family names usually come before first names.",
    "The kana 'あ' evolved from an ancient Chinese character.",
    "Japanese writing can be written both vertically and horizontally.",
    "Anime subtitles often use Hiragana to help beginners read Kanji.",
    "There are 46 basic Hiragana characters.",
    "There are 46 basic Katakana characters.",
    "Japanese convenience stores are called 'konbini'.",
    "Mount Fuji is the tallest mountain in Japan.",
    "Ramen originally came from Chinese noodle dishes.",
    "Sushi originally began as a way to preserve fish.",
    "Japanese trains are famous for being extremely punctual.",
    "The torii gate symbolizes the entrance to a sacred place.",
    "Sakura blossoms only bloom for a short time each year.",
    "In Japan, slurping noodles is considered polite.",
    "Japanese has no spaces between words.",
    "Manga is usually read from right to left.",
    "Japanese people often bow while greeting each other.",
    "The lucky cat statue is called 'Maneki-neko'.",
    "The kana 'の' is often used in Japanese logos because of its beautiful shape.",
    "Japanese calligraphy is known as 'Shodō'.",
    "Some Japanese words have no direct English translation.",
    "The Japanese alphabet sounds stay very consistent unlike English.",
    "Bento boxes are designed to make meals visually beautiful.",
    "The word 'emoji' originally comes from Japanese.",
    "Japanese shrines often have fox statues as protectors.",
    "Traditional Japanese paper is called 'washi'.",
    "Japanese tea ceremonies focus on harmony and mindfulness.",
    "Ninjas historically worked as spies more than fighters.",
    "Japanese gardens are designed to create calm and balance.",
    "The kana 'し' can be pronounced differently in some dialects.",
    "Origami means folded paper.",
    "Japanese vending machines can sell hot meals and umbrellas.",
    "The sound 'R' in Japanese is between an English R and L.",
    "Some Japanese festivals use giant illuminated lanterns.",
    "The Japanese flag represents the rising sun.",
    "Learning kana first makes Kanji much easier later.",
    "The kana 'つ' is one of the hardest for beginners to pronounce correctly.",
    "Japanese uses three writing systems together: Hiragana, Katakana, and Kanji.",
    "Samurai followed a code called 'Bushidō'.",
    "Many Japanese words are inspired by nature and seasons.",
    "Japanese New Year is one of the country’s most important celebrations.",
    "Matcha tea is made from finely ground green tea leaves.",
    "In Japan, shoes are removed before entering many homes.",
    "The famous Shinkansen trains are called bullet trains.",
    "Japanese rhythm often follows equal-timed syllables called mora.",
    "The kana 'へ' is pronounced differently when used as a particle.",
    "Tokyo is one of the largest cities in the world.",
    "Traditional Japanese lanterns are called 'chōchin'.",
    "Japanese myths include magical creatures called 'yōkai'.",
    "Japanese temples and shrines are different types of sacred places.",
    "The word 'sensei' can mean teacher, doctor, or master.",
    "Many Japanese signs mix Hiragana, Katakana, and Kanji together.",
    "Japanese pronunciation is considered one of the most consistent languages to read aloud.",
    "The kana 'を' is mainly used as a grammar particle in modern Japanese.",
    "Writing kana repeatedly helps build muscle memory faster."
)