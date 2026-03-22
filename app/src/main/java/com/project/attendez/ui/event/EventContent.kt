package com.project.attendez.ui.event

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.project.attendez.R
import com.project.attendez.data.local.entity.EventEntity
import com.project.attendez.ui.theme.BackgroundGradient
import com.project.attendez.ui.theme.BluePrimary
import com.project.attendez.ui.theme.Typography
import com.project.attendez.ui.util.drawGradient
import com.project.attendez.viewmodel.EventViewModel
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventContent(
    paddingValues: PaddingValues,
    eventViewModel: EventViewModel,
    selectedTabIndex: Int,
    ongoingEvents: List<EventEntity>,
    pastEvents: List<EventEntity>,
    filteredEvents: List<EventEntity>,
    selectedEvent: EventEntity?,
    isLoading: Boolean,
    showSheet: Boolean,
    onTabSelected: (Int) -> Unit,
    onEventSelected: (EventEntity) -> Unit,
    onShowSheet: (Boolean) -> Unit,
    onEventClick: (Long) -> Unit,
    onHistory: () -> Unit,
    onCreate: () -> Unit,
    onSearching: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = BackgroundGradient,
                    shape = RoundedCornerShape(32.dp),
                    alpha = 0.7f
                )
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Actions(onCreate, onHistory)

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = Color.White,
                thickness = 0.5.dp
            )

            Tabs(selectedTabIndex, onTabSelected)
        }

        Column(
            modifier = Modifier.background(Color.White),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            EventsHeader(
                selectedTabIndex = selectedTabIndex,
                totalOngoing = ongoingEvents.size,
                totalPast = pastEvents.size,
                onSearching = onSearching
            )

            AnimatedVisibility(visible = (selectedTabIndex == 0 && ongoingEvents.isEmpty()) || (selectedTabIndex == 1 && pastEvents.isEmpty())) {
                EmptyAttendance(label = "There are no ${if (selectedTabIndex == 0) "ongoing" else "past"} events available at this time.")
            }

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                        content = { CircularProgressIndicator(Modifier.drawGradient()) }
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(items = filteredEvents, key = { it.id }) { event ->
                            EventItem(event, onEventClick) {
                                onEventSelected(event)
                                onShowSheet(true)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showSheet) {
        EventSheet(selectedEvent, eventViewModel) { onShowSheet(false) }
    }
}

@Composable
private fun Actions(onCreate: () -> Unit, onHistory: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        listOf(
            "Create" to R.drawable.create,
            "History" to R.drawable.history
        ).forEach { (label, iconRes) ->
            CreateAction(
                label = label,
                iconRes = iconRes,
                onClick = { if (label == "Create") onCreate() else onHistory() }
            )
        }
    }
}

@Composable
fun EmptyAttendance(
    label: String = "There are no attendance available at this time.",
    isFullSize: Boolean = true,
) {
    val modifier = if (isFullSize) Modifier.fillMaxSize() else Modifier

    Box(
        modifier = modifier.padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.empty_attendance),
                contentDescription = "Empty",
                modifier = Modifier.size(58.dp)
            )

            Text(
                text = label,
                textAlign = TextAlign.Center,
                color = Color.Black
            )
        }
    }
}


@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun EventsHeader(
    selectedTabIndex: Int,
    totalOngoing: Int,
    totalPast: Int,
    onSearching: (Boolean) -> Unit,
) {

    val prefix = if (selectedTabIndex == 0) "Ongoing" else "Past"
    val count = if (selectedTabIndex == 0) totalOngoing else totalPast

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "$prefix Event${if (count != 1) "s" else ""} ($count)",
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        IconButton(onClick = { onSearching(true) }) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                modifier = Modifier.drawGradient()
            )
        }
    }
}

@Composable
private fun CreateAction(label: String, @DrawableRes iconRes: Int, onClick: () -> Unit) {
    /*Column(
        modifier = Modifier
            .clickable { onClick() }
            .width(100.dp)
            .background(Color.White, CircleShape)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = label,
            modifier = Modifier.size(38.dp)
        )

        Text(text = label, color = Color.Black, fontWeight = FontWeight.Bold)
    }*/

    Button(
        onClick = onClick,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .width(100.dp)
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = label,
                modifier = Modifier.size(38.dp)
            )

            Text(text = label, color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun Tabs(selectedTabIndex: Int, onTabSelected: (Int) -> Unit) {
    TabRow(
        selectedTabIndex = selectedTabIndex,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                color = BluePrimary
            )
        },
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clip(CircleShape)
    ) {
        listOf("Ongoing" to R.drawable.ongoing, "Past" to R.drawable.past)
            .forEachIndexed { index, (label, icon) ->
                Row(
                    modifier = Modifier
                        .clickable { onTabSelected(index) }
                        .background(Color.White)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Image(
                        painter = painterResource(icon),
                        contentDescription = "Icon",
                        modifier = Modifier.size(28.dp)
                    )

                    Text(text = " $label", color = BluePrimary, fontWeight = FontWeight.Black)
                }
            }
    }
}

@Composable
fun LazyItemScope.EventItem(
    event: EventEntity,
    onEventClick: (Long) -> Unit,
    onOpenSheet: () -> Unit,
) {
    val eventColor = Color(event.color.toColorInt())
    val totalDays = ChronoUnit.DAYS.between(event.startDate, event.endDate).toInt() + 1

    Column(
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onEventClick(event.id) },
                    onLongPress = { onOpenSheet() }
                )
            }
            .background(
                brush = Brush.verticalGradient(listOf(eventColor, eventColor.copy(alpha = 0.7f))),
                shape = RoundedCornerShape(16.dp)
            )
            .fillMaxWidth()
            .padding(8.dp)
            .animateItem(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.name),
                contentDescription = "Name",
                modifier = Modifier.size(32.dp)
            )

            Text(
                text = event.name,
                color = Color.White,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            EventStatus(event)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "${event.startDate} - ${event.endDate}",
                style = Typography.labelMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(text = "/", style = Typography.titleLarge, color = Color.White)
            Text(
                text = "$totalDays Day${if (totalDays > 1) "s" else ""}",
                style = Typography.labelMedium,
                color = Color.White,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
fun EventStatus(event: EventEntity) {

    val started = event.lastAttendanceDate != null

    val infiniteTransition = rememberInfiniteTransition(label = "")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (started) 1.4f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (started) 0.4f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Box(
            modifier = Modifier
                .size(16.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    this.alpha = alpha
                }
                .background(
                    color = if (started) Color.Green else Color.Red,
                    shape = CircleShape
                )
        )

        Text(
            text = if (started) "Started" else "Ongoing",
            style = Typography.bodyMedium.copy(color = Color.White)
        )
    }
}