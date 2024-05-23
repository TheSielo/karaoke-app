package com.sielotech.karaokeapp.database

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.sielotech.karaokeapp.database.entity.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class RemoteSongsDataSourceTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Mock
    private lateinit var firebaseAuth: FirebaseAuth
    @Mock
    private lateinit var database: FirebaseDatabase
    @Mock
    private lateinit var databaseReference: DatabaseReference

    private lateinit var remoteSongsDataSource: RemoteSongsDataSource


    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        //backgroundScope is used because the data source will start a non-terminating coroutines
        remoteSongsDataSource = RemoteSongsDataSource(firebaseAuth, database, testScope)

        val firebaseUser = mock(FirebaseUser::class.java)
        whenever(firebaseUser.uid).thenReturn("testUserId")
        whenever(firebaseAuth.currentUser).thenReturn(firebaseUser)

        whenever(database.getReference(anyString())).thenReturn(databaseReference)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initialize should trigger a remoteSongsFlow update when user is logged in`() = testScope.runTest {
        /* create a map with a fake song */
        val song = Song(
            1, "8d9623db-b28b-4479-ac24-27d522e04487", "Title",
            "JapaneseText", "TranslatedText", "https://example.com"
        )
        val songJson = Gson().toJson(song)
        val songsMap = mapOf("testSongId" to songJson)

        /* Mock a dataSnapshot and make it return the fake map */
        val dataSnapshot = mock<DataSnapshot>()
        whenever(dataSnapshot.value).thenReturn(songsMap)

        /* Initialize the data source. This triggers a call to getRemoteSongs and a new snapshot
        will be received. */
        remoteSongsDataSource.initialize()
        //Wait for addValueEventListener to execute
        advanceUntilIdle()

        /* Build an ArgumentCaptor to capture the ValueEventListener */
        val valueEventListenerCaptor = ArgumentCaptor.forClass(ValueEventListener::class.java)
        verify(databaseReference).addValueEventListener(valueEventListenerCaptor.capture())

        //Trigger onDataChanged manually
        valueEventListenerCaptor.value.onDataChange(dataSnapshot)

        /* onDataChanged will parse the snapshot and update remoteSongsFlow. Take the first update */
        val result = remoteSongsDataSource.remoteSongsFlow.first()

        // Check that the flow update contains the songs we are expecting
        assert(result.isNotEmpty())
        assert(result[0].id == 1)
        assert(result[0].uuid == "8d9623db-b28b-4479-ac24-27d522e04487")
        assert(result[0].title == "Title")
    }

    @Test
    fun `addOrUpdate should upload a song if the user is logged in`() = testScope.runTest {
        /* Mock a song and verify that the databaseReference value is set. */
        val song = mock<Song>()
        remoteSongsDataSource.addOrUpdate(song)
        verify(databaseReference).setValue(Gson().toJson(song))
    }
}