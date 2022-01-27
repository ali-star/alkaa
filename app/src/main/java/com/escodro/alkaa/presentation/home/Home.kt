package com.escodro.alkaa.presentation.home

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomAppBar
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.escodro.alkaa.model.HomeSection
import com.escodro.category.presentation.list.CategoryListSection
import com.escodro.designsystem.AlkaaTheme
import com.escodro.preference.presentation.PreferenceSection
import com.escodro.search.presentation.SearchSection
import com.escodro.task.presentation.list.TaskListSection

/**
 * Alkaa Home screen.
 */
@Composable
fun Home(
    onTaskClick: (Long) -> Unit,
    onAboutClick: () -> Unit,
    onTrackerClick: () -> Unit,
    onTaskSheetOpen: () -> Unit,
    onCategorySheetOpen: (Long?) -> Unit
) {
    val (currentSection, setCurrentSection) = rememberSaveable { mutableStateOf(HomeSection.Tasks) }
    val navItems = HomeSection.values().toList()
    val homeModifier = Modifier.padding(bottom = 56.dp)

    val actions = HomeActions(
        onTaskClick = onTaskClick,
        onAboutClick = onAboutClick,
        onTrackerClick = onTrackerClick,
        onTaskSheetOpen = onTaskSheetOpen,
        onCategorySheetOpen = onCategorySheetOpen,
        setCurrentSection = setCurrentSection,
    )

    Crossfade(currentSection) { homeSection ->
        AlkaaHomeScaffold(
            homeSection = homeSection,
            modifier = homeModifier,
            navItems = navItems,
            actions = actions
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun AlkaaHomeScaffold(
    homeSection: HomeSection,
    modifier: Modifier,
    navItems: List<HomeSection>,
    actions: HomeActions
) {
    Scaffold(
        topBar = {
            AlkaaTopBar(currentSection = homeSection)
        },
        content = {
            AlkaaContent(homeSection, modifier, actions)
        },
        bottomBar = {
            AlkaaBottomNav(
                currentSection = homeSection,
                onSectionSelect = actions.setCurrentSection,
                items = navItems
            )
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun AlkaaContent(
    homeSection: HomeSection,
    modifier: Modifier,
    actions: HomeActions,
) {
    when (homeSection) {
        HomeSection.Tasks ->
            TaskListSection(
                modifier = modifier,
                onItemClick = actions.onTaskClick,
                onBottomShow = actions.onTaskSheetOpen
            )
        HomeSection.Search ->
            SearchSection(modifier = modifier, onItemClick = actions.onTaskClick)
        HomeSection.Categories ->
            CategoryListSection(
                modifier = modifier,
                onShowBottomSheet = actions.onCategorySheetOpen

            )
        HomeSection.Settings ->
            PreferenceSection(
                modifier = modifier,
                onAboutClick = actions.onAboutClick,
                onTrackerClick = actions.onTrackerClick
            )
    }
}

@Composable
private fun AlkaaTopBar(currentSection: HomeSection) {
    TopAppBar(backgroundColor = MaterialTheme.colors.background, elevation = 0.dp) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.h5,
                text = stringResource(currentSection.title)
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun AlkaaBottomNav(
    currentSection: HomeSection,
    onSectionSelect: (HomeSection) -> Unit,
    items: List<HomeSection>
) {
    BottomAppBar(backgroundColor = MaterialTheme.colors.background) {
        items.forEach { section ->
            val selected = section == currentSection
            val colorState = animateColorAsState(
                if (selected) {
                    MaterialTheme.colors.primary
                } else {
                    MaterialTheme.colors.onSecondary
                }
            )
            AlkaaBottomIcon(
                section = section,
                tint = colorState.value,
                onSectionSelect = onSectionSelect,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun AlkaaBottomIcon(
    section: HomeSection,
    tint: Color,
    onSectionSelect: (HomeSection) -> Unit,
    modifier: Modifier
) {
    val title = stringResource(section.title)
    IconButton(
        onClick = { onSectionSelect(section) },
        modifier = modifier
    ) {
        Icon(imageVector = section.icon, tint = tint, contentDescription = title)
    }
}

@Suppress("UndocumentedPublicFunction")
@Preview(showBackground = true)
@Composable
fun AlkaaTopBarPreview() {
    AlkaaTheme {
        AlkaaTopBar(HomeSection.Tasks)
    }
}

@Suppress("UndocumentedPublicFunction")
@Preview(showBackground = true)
@Composable
fun AlkaaBottomNavPreview() {
    AlkaaTheme {
        AlkaaBottomNav(
            currentSection = HomeSection.Tasks,
            onSectionSelect = {},
            items = HomeSection.values().toList()
        )
    }
}
