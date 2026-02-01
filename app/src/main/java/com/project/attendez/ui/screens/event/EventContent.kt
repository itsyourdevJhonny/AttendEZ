package com.project.attendez.ui.screens.event

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.project.attendez.R
import com.project.attendez.data.local.entity.EventEntity
import com.project.attendez.ui.theme.BackgroundGradient
import com.project.attendez.ui.theme.BluePrimary
import com.project.attendez.ui.theme.BlueSecondary
import com.project.attendez.ui.util.drawGradient
import com.project.attendez.viewmodel.EventViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventContent(
    paddingValues: PaddingValues,
    eventViewModel: EventViewModel,
    onEventClick: () -> Unit,
    onShowCreateDialog: (Boolean) -> Unit
) {
    val events by eventViewModel.events.collectAsState()
    val isLoading by eventViewModel.isLoading.collectAsState()

    val today = LocalDate.now()
    val ongoingEvents = events.filter { it.date >= today }
    val pastEvents = events.filter { it.date < today }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showDeleteSheet by remember { mutableStateOf(false) }
    var selectedEvent by remember { mutableStateOf<EventEntity?>(null) }

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
            Actions(onShowCreateDialog)

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = Color.White,
                thickness = 0.5.dp
            )

            Tabs(selectedTabIndex) { selectedTabIndex = it }
        }

        Column(
            modifier = Modifier.background(Color.White),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LabelIndicator(
                selectedTabIndex = selectedTabIndex,
                totalOngoing = ongoingEvents.size,
                totalPast = pastEvents.size
            )

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                        content = { CircularProgressIndicator(Modifier.drawGradient()) }
                    )
                }

                (selectedTabIndex == 0 && ongoingEvents.isEmpty()) || (selectedTabIndex == 1 && pastEvents.isEmpty()) -> {
                    EmptyAttendance()
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = if (selectedTabIndex == 0) ongoingEvents else pastEvents,
                            key = { it.id }
                        ) { event ->
                            EventItem(event, onEventClick) {
                                selectedEvent = event
                                showDeleteSheet = true
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDeleteSheet) {
        DeleteEventSheet(selectedEvent, eventViewModel) { showDeleteSheet = false }
    }
}

@Composable
private fun Actions(onShowCreateDialog: (Boolean) -> Unit) {
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
                onClick = {
                    if (label == "Create") {
                        onShowCreateDialog(true)
                    } else {
                        //
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteEventSheet(
    selectedEvent: EventEntity?,
    eventViewModel: EventViewModel,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss, dragHandle = null) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    selectedEvent?.let { event ->
                        eventViewModel.deleteEvent(event)
                        onDismiss()
                    }
                }
                .padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(Color.Red.copy(alpha = 0.6f), CircleShape)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White
                )
            }

            Text(text = " Delete Event", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun EmptyAttendance() {
    Box(
        modifier = Modifier.fillMaxSize(),
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
                text = "There are no attendance available at this time.",
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun LabelIndicator(selectedTabIndex: Int, totalOngoing: Int, totalPast: Int) {
    val prefix = if (selectedTabIndex == 0) "Ongoing" else "Past"
    val count = if (selectedTabIndex == 0) totalOngoing else totalPast

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(text = "$prefix Event${if (count > 1) "s" else ""} ($count)", color = Color.Black)

        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            modifier = Modifier.drawGradient()
        )
    }
}

@Composable
private fun CreateAction(label: String, @DrawableRes iconRes: Int, onClick: () -> Unit) {
    Column(
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
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Image(
                        painter = painterResource(icon),
                        contentDescription = "Icon",
                        modifier = Modifier.size(28.dp)
                    )

                    Text(
                        text = " $label",
                        color = BluePrimary,
                        fontWeight = FontWeight.Black
                    )
                }
            }
    }
}

@Composable
fun LazyItemScope.EventItem(event: EventEntity, onEventClick: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .combinedClickable(onClick = onEventClick, onLongClick = onDelete)
            .background(BlueSecondary.copy(alpha = 0.3f), CircleShape)
            .fillMaxWidth()
            .padding(8.dp)
            .animateItem(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
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
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.widthIn(max = 216.dp)
            )
        }

        Icon(imageVector = Icons.Default.Delete, contentDescription = "More", tint = Color.Red)
    }
}