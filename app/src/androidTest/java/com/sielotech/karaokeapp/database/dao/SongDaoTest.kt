package com.sielotech.karaokeapp.database.dao

import androidx.test.filters.SmallTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@SmallTest
class SongDaoTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Test
    fun getSongsFlow_should_return_a_flow_containing_a_list_of_songs() {

    }
}