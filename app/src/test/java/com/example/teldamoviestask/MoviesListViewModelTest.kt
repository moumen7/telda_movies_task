import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.teldamoviestask.data.local.FavoritesRepository
import com.example.teldamoviestask.data.remote.MoviesRepository
import com.example.teldamoviestask.model.Movie
import com.example.teldamoviestask.model.MovieResponse
import com.example.teldamoviestask.model.Resource
import com.example.teldamoviestask.ui.movies_list.MoviesListViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class MoviesListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var moviesRepository: MoviesRepository

    @Mock
    private lateinit var favoritesRepository: FavoritesRepository

    private lateinit var viewModel: MoviesListViewModel
    private val popularMoviesMock = listOf(
        Movie(
            "backdrop_path",
            12,
            "Her",
            "A nice Movie",
            "posterPath",
            "2022",
            "Her",
            3.7
        )
    )
    private val popularMoviesMockResponse = MovieResponse(
        page = 1,
        results = popularMoviesMock,
        total_pages = 100,
        total_results = 100
    )

    @Before
    fun setUp() {
        // Initialize the ViewModel with mocked dependencies
        Dispatchers.setMain(StandardTestDispatcher())
        viewModel = MoviesListViewModel(moviesRepository, favoritesRepository)
    }


    @Test
    fun `getPopularMovies updates movies`() {
        runTest {
            whenever(moviesRepository.getPopularMovies()).thenReturn(Resource.Success(popularMoviesMockResponse))

            viewModel.fetchPopularMovies()
            advanceUntilIdle()

            assertTrue(viewModel.movies.value is Resource.Success)
            assertEquals(
                popularMoviesMockResponse.results,
                (viewModel.movies.value as Resource.Success<List<Movie>>).data
            )
        }
    }

    @Test
    fun `toggleFavorite updates favorites correctly`() = runTest {
        // initially no favorites
        viewModel.favorites.value?.let { assert(it.isEmpty()) }
        val movieId = 1
        viewModel.toggleFavorite(movieId, false)
        advanceUntilIdle()

        // Verify - Check if favorites LiveData is updated correctly
        val favorites = viewModel.favorites.value
        advanceUntilIdle()
        assert(!favorites.isNullOrEmpty())
        assert(favorites!!.contains(movieId))

        // Action - Toggle favorite off
        viewModel.toggleFavorite(movieId, true)
        advanceUntilIdle()

        // Verify - Check if favorites LiveData is updated correctly
        val updatedFavorites = viewModel.favorites.value
        assert(updatedFavorites!!.isEmpty())
    }

    @After
    fun tearDown() {
        // Reset the main dispatcher to the original Main dispatcher
        Dispatchers.resetMain()
    }

}
