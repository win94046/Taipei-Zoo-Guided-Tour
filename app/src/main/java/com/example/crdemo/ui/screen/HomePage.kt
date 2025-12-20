package com.example.crdemo.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.crdemo.R
import com.example.crdemo.ui.component.AnimalItem
import com.example.crdemo.viewmodel.SettingsViewModel
import com.example.crdemo.viewmodel.ZooViewModel

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(){
    val viewModel: ZooViewModel = hiltViewModel()
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val allAnimals = viewModel.allAnimals.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        // 取得動物、植物與展覽資料
        settingsViewModel.changeTaipeiZooUrl()
        viewModel.refreshAllData()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { "CRDemo" },
                modifier = Modifier
                    .height(50.dp)
                    .background(
                        color = Color.Red
                    )
            )
        },
    ){ padding ->
        Box(modifier = Modifier.padding(padding)){
            LazyColumn{
                items(
                    allAnimals.value.size,
                    key = {allAnimals.value[it].id}
                ){
                    AnimalItem(
                        animal = allAnimals.value[it],
                        onClick = {
//                            viewModel.onItemClicked(ListType.ANIMAL, it.id)
                        }
                    )
                }
            }
        }
    }
}

