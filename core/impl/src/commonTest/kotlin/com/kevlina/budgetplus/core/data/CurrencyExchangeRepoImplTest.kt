package com.kevlina.budgetplus.core.data

import androidx.datastore.preferences.core.stringPreferencesKey
import com.kevlina.budgetplus.core.common.Currency
import com.kevlina.budgetplus.core.data.fixtures.FakeBookRepo
import com.kevlina.budgetplus.core.data.fixtures.FakePreference
import com.kevlina.budgetplus.core.data.remote.Book
import com.kevlina.budgetplus.core.data.remote.Record
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CurrencyExchangeRepoImplTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `onAppStart refreshes rates`() = runTest {
        val mockEngine = createMockEngine(
            "usd" to """{"usd": {"eur": 0.9}}"""
        )
        val repo = createRepo(mockEngine = mockEngine)

        repo.onAppStart()
        
        // Wait for potential async refresh
        repo.exchangeRateChange.first()

        assertEquals("USD", repo.preferredCurrencyCode)
    }

    @Test
    fun `updatePreferredCurrency updates preference and refreshes rates`() = runTest {
        val mockEngine = createMockEngine(
            "eur" to """{"eur": {"usd": 1.1}}"""
        )
        val repo = createRepo(mockEngine = mockEngine)

        repo.updatePreferredCurrency(Currency(name = "Euro", currencyCode = "EUR", symbol = "€"))

        repo.exchangeRateChange.first()

        assertEquals("EUR", repo.preferredCurrencyCode)
    }

    @Test
    fun `formatPreferredCurrency returns null when currencies match`() = runTest {
        val repo = createRepo(bookCurrency = "USD")
        // Preferred is USD by default (from getDefaultCurrencyCode mock or fallback)
        
        val result = repo.formatPreferredCurrency(100.0, false)

        assertNull(result)
    }

    @Test
    fun `formatPreferredCurrency converts price when rates are available`() = runTest {
        val mockEngine = createMockEngine(
            "usd" to """{"usd": {"eur": 0.5}}"""
        )
        val repo = createRepo(mockEngine = mockEngine, bookCurrency = "EUR")
        
        repo.onAppStart()
        repo.exchangeRateChange.first()

        // book is EUR, preferred is USD. rate from USD to EUR is 0.5.
        // price in EUR is 100.0. converted to USD: 100.0 / 0.5 = 200.0
        val result = repo.formatPreferredCurrency(100.0, false)

        assertContains(result!!, "200")
    }

    @Test
    fun `toggleDisplayInPreferredCurrency toggles state`() = runTest {
        val repo = createRepo()
        assertFalse(repo.displayInPreferredCurrency.value)
        
        repo.toggleDisplayInPreferredCurrency()
        assertTrue(repo.displayInPreferredCurrency.value)
        
        repo.toggleDisplayInPreferredCurrency()
        assertFalse(repo.displayInPreferredCurrency.value)
    }

    @Test
    fun `refreshRate uses fallback when primary fails`() = runTest {
        var callCount = 0
        val mockEngine = MockEngine { request ->
            callCount++
            if (request.url.host.contains("jsdelivr")) {
                respond("Error", status = HttpStatusCode.InternalServerError)
            } else {
                respond(
                    content = """{"usd": {"eur": 0.9}}""",
                    status = HttpStatusCode.OK,
                    headers = headersOf("Content-Type", "application/json")
                )
            }
        }
        
        val repo = createRepo(mockEngine = mockEngine, bookCurrency = "EUR")
        repo.onAppStart()
        repo.exchangeRateChange.first()
        
        assertEquals(2, callCount)
        // Verify it actually worked by checking conversion
        val result = repo.formatPreferredCurrency(1.0, false)

        assertNotNull(result)
    }

    @Test
    fun `getDisplayPrice returns the book price when displaying in book currency`() = runTest {
        val repo = createRepoWithRate()

        val record = Record(price = 50.0, preferredPrice = 100.0, preferredCurrencyCode = "USD")

        assertEquals(50.0, repo.getDisplayPrice(record))
    }

    @Test
    fun `getDisplayPrice uses the recorded preferred price to avoid a doubled conversion`() = runTest {
        val repo = createRepoWithRate()
        repo.toggleDisplayInPreferredCurrency()

        // The record was created with 100 USD when the rate was 0.55, so a naive conversion
        // of the book price with the current rate (0.5) would incorrectly yield 110 USD.
        val record = Record(price = 55.0, preferredPrice = 100.0, preferredCurrencyCode = "USD")

        assertEquals(100.0, repo.getDisplayPrice(record))
    }

    @Test
    fun `getDisplayPrice converts the book price for records created in the book currency`() = runTest {
        val repo = createRepoWithRate()
        repo.toggleDisplayInPreferredCurrency()

        val record = Record(price = 50.0)

        // book is EUR, preferred is USD. rate from USD to EUR is 0.5. 50 EUR = 100 USD.
        assertEquals(100.0, repo.getDisplayPrice(record))
    }

    @Test
    fun `getDisplayPrice converts the book price when the record currency differs from the preferred one`() = runTest {
        val repo = createRepoWithRate()
        repo.toggleDisplayInPreferredCurrency()

        val record = Record(price = 50.0, preferredPrice = 80.0, preferredCurrencyCode = "GBP")

        assertEquals(100.0, repo.getDisplayPrice(record))
    }

    @Test
    fun `getDisplayPrice falls back to the book price when the rate is unresolved`() = runTest {
        val repo = createRepo(bookCurrency = "EUR")
        repo.toggleDisplayInPreferredCurrency()

        val record = Record(price = 50.0)

        assertEquals(50.0, repo.getDisplayPrice(record))
    }

    @Test
    fun `formatDisplayPrice formats with the preferred currency only when the toggle is on`() = runTest {
        val repo = createRepoWithRate()

        val bookFormat = repo.formatDisplayPrice(100.0)
        repo.toggleDisplayInPreferredCurrency()
        val preferredFormat = repo.formatDisplayPrice(100.0)

        assertContains(bookFormat, "100")
        assertContains(preferredFormat, "100")
        // EUR for the book currency, USD for the preferred currency.
        assertNotEquals(bookFormat, preferredFormat)
    }

    /**
     * @return A repo where the book currency is EUR, the preferred currency is USD, and the
     *  rate from USD to EUR is 0.5.
     */
    private suspend fun TestScope.createRepoWithRate(): CurrencyExchangeRepoImpl {
        val mockEngine = createMockEngine(
            "usd" to """{"usd": {"eur": 0.5}}"""
        )
        val repo = createRepo(mockEngine = mockEngine, bookCurrency = "EUR")
        repo.onAppStart()
        repo.exchangeRateChange.first()
        return repo
    }

    private fun createMockEngine(vararg rates: Pair<String, String>): MockEngine {
        return MockEngine { request ->
            val content = rates.find { (code, _) -> request.url.encodedPath.contains(code) }?.second
            if (content != null) {
                respond(
                    content = content,
                    status = HttpStatusCode.OK,
                    headers = headersOf("Content-Type", "application/json")
                )
            } else {
                respond("Not Found", status = HttpStatusCode.NotFound)
            }
        }
    }

    private fun TestScope.createRepo(
        mockEngine: MockEngine = createMockEngine(),
        bookCurrency: String = "USD",
    ): CurrencyExchangeRepoImpl {
        val httpClient = HttpClient(mockEngine)
        val preference = FakePreference {
            set(stringPreferencesKey("preferredCurrencyCode"), "USD")
        }
        return CurrencyExchangeRepoImpl(
            bookRepo = FakeBookRepo(book = Book(currencyCode = bookCurrency)),
            preference = preference,
            json = json,
            appScope = backgroundScope,
            httpClient = httpClient
        )
    }
}
