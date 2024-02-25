import android.content.Context
import android.graphics.Bitmap

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.perceive.data.local.bitmapstore.AndroidBitmapStore
import junit.framework.TestCase.assertNull

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class AndroidBitmapStoreTest {

    private lateinit var bitmapStore: AndroidBitmapStore
    private lateinit var context: Context
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        bitmapStore = AndroidBitmapStore(context, testDispatcher)
    }

    @Test
    fun saveRetrieveAndDeleteTest() = runTest(testDispatcher) {
        // given a test bitmap
        val testBitmap = Bitmap.createBitmap(101, 106, Bitmap.Config.ARGB_8888)

        // the bitmap must be saved successfully
        val uri = bitmapStore.saveBitmap(testBitmap)!!
        advanceUntilIdle()

        // if the bitmap was saved successfully, it must be also be retrievable
        val retrievedBitmap = bitmapStore.retrieveBitmapForUri(uri)!!
        advanceUntilIdle()
        assert(retrievedBitmap.width == testBitmap.width)
        assert(retrievedBitmap.height == testBitmap.height)

        // if the bitmap is deleted
        bitmapStore.deleteBitmapWithUri(uri)
        // trying to retrieve it must return null
        assertNull(bitmapStore.retrieveBitmapForUri(uri))
    }


}
