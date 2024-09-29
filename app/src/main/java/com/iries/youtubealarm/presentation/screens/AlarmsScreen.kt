package com.iries.youtubealarm.presentation.screens

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.iries.youtubealarm.data.entity.AlarmInfo
import com.iries.youtubealarm.domain.AlarmManager
import com.iries.youtubealarm.presentation.common.AlarmItem
import com.iries.youtubealarm.presentation.common.DatePicker
import com.iries.youtubealarm.presentation.viewmodels.AlarmsViewModel

@Composable
fun AlarmsScreen(
    context: Context
) {
    val viewModel: AlarmsViewModel = hiltViewModel()
    var showDialog by remember { mutableStateOf(false) }
    val alarmsList = viewModel.getAllAlarms().observeAsState()
    val selectedAlarm: MutableState<AlarmInfo?> = remember { mutableStateOf(null) }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(
            onClick = { showDialog = true },
            modifier = Modifier
                .size(60.dp),
            shape = CircleShape,
            contentPadding = PaddingValues(0.dp)
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Add alarm"
            )
        }

        Spacer(Modifier.padding(0.dp, 20.dp))

        if (showDialog) DatePicker(
            onCloseDialog = { showDialog = false },
            onConfirm = {
                if (alarmsList.value?.contains(it) == true)
                    viewModel.update(it)
                else
                    viewModel.insert(context, it)
            },
            selectedAlarm.value
        )

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            alarmsList.value?.let { alarms ->
                items(alarms.toList()) { alarm ->
                    AlarmItem(
                        alarm = alarm,
                        onRemoveAlarm = {
                            viewModel.remove(context, alarm)
                        },
                        onEditAlarm = {
                            selectedAlarm.value = alarm
                            showDialog = true
                        },
                        onSwitchAlarm = {
                            if (alarm.isActive()) {
                                AlarmManager.stopAlarm(context)
                                // no need to stop all alarms
                                viewModel.stopAlarms(context, alarm.getDaysId())
                            } else
                                viewModel.setRepeatingAlarm(context, alarm)
                            alarm.setActive(it)
                            viewModel.update(alarm)
                        }
                    )
                }
            }
        }

    }
}








